package main;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gui.EmailSenderGUI;
import kwee.library.JarInfo;
import main.Main;

public class Main {
  static public String m_creationtime;
  static public String c_CopyrightYear;

  private UserSetting m_params = UserSetting.getInstance();

  public static void main(String[] args) {
    m_creationtime = JarInfo.getProjectVersion(Main.class);
    c_CopyrightYear = JarInfo.getYear(Main.class);

    SwingUtilities.invokeLater(() -> {
      try {
        // Gebruik system look and feel
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // Optioneel: verbeter de UI
        UIManager.put("TabbedPane.selected", new Color(66, 165, 245));
        UIManager.put("TabbedPane.selectHighlight", new Color(66, 165, 245));

      } catch (Exception e) {
        e.printStackTrace();
      }

      // Toon de GUI
      EmailSenderGUI gui = new EmailSenderGUI();
      gui.setVisible(true);
    });
  }
}
