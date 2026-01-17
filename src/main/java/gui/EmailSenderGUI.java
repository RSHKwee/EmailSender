package gui;

import gui.panels.*;
import models.AttachmentConfig;
import models.EmailRecipient;
import services.EmailService;
import services.EmlService;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmailSenderGUI extends JFrame {
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

  // Temporary storage for recipients until attachmentsPanel is initialized
  private List<EmailRecipient> pendingRecipientsUpdate;

  public EmailSenderGUI() {
    setTitle("E-mail Verzender Pro");
    setSize(800, 650);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Initialize services with logging
    logPanel = new LogPanel();
    emailService = new EmailService(logPanel::log);
    emlService = new EmlService(logPanel::log);

    pendingRecipientsUpdate = new ArrayList<>();

    initComponents();

    // Process any pending recipient updates
    if (!pendingRecipientsUpdate.isEmpty()) {
      attachmentsPanel.updateRecipientsList(pendingRecipientsUpdate);
      pendingRecipientsUpdate.clear();
    }
  }

  private void initComponents() {
    // Create panels in correct order
    configPanel = new ConfigPanel();
    messagePanel = new MessagePanel();
    attachmentsPanel = new AttachmentsPanel();
    emlStoragePanel = new EmlStoragePanel();

    // Create recipients panel last, with delayed callback
    recipientsPanel = new RecipientsPanel(this::onRecipientsUpdated);

    // Main tabbed pane
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("📧 Configuratie", configPanel);
    tabbedPane.addTab("👥 Ontvangers", recipientsPanel);
    tabbedPane.addTab("✉️ Bericht", messagePanel);
    tabbedPane.addTab("📎 Bijlagen", attachmentsPanel);
    tabbedPane.addTab("💾 EML Opslag", emlStoragePanel);
    tabbedPane.addTab("📋 Log", logPanel);

    // Button panel
    JPanel buttonPanel = createButtonPanel();

    // Layout
    setLayout(new BorderLayout());
    add(tabbedPane, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
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

    JButton sendBtn = createButton("📤 Verzenden", new Color(46, 125, 50), e -> sendEmails());
    JButton saveBtn = createButton("💾 Opslaan EML", new Color(21, 101, 192), e -> saveAsEml());
    JButton testBtn = createButton("🔌 Testen", new Color(245, 124, 0), e -> testConnection());
    JButton clearBtn = createButton("🧹 Wissen", new Color(158, 158, 158), e -> clearAll());
    JButton helpBtn = createButton("❓ Help", new Color(103, 58, 183), e -> showHelp());
    JButton exitBtn = createButton("🚪 Afsluiten", new Color(198, 40, 40), e -> System.exit(0));

    panel.add(sendBtn);
    panel.add(saveBtn);
    panel.add(testBtn);
    panel.add(clearBtn);
    panel.add(helpBtn);
    panel.add(exitBtn);

    return panel;
  }

  private JButton createButton(String text, Color color, java.awt.event.ActionListener action) {
    JButton button = new JButton(text);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFont(new Font("Arial", Font.BOLD, 12));
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    button.addActionListener(action);
    return button;
  }

  // ... rest van de methoden blijft hetzelfde ...
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
            String personalizedMessage = personalizeMessage(messagePanel.getMessage(), recipient);

            // Haal bijlagen op voor deze ontvanger
            List<File> attachments = attachmentConfig.getAllAttachmentsForRecipient(recipient.getId());

            // Verzend e-mail
            emailService.sendEmail(recipient.getEmail(), messagePanel.getSubject(), personalizedMessage, attachments);

            // Sla EML op indien gewenst
            if (emlStoragePanel.shouldSaveEml()) {
              emlService.saveAsEml(configPanel.getUsername(), recipient.getEmail(), messagePanel.getSubject(),
                  personalizedMessage, attachments, emlStoragePanel.getSaveDirectory(), true);
            }

            successCount++;
            publish("✓ Verzonden naar: " + recipient.getEmail());

            // Korte pauze om rate limiting te voorkomen
            Thread.sleep(1000);

          } catch (Exception e) {
            failCount++;
            publish("✗ Fout bij " + recipient.getEmail() + ": " + e.getMessage());

            // Sla altijd EML op bij fout
            emlService.saveAsEml(configPanel.getUsername(), recipient.getEmail(), messagePanel.getSubject(),
                personalizeMessage(messagePanel.getMessage(), recipient),
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

        for (EmailRecipient recipient : recipients) {
          try {
            String personalizedMessage = personalizeMessage(messagePanel.getMessage(), recipient);

            List<File> attachments = attachmentConfig.getAllAttachmentsForRecipient(recipient.getId());

            File savedFile = emlService.saveAsEml(configPanel.getUsername(), recipient.getEmail(),
                messagePanel.getSubject(), personalizedMessage, attachments, emlStoragePanel.getSaveDirectory(), true);

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

  private String personalizeMessage(String template, EmailRecipient recipient) {
    // Simple personalization - kan uitgebreid worden
    return template.replace("{naam}", recipient.getName()).replace("{email}", recipient.getEmail()).replace("{id}",
        recipient.getId());
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

  private void showHelp() {
    String helpText = """
        E-mail Verzender Pro - Help

        1. CONFIGURATIE
        - Vul SMTP gegevens in (bijv. Gmail, Outlook)
        - Gebruik App-wachtwoord bij 2-factor authenticatie

        2. ONTVANGERS
        - Voer e-mailadressen in, één per regel
        - Gebruik de knoppen om te laden, valideren of duplicaten te verwijderen

        3. BERICHT
        - Vul onderwerp en bericht in
        - Gebruik variabelen: {naam}, {email}, {id}, {datum}, {tijd}

        4. BIJLAGEN
        - Algemene bijlagen: voor alle ontvangers
        - Persoonlijke bijlagen: specifiek per ontvanger
        - Selecteer eerst een ontvanger voor persoonlijke bijlagen

        5. EML OPSLAG
        - Sla e-mails op als .eml bestanden
        - Kan geopend worden in Outlook, Thunderbird, etc.
        - Altijd opslaan bij mislukte verzending

        6. KNOPPEN
        - Verzenden: Stuur alle e-mails
        - Opslaan EML: Genereer alleen EML bestanden
        - Testen: Test SMTP verbinding
        - Wissen: Wis alle ingevoerde gegevens
        """;

    JTextArea textArea = new JTextArea(helpText);
    textArea.setEditable(false);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(600, 400));

    JOptionPane.showMessageDialog(this, scrollPane, "Help", JOptionPane.INFORMATION_MESSAGE);
  }

}