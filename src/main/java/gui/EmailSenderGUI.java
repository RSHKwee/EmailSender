package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import kwee.library.ApplicationMessages;
import kwee.library.JarInfo;
import kwee.logger.MyLogger;

import library.EmailService;
import library.EmlService;
import library.MailPersonalize;

import main.Main;
import main.UserSetting;

import models.AttachmentConfig;
import models.EmailRecipient;

import gui.panels.AttachmentsPanel;
import gui.panels.ConfigPanel;
import gui.panels.EmlStoragePanel;
import gui.panels.LogPanel;
import gui.panels.MessagePanel;
import gui.panels.RecipientsPanel;

public class EmailSenderGUI extends JFrame {
  /**
   * 
   */
  private static final long serialVersionUID = -1600749552354317771L;
  private UserSetting m_params = UserSetting.getInstance();
  private ApplicationMessages bundle = ApplicationMessages.getInstance();
  private static final Logger LOGGER = MyLogger.getLogger();
  private boolean b_StandAlone = true;

  // Services
  private EmailService emailService;
  private EmlService emlService;

  // Panels
  private ConfigPanel configPanel;
  private RecipientsPanel recipientsPanel;
  private MessagePanel messagePanel;
  private AttachmentsPanel attachmentsPanel;
  private EmlStoragePanel emlStoragePanel;
  private LogPanel logPanel;
  private JMenuBar menuBar = new JMenuBar();
  private JSplitPane splitPane;

  private List<EmailRecipient> admEmailRecipients = new ArrayList<EmailRecipient>();

  // Temporary storage for recipients until attachmentsPanel is initialized
  @SuppressWarnings("unused")
  private List<EmailRecipient> pendingRecipientsUpdate;

  // Nieuwe constructors
  public EmailSenderGUI() {
    this("", new ArrayList<>(), null, null, null);
  }

  public EmailSenderGUI(List<RecipientData> recipientData) {
    this("", recipientData, null, null, null);
  }

  public EmailSenderGUI(String a_title, List<RecipientData> recipientData, SMTPConfig smtpConfig,
      MessageConfig messageConfig, List<File> commonAttachments) {

    Main.m_creationtime = JarInfo.getProjectVersion(Main.class);
    Main.c_CopyrightYear = JarInfo.getYear(Main.class);

    String apptxt = bundle.getMessage("AppTitel", Main.m_creationtime, Main.c_CopyrightYear);
    if (a_title.isBlank()) {
      setTitle(apptxt);
    } else {
      setTitle(apptxt + ", " + a_title);
    }

    loadWindowProperties();
    // setSize(800, 450);
    // setLocationRelativeTo(null);

    DefMenuBar dmenu = new DefMenuBar();
    menuBar = dmenu.defineMenuBar(this);
    setJMenuBar(menuBar);

    // Initialize services
    logPanel = new LogPanel();
    emailService = new EmailService();
    emlService = new EmlService(logPanel::log);
    pendingRecipientsUpdate = new ArrayList<>();

    initComponents();

    // Gebruik DISPOSE_ON_CLOSE in plaats van EXIT_ON_CLOSE
    // setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        saveWindowProperties();
        m_params.save();

        if (b_StandAlone) {
          System.exit(0);
        } else {
          EmailSenderGUI.this.dispose();
        }
      }
    });

    // Configureer met parameters
    if (smtpConfig != null) {
      b_StandAlone = false;
      applySMTPConfig(smtpConfig);
    }

    if (recipientData != null && !recipientData.isEmpty()) {
      b_StandAlone = false;
      applyRecipientData(recipientData);
    }

    if (messageConfig != null) {
      b_StandAlone = false;
      String l_Subject = m_params.get_Subject();
      String l_Message = m_params.get_Message();
      if (!l_Subject.isBlank()) {
        messageConfig.subject = l_Subject;
      }
      if (!l_Message.isBlank()) {
        messageConfig.template = l_Message;
      }
      applyMessageConfig(messageConfig);
    }

    if (commonAttachments != null && !commonAttachments.isEmpty()) {
      b_StandAlone = false;
      applyCommonAttachments(commonAttachments);
    }

    logPanel.log("GUI geladen met " + (recipientData != null ? recipientData.size() : 0) + " ontvangers");
  }

  // Private helper methoden om configuratie toe te passen
  private void applySMTPConfig(SMTPConfig config) {
    configPanel.setSMTPConfig(config.host, config.port, config.username, config.password, config.cc, config.replyTo,
        config.alias);
  }

  private void applyMessageConfig(MessageConfig config) {
    messagePanel.setMessage(config.subject, config.template);
  }

  private void applyCommonAttachments(List<File> files) {
    AttachmentConfig attachmentConfig = attachmentsPanel.getAttachmentConfig();
    for (File file : files) {
      if (file.exists()) {
        attachmentConfig.addCommonAttachment(file);
      }
    }
    attachmentsPanel.updateAttachmentInfo();
  }

  private void applyRecipientData(List<RecipientData> recipientData) {
    List<EmailRecipient> recipients = new ArrayList<>();
    AttachmentConfig attachmentConfig = attachmentsPanel.getAttachmentConfig();

    for (RecipientData data : recipientData) {
      // Maak EmailRecipient aan
      EmailRecipient recipient = new EmailRecipient(data.email);
      recipient.setVoornaam(data.firstName);
      recipient.setAchternaam(data.lastName);
      recipient.setStraatHnr(data.street_housenr);
      recipient.setPostcode(data.zipcode);
      recipient.setPlaats(data.city);
      recipients.add(recipient);

      // Voeg persoonlijke bestanden toe
      if (data.personalFilePaths != null && !data.personalFilePaths.isEmpty()) {
        for (File filePath : data.personalFilePaths) {
          if (filePath.exists()) {
            attachmentConfig.addPersonalAttachment(recipient.getId(), filePath);
          } else {
            logPanel.log("Waarschuwing: Bestand niet gevonden: " + filePath);
          }
        }
      }
    }
    admEmailRecipients.addAll(recipients);

    // Update GUI
    recipientsPanel.setRecipients(recipients);
    attachmentsPanel.updateRecipientsList(recipients);
    attachmentsPanel.updateAttachmentInfo();
  }

  private void initComponents() {
    // Create panels in correct order
    configPanel = new ConfigPanel();
    messagePanel = new MessagePanel();
    attachmentsPanel = new AttachmentsPanel();
    emlStoragePanel = new EmlStoragePanel(configPanel);

    // Create recipients panel last, with delayed callback
    recipientsPanel = new RecipientsPanel(this::onRecipientsUpdated);

    // Main tabbed pane
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("📧 Configuratie", configPanel);
    tabbedPane.addTab("👥 Ontvangers", recipientsPanel);
    tabbedPane.addTab("✉️ Bericht", messagePanel);
    tabbedPane.addTab("📎 Bijlagen", attachmentsPanel);
    tabbedPane.addTab("💾 EML Opslag", emlStoragePanel);

    // Button panel
    JPanel buttonPanel = createButtonPanel();

    // Create a panel for the log and buttons
    JPanel logAndButtonPanel = new JPanel(new BorderLayout());
    logAndButtonPanel.add(logPanel, BorderLayout.CENTER);
    logAndButtonPanel.add(buttonPanel, BorderLayout.SOUTH);

    // Create split pane between tabbedPane and logPanel
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, logAndButtonPanel);
    splitPane.setResizeWeight(0.7); // 80% voor tabs, 20% voor log

    // SplitPane positie herstellen indien aanwezig
    if (splitPane != null) {
      int splitPosition = m_params.get_Pref_Split_Posion();
      if (splitPosition != -1) {
        splitPane.setDividerLocation(splitPosition);
      }
    }

    // Layout
    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
  }

  private void onRecipientsUpdated(List<EmailRecipient> recipients) {
    // If attachmentsPanel is not yet initialized, store the update
    if (attachmentsPanel == null) {
      pendingRecipientsUpdate = new ArrayList<>(recipients);
    } else {
      attachmentsPanel.updateRecipientsList(recipients);
    }
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new GridLayout(1, 6, 5, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JButton testBtn = createButton("🔌 Testen", new Color(245, 124, 0), e -> testConnection());
    JButton sendBtn = createButton("📤 Verzenden", new Color(46, 125, 50), e -> sendEmails());
    JButton saveBtn = createButton("💾 Opslaan EML", new Color(21, 101, 192), e -> saveAsEml());
    // JButton clearBtn = createButton("🧹 Wissen", new Color(158, 158, 158), e ->
    // clearAll());
    // JButton exitBtn = createButton("🚪 Afsluiten", new Color(198, 40, 40), e ->
    // dispose());

    panel.add(testBtn);
    panel.add(sendBtn);
    panel.add(saveBtn);
    // panel.add(clearBtn);
    // panel.add(exitBtn);

    return panel;
  }

  private JButton createButton(String text, Color color, java.awt.event.ActionListener action) {
    JButton button = new JButton(text);
    button.setBackground(color);
    button.setForeground(Color.BLACK);
    button.setFont(new Font("Arial", Font.BOLD, 12));
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    button.addActionListener(action);
    return button;
  }

  private void sendEmails() {
    // Controleer configuratie
    if (!validateConfiguration()) {
      return;
    }

    List<EmailRecipient> recipients = recipientsPanel.getRecipients();
    if (recipients.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Voer eerst ontvangers in!", "Fout", JOptionPane.ERROR_MESSAGE);
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Wilt u " + recipients.size() + " e-mails verzenden?",
        "Bevestiging", JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    // Configureer email service
    emailService.configure(configPanel.getSmtpHost(), configPanel.getPort(), configPanel.getUsername(),
        configPanel.getPassword());

    // Start verzending in background thread
    SwingWorker<Void, String> worker = new SwingWorker<>() {
      @Override
      protected Void doInBackground() {
        AttachmentConfig attachmentConfig = attachmentsPanel.getAttachmentConfig();
        int successCount = 0;
        int failCount = 0;

        for (EmailRecipient recipient : recipients) {
          if (!recipient.isEnabled()) {
            publish("Overgeslagen: " + recipient.getEmail() + " (uitgeschakeld)");
            continue;
          }

          try {
            // Personaliseer bericht
            String personalizedMessage = MailPersonalize.personalizeMessage(messagePanel.getMessage(), recipient);

            // Haal bijlagen op voor deze ontvanger
            List<File> attachments = attachmentConfig.getAllAttachmentsForRecipient(recipient.getId());

            // Verzend e-mail
            emailService.sendEmail(configPanel.getFrom(), recipient.getEmail(), configPanel.getCC(),
                configPanel.getReplyTo(), configPanel.getAlias(),
                MailPersonalize.personalizeMessage(messagePanel.getSubject(), recipient), personalizedMessage,
                attachments);

            // Sla EML op indien gewenst
            if (emlStoragePanel.shouldSaveEml()) {
              emlService.saveAsEml(configPanel.getFrom(), recipient.getEmail(), configPanel.getCC(),
                  configPanel.getReplyTo(), configPanel.getAlias(), messagePanel.getSubject(), personalizedMessage,
                  attachments, emlStoragePanel.getSaveDirectory(), true);
            }

            successCount++;
            publish("✓ Verzonden naar: " + recipient.getEmail());

            // Korte pauze om rate limiting te voorkomen
            Thread.sleep(1000);

          } catch (Exception e) {
            failCount++;
            publish("✗ Fout bij " + recipient.getEmail() + ": " + e.getMessage());

            // Sla altijd EML op bij fout
            emlService.saveAsEml(configPanel.getUsername(), recipient.getEmail(),
                MailPersonalize.personalizeMessage(messagePanel.getSubject(), recipient), configPanel.getCC(),
                configPanel.getReplyTo(), configPanel.getAlias(),
                MailPersonalize.personalizeMessage(messagePanel.getMessage(), recipient),
                attachmentConfig.getAllAttachmentsForRecipient(recipient.getId()), emlStoragePanel.getSaveDirectory(),
                false);
          }
        }

        publish("=== Verzending voltooid: " + successCount + " succes, " + failCount + " mislukt ===");
        return null;
      }

      @Override
      protected void process(List<String> chunks) {
        for (String message : chunks) {
          logPanel.log(message);
        }
      }
    };

    worker.execute();
  }

  private void saveAsEml() {
    m_params.save();
    if (!validateConfiguration()) {
      return;
    }

    List<EmailRecipient> recipients = recipientsPanel.getRecipients();
    if (recipients.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Voer eerst ontvangers in!", "Fout", JOptionPane.ERROR_MESSAGE);
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
        "Wilt u EML bestanden genereren voor " + recipients.size() + " ontvangers?", "Bevestiging",
        JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
      return;
    }

    SwingWorker<Void, String> worker = new SwingWorker<>() {
      @Override
      protected Void doInBackground() {
        AttachmentConfig attachmentConfig = attachmentsPanel.getAttachmentConfig();
        int successCount = 0;
        int failCount = 0;

        for (EmailRecipient recipient : admEmailRecipients) {
          try {
            String personalizedMessage = MailPersonalize.personalizeMessage(messagePanel.getMessage(), recipient);

            List<File> attachments = attachmentConfig.getAllAttachmentsForRecipient(recipient.getId());

            File savedFile = emlService.saveAsEml(configPanel.getUsername(), recipient.getEmail(), configPanel.getCC(),
                configPanel.getReplyTo(), configPanel.getAlias(),
                MailPersonalize.personalizeMessage(messagePanel.getSubject(), recipient), personalizedMessage,
                attachments, emlStoragePanel.getSaveDirectory(), true);

            if (savedFile != null) {
              successCount++;
              publish("✓ EML opgeslagen voor: " + recipient.getEmail());
            } else {
              failCount++;
            }

          } catch (Exception e) {
            failCount++;
            publish("✗ Fout bij opslaan EML voor " + recipient.getEmail() + ": " + e.getMessage());
          }
        }

        publish("=== EML opslag voltooid: " + successCount + " succes, " + failCount + " mislukt ===");
        return null;
      }

      @Override
      protected void process(List<String> chunks) {
        for (String message : chunks) {
          logPanel.log(message);
        }
      }
    };

    worker.execute();
  }

  private void testConnection() {
    if (!validateConfiguration()) {
      return;
    }

    emailService.configure(configPanel.getSmtpHost(), configPanel.getPort(), configPanel.getUsername(),
        configPanel.getPassword());

    SwingWorker<Boolean, String> worker = new SwingWorker<>() {
      @Override
      protected Boolean doInBackground() {
        return emailService.testConnection();
      }

      @Override
      protected void done() {
        try {
          if (get()) {
            JOptionPane.showMessageDialog(EmailSenderGUI.this, "Verbinding succesvol getest!", "Succes",
                JOptionPane.INFORMATION_MESSAGE);
          } else {
            JOptionPane.showMessageDialog(EmailSenderGUI.this, "Verbinding mislukt. Controleer instellingen.", "Fout",
                JOptionPane.ERROR_MESSAGE);
          }
        } catch (Exception e) {
          logPanel.log("Fout bij verbindingstest: " + e.getMessage());
        }
      }
    };

    worker.execute();
  }

  private boolean validateConfiguration() {
    if (configPanel.getSmtpHost().isEmpty() || configPanel.getUsername().isEmpty()
        || configPanel.getPassword().isEmpty()) {

      JOptionPane.showMessageDialog(this, "Vul eerst de SMTP configuratie in!", "Configuratie nodig",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }

  private void loadWindowProperties() {
    // Standaard afmetingen
    int defaultWidth = 800;
    int defaultHeight = 600;

    // Opgeslagen waarden ophalen (of standaardwaarden)
    int width = m_params.get_Pref_Window_Width();
    int height = m_params.get_Pref_Window_Height();
    int x = m_params.get_Pref_Window_X();
    int y = m_params.get_Pref_Window_Y();

    // Venstergrootte instellen
    setSize(width, height);

    // Positie instellen (alleen als opgeslagen)
    if (x != -1 && y != -1) {
      setLocation(x, y);
    } else {
      setLocationRelativeTo(null); // Centreer bij eerste keer
    }
  }

  private void saveWindowProperties() {
    // Huidige vensterafmetingen opslaan
    m_params.set_Pref_Window_Width(getWidth());
    m_params.set_Pref_Window_Heiht(getHeight());

    m_params.set_Pref_Window_X(getX());
    m_params.set_Pref_Window_Y(getY());

    // SplitPane positie opslaan indien aanwezig
    if (splitPane != null) {
      m_params.set_Pref_Split_Posion(splitPane.getDividerLocation());
    }
  }

  private void clearAll() {
    int confirm = JOptionPane.showConfirmDialog(this,

        "Alle ingevoerde gegevens wissen?\n\n" + "Dit wist:\n" + "- Alle ontvangers\n" + "- Bericht en onderwerp\n"
            + "- Alle bijlagen\n" + "- Log geschiedenis\n\n" + "Configuratie en EML instellingen blijven behouden.",

        "Alles wissen - Bevestiging", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
      // Roep de nieuwe clear methoden aan
      recipientsPanel.clearRecipients();
      messagePanel.clearMessage();
      attachmentsPanel.clearAttachments();
      logPanel.clearLog();

      logPanel.log("Alle gegevens gewist");
    }
  }

  // ===================================================
  // Data classes for parameters
  public static class RecipientData {
    public String email;
    public String firstName;
    public String lastName;

    public String street_housenr = "";
    public String zipcode = "";
    public String city = "";

    public List<File> personalFilePaths; // Lijst met bestandspaden

    public RecipientData(String email, String firstName, String lastName) {
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
      this.personalFilePaths = new ArrayList<>();
    }

    public RecipientData(String email, String firstName, String lastName, List<File> personalFilePaths) {
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
      this.personalFilePaths = personalFilePaths != null ? personalFilePaths : new ArrayList<>();
    }

    public RecipientData(String email, String firstName, String lastName, List<File> personalFilePaths,
        String a_street_housenr, String a_zipcode, String a_city) {
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
      this.personalFilePaths = personalFilePaths != null ? personalFilePaths : new ArrayList<>();

      this.street_housenr = a_street_housenr;
      this.zipcode = a_zipcode;
      this.city = a_city;
    }
  }

  public static class SMTPConfig {
    public String host;
    public int port;
    public String username;
    public String password;
    public String cc;
    public String replyTo;
    public String alias;

    public SMTPConfig(String host, int port, String username, String password, String cc, String replyTo,
        String alias) {
      this.host = host;
      this.port = port;
      this.username = username;
      this.password = password;
      this.cc = cc;
      this.replyTo = replyTo;
      this.alias = alias;
    }
  }

  public static class MessageConfig {
    public String subject;
    public String template;

    public MessageConfig(String subject, String template) {
      this.subject = subject;
      this.template = template;
    }
  }

}