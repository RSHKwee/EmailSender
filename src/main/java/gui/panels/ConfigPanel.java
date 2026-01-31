package gui.panels;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import kwee.logger.MyLogger;
import library.MailServerSettings;
import main.Main;
import main.UserSetting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigPanel extends JPanel {
  /**
  * 
  */
  private static final long serialVersionUID = 6238324373086768935L;
  private static final Logger LOGGER = MyLogger.getLogger();
  private UserSetting m_params = UserSetting.getInstance();

  private JTextField smtpField;
  private JTextField portField;
  private JTextField usernameField;
  private JPasswordField passwordField;

  private JTextField ccField;
  private JTextField replyToField;
  private JTextField aliasField;

  private MailServerSettings mailSrvSetting = new MailServerSettings();

  public ConfigPanel() {
    mailSrvSetting.load();
    mailSrvSetting.setApplicatie(Main.c_AppName, Main.c_CopyrightYear);
    initComponents();
  }

  private void initComponents() {
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createTitledBorder("SMTP Configuratie"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Provider selector
    List<String> l_providers = mailSrvSetting.getIds();
    l_providers.add("Gmail");
    l_providers.add("Outlook/Hotmail");
    l_providers.add("Office365");
    l_providers.add("Yahoo");
    TreeSet<String> treeSet = new TreeSet<>(l_providers);
    l_providers = new ArrayList<>(treeSet);

    List<String> temp = new ArrayList<String>();
    temp.add("Kies provider...");
    temp.addAll(l_providers);
    temp.add("Custom");
    String[] providers = temp.toArray(new String[0]);

    JComboBox<String> providerCombo = new JComboBox<>(providers);
    providerCombo.setEditable(true);
    providerCombo.setSelectedItem(m_params.get_MailProvider());
    providerCombo.addActionListener(e -> setProviderConfig((String) providerCombo.getSelectedItem()));

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    add(new JLabel("E-mail provider:"), gbc);
    gbc.gridx = 1;
    gbc.gridy = 0;
    add(providerCombo, gbc);

    // SMTP fields
    gbc.gridwidth = 1;
    gbc.gridy = 2;
    gbc.gridx = 0;
    add(new JLabel("SMTP Server:"), gbc);
    gbc.gridx = 1;
    smtpField = new JTextField(25);
    addDocumentListenerToSave(smtpField, "smtpServer");
    add(smtpField, gbc);

    gbc.gridy = 3;
    gbc.gridx = 0;
    add(new JLabel("Port:"), gbc);
    gbc.gridx = 1;
    portField = new JTextField(10);
    addDocumentListenerToSave(portField, "smtpPort");
    add(portField, gbc);

    gbc.gridy = 4;
    gbc.gridx = 0;
    add(new JLabel("Gebruikersnaam:"), gbc);
    gbc.gridx = 1;
    usernameField = new JTextField(25);
    addDocumentListenerToSave(usernameField, "username");
    add(usernameField, gbc);

    gbc.gridy = 5;
    gbc.gridx = 0;
    add(new JLabel("Wachtwoord:"), gbc);
    gbc.gridx = 1;
    passwordField = new JPasswordField(25);
    addDocumentListenerToSave(passwordField, "password");
    add(passwordField, gbc);

    gbc.gridy = 6;
    gbc.gridx = 0;
    add(new JLabel("cc adres:"), gbc);
    gbc.gridx = 1;
    ccField = new JTextField(25);
    addDocumentListenerToSave(ccField, "carboncopy");
    add(ccField, gbc);

    gbc.gridy = 7;
    gbc.gridx = 0;
    add(new JLabel("Reply to adres:"), gbc);
    gbc.gridx = 1;
    replyToField = new JTextField(25);
    addDocumentListenerToSave(replyToField, "replyto");
    add(replyToField, gbc);

    gbc.gridy = 8;
    gbc.gridx = 0;
    add(new JLabel("Alias:"), gbc);
    gbc.gridx = 1;
    aliasField = new JTextField(25);
    addDocumentListenerToSave(aliasField, "alias");
    add(aliasField, gbc);
  }

  private void setProviderConfig(String a_provider) {
    String provider = a_provider.trim();
    mailSrvSetting.setId(provider);

    m_params.set_MailProvider(provider);
    m_params.save();

    if (mailSrvSetting.getPort() == -1) {
      switch (provider) {
      case "Gmail":
        smtpField.setText("smtp.gmail.com");
        portField.setText("587");
        break;
      case "Outlook/Hotmail":
        smtpField.setText("smtp-mail.outlook.com");
        portField.setText("587");
        break;
      case "Office365":
        smtpField.setText("smtp.office365.com");
        portField.setText("587");
        break;
      case "Yahoo":
        smtpField.setText("smtp.mail.yahoo.com");
        portField.setText("587");
        break;
      }
      ccField.setText(m_params.get_CC());
      replyToField.setText(m_params.get_ReplyTo());
      aliasField.setText(m_params.get_Alias());
    } else {
      smtpField.setText(mailSrvSetting.getHost());
      portField.setText(String.valueOf(mailSrvSetting.getPort()));
      usernameField.setText(mailSrvSetting.getUsername());
      passwordField.setText(mailSrvSetting.getPassword());
      ccField.setText(m_params.get_CC());
      replyToField.setText(m_params.get_ReplyTo());
      aliasField.setText(m_params.get_Alias());
    }
  }

  public void setSMTPConfig(String host, int port, String username, String password, String cc, String replyTo,
      String alias) {
    mailSrvSetting.setId(m_params.get_MailProvider());
    if (mailSrvSetting.getPort() == -1) {
      smtpField.setText(host);
      portField.setText(String.valueOf(port));
      usernameField.setText(username);
      passwordField.setText(password);
      ccField.setText(cc);
      replyToField.setText(replyTo);
      aliasField.setText(alias);
    } else {
      smtpField.setText(mailSrvSetting.getHost());
      portField.setText(String.valueOf(mailSrvSetting.getPort()));
      usernameField.setText(mailSrvSetting.getUsername());
      passwordField.setText(mailSrvSetting.getPassword());
      ccField.setText(m_params.get_CC());
      replyToField.setText(m_params.get_ReplyTo());
      aliasField.setText(m_params.get_Alias());
    }
  }

  // Getters
  public String getSmtpHost() {
    return smtpField.getText();
  }

  public int getPort() {
    try {
      return Integer.parseInt(portField.getText());
    } catch (NumberFormatException e) {
      return 587;
    }
  }

  public String getUsername() {
    return usernameField.getText();
  }

  public String getPassword() {
    return new String(passwordField.getPassword());
  }

  public String getCc() {
    return ccField.getText();
  }

  public String getReplyTo() {
    return replyToField.getText();
  }

  public String getAlias() {
    return aliasField.getText();
  }

  private void addDocumentListenerToSave(JTextField field, String propertyName) {
    field.getDocument().addDocumentListener(new DocumentListener() {
      private Timer timer;
      {
        // Timer om wijzigingen te bufferen (niet bij elke toetsaanslag opslaan)
        timer = new Timer(1000, e -> saveField(propertyName, field.getText()));
        timer.setRepeats(false);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        timer.restart();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        timer.restart();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        timer.restart();
      }

    });
  }

  private void saveField(String propertyName, String value) {
    switch (propertyName) {
    case "smtpServer":
      mailSrvSetting.setHost(value);
      break;
    case "smtpPort":
      mailSrvSetting.setPort(Integer.parseInt(value));
      break;
    case "username":
      mailSrvSetting.setUsername(value);
      break;
    case "password":
      mailSrvSetting.setPassword(value);
      break;
    case "carboncopy":
      m_params.set_CC(value);
      break;
    case "replyto":
      m_params.set_ReplyTo(value);
      break;
    case "alias":
      m_params.set_Alias(value);
      break;
    }
    m_params.save();
    mailSrvSetting.save();
  }
}