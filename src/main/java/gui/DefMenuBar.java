package gui;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import kwee.library.ApplicationMessages;
import kwee.library.swing.AboutWindow;
import kwee.library.swing.ShowPreferences;
import kwee.logger.MyLogger;
import main.Main;
import main.UserSetting;

public class DefMenuBar {
  private static final Logger LOGGER = MyLogger.getLogger();

  static final String c_CopyrightYear = Main.c_CopyrightYear;
  static final String c_repoName = "emailsender";
  static final String m_HelpFile = "emailsender.chm";

  private String m_Language = "nl";
  private boolean m_toDisk = false;
  private Level m_Level = Level.INFO;
  private String m_LogDir = "c:/";

  private ApplicationMessages bundle = ApplicationMessages.getInstance();

  // Preferences
  private UserSetting m_param;

  // Loglevels: OFF SEVERE WARNING INFO CONFIG FINE FINER FINEST ALL
  static final String[] c_levels = { "OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL" };
  static final String[] c_LogToDisk = { "Yes", "No" };
  private int i = 0;

  public JMenuBar defineMenuBar(JFrame hoofdFrame) {
    m_param = UserSetting.getInstance();
    JMenuBar menuBar = new JMenuBar();
    JMenuItem mntmLoglevel = new JMenuItem(bundle.getMessage("Loglevel"));
    JMenuItem mntmLanguages = new JMenuItem(bundle.getMessage("Languages"));

    // Define Setting menu in menubalk:
    JMenu mnSettings = new JMenu(bundle.getMessage("Settings"));
    mnSettings.setEnabled(true);
    menuBar.add(mnSettings);

    // Language setting
    mntmLanguages.setHorizontalAlignment(SwingConstants.LEFT);
    mntmLanguages.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame(bundle.getMessage("Language"));
        String language = "nl";
        Set<String> l_languages = bundle.getTranslations();
        String[] la_languages = new String[l_languages.size()];
        i = 0;
        l_languages.forEach(lang -> {
          la_languages[i] = lang;
          i++;
        });

        language = (String) JOptionPane.showInputDialog(frame, bundle.getMessage("Language") + "?", "nl",
            JOptionPane.QUESTION_MESSAGE, null, la_languages, m_Language);
        if (language != null) {
          m_Language = language;
          m_param.set_Language(m_Language);
          m_param.save();

          bundle.changeLanguage(language);
          hoofdFrame.dispose(); // Dispose of the current GUI window or frame
          SwingUtilities.invokeLater(() -> new EmailSenderGUI());
        }
      }
    });
    mnSettings.add(mntmLanguages);

    // Option log level
    mntmLoglevel.setHorizontalAlignment(SwingConstants.LEFT);
    mntmLoglevel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame(bundle.getMessage("Loglevel"));
        String level = "";
        level = (String) JOptionPane.showInputDialog(frame, bundle.getMessage("Loglevel") + "?", "INFO",
            JOptionPane.QUESTION_MESSAGE, null, c_levels, m_Level.toString());
        if (level != null) {
          m_Level = Level.parse(level.toUpperCase());
          m_param.set_Level(m_Level);
          m_param.save();
          MyLogger.changeLogLevel(m_Level);
        }
      }
    });
    mnSettings.add(mntmLoglevel);

    // Add item Look and Feel
    JMenu menu = new JMenu(bundle.getMessage("LookAndFeel"));
    menu.setName("LookAndFeel");
    menu.setHorizontalAlignment(SwingConstants.LEFT);
    mnSettings.add(menu);

    // Get all the available look and feel that we are going to use for
    // creating the JMenuItem and assign the action listener to handle
    // the selection of menu item to change the look and feel.
    UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
    for (UIManager.LookAndFeelInfo lookAndFeelInfo : lookAndFeels) {
      JMenuItem item = new JMenuItem(lookAndFeelInfo.getName());
      item.addActionListener(event -> {
        try {
          // Set the look and feel for the frame and update the UI
          // to use a new selected look and feel.
          UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
          SwingUtilities.updateComponentTreeUI(hoofdFrame);
          m_param.set_LookAndFeel(lookAndFeelInfo.getClassName());

          // Pas het standaard lettertype aan voor ALLE UI-elementen
          Font customFont = new Font("Arial", Font.PLAIN, 16);
          for (Object key : UIManager.getDefaults().keySet()) {
            if (key instanceof String && ((String) key).endsWith(".font")) {
              UIManager.put(key, customFont);
            }
          }
        } catch (Exception e) {
          LOGGER.log(Level.WARNING, e.getMessage());
        }
      });
      menu.add(item);
    }

    // Option Logging to Disk
    JCheckBoxMenuItem mntmLogToDisk = new JCheckBoxMenuItem(bundle.getMessage("CreateLogfiles"));
    mntmLogToDisk.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = mntmLogToDisk.isSelected();
        if (selected) {
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fileChooser.setSelectedFile(new File(m_LogDir));
          int option = fileChooser.showOpenDialog(hoofdFrame);
          if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            LOGGER.log(Level.INFO, bundle.getMessage("LogFolder", file.getAbsolutePath()));
            m_LogDir = file.getAbsolutePath() + "/";
            m_param.set_LogDir(m_LogDir);
            m_param.set_toDisk(true);
            m_toDisk = selected;
            m_param.save();
          }
        } else {
          m_param.set_toDisk(false);
          m_toDisk = selected;
          m_param.save();
        }
        try {
          MyLogger.setup(m_Level, m_LogDir, m_toDisk);
        } catch (IOException es) {
          LOGGER.log(Level.SEVERE, Class.class.getName() + ": " + es.toString());
          es.printStackTrace();
        }
      }
    });
    mnSettings.add(mntmLogToDisk);

    JMenuItem mntmEmailConfig = new JMenuItem("Mailconfiguratie");
    mntmEmailConfig.setName("Mailconfiguratie directory");
    mntmEmailConfig.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        File l_mailConfigDir = new File(m_param.get_mailConfigDirectory());
        JFileChooser fileChooser = new JFileChooser(l_mailConfigDir);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setSelectedFile(l_mailConfigDir);
        int option = fileChooser.showOpenDialog(hoofdFrame);
        if (option == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          LOGGER.log(Level.INFO, "Mail configuratie directory: " + file.getAbsolutePath());
          l_mailConfigDir = file;
          m_param.set_mailConfigDirectory(l_mailConfigDir.getAbsolutePath());
          mntmEmailConfig.setText(l_mailConfigDir.getAbsolutePath());
        }
      }
    });
    mnSettings.add(mntmEmailConfig);

    // Option To BBC
    JCheckBoxMenuItem mntmToBBC = new JCheckBoxMenuItem(bundle.getMessage("ToBBC"));
    mntmToBBC.setState(m_param.is_toBCC());
    mntmToBBC.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = mntmToBBC.isSelected();
        if (selected) {
          m_param.set_toBCC(selected);
          m_param.save();
          LOGGER.log(Level.INFO, bundle.getMessage("ToBBCSel", Boolean.toString(selected)));
        } else {
          m_param.set_toBCC(selected);
          m_param.save();
          LOGGER.log(Level.INFO, bundle.getMessage("ToBBCSel", Boolean.toString(selected)));
        }
      }
    });
    mnSettings.add(mntmToBBC);

    // Option Timestamp in eml filename
    JCheckBoxMenuItem mntmTimestamp = new JCheckBoxMenuItem(bundle.getMessage("Timestamp"));
    mntmTimestamp.setState(m_param.is_TimeStamp());
    mntmTimestamp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = mntmTimestamp.isSelected();
        if (selected) {
          m_param.set_Timestamp(selected);
          m_param.save();
          LOGGER.log(Level.INFO, bundle.getMessage("TimestampSel", Boolean.toString(selected)));
        } else {
          m_param.set_Timestamp(selected);
          m_param.save();
          LOGGER.log(Level.INFO, bundle.getMessage("TimestampSel", Boolean.toString(selected)));
        }
      }
    });
    mnSettings.add(mntmTimestamp);

    // Option Preferences
    JMenuItem mntmPreferences = new JMenuItem(bundle.getMessage("Preferences"));
    mntmPreferences.setName("Preferences");
    mntmPreferences.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ShowPreferences showpref = new ShowPreferences(UserSetting.NodePrefName);
        showpref.showAllPreferences();
      }
    });
    mnSettings.add(mntmPreferences);

    JMenuItem mntmAllPreferences = new JMenuItem(bundle.getMessage("AllPreferences"));
    mntmAllPreferences.setName("AllPreferences");
    mntmAllPreferences.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ShowPreferences showpref = new ShowPreferences();
        showpref.showAllPreferences();
      }
    });
    mnSettings.add(mntmAllPreferences);

    // ? item
    JMenu mnHelpAbout = new JMenu("?");
    mnHelpAbout.setHorizontalAlignment(SwingConstants.RIGHT);
    menuBar.add(mnHelpAbout);

    // Help
    JMenuItem mntmHelp = new JMenuItem(bundle.getMessage("Help"));
    mntmHelp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        File helpFile = new File("help\\" + m_Language + "\\" + m_HelpFile);

        // Locate helpfile
        if (!helpFile.exists()) {
          helpFile = new File("app\\help\\" + m_Language + "\\" + m_HelpFile);
        }

        if (helpFile.exists()) {
          try {
            // Open the help file with the default viewer
            Desktop.getDesktop().open(helpFile);
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        } else {
          LOGGER.log(Level.INFO, bundle.getMessage("HelpFileNotFound", helpFile.getAbsolutePath()));
        }
      }
    });
    mnHelpAbout.add(mntmHelp);

    // About
    JMenuItem mntmAbout = new JMenuItem(bundle.getMessage("About"));
    mntmAbout.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        AboutWindow l_window = new AboutWindow(c_repoName, Main.m_creationtime, c_CopyrightYear);
        l_window.setVisible(true);
      }
    });
    mnHelpAbout.add(mntmAbout);

    return menuBar;
  }
}
