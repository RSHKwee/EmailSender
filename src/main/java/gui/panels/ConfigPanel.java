package gui.panels;

import javax.swing.*;

import kwee.logger.MyLogger;

import java.awt.*;
import java.util.logging.Logger;

public class ConfigPanel extends JPanel {
  /**
  * 
  */
  private static final long serialVersionUID = 6238324373086768935L;
  private static final Logger LOGGER = MyLogger.getLogger();

  private JTextField smtpField;
  private JTextField portField;
  private JTextField usernameField;
  private JPasswordField passwordField;

  public ConfigPanel() {
    initComponents();
  }

  private void initComponents() {
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createTitledBorder("SMTP Configuratie"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Provider selector
    String[] providers = { "Kies provider...", "Gmail", "Outlook/Hotmail", "Office365", "Yahoo", "Custom" };
    JComboBox<String> providerCombo = new JComboBox<>(providers);
    providerCombo.addActionListener(e -> setProviderConfig((String) providerCombo.getSelectedItem()));

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    add(new JLabel("E-mail provider:"), gbc);
    gbc.gridy = 1;
    add(providerCombo, gbc);

    // SMTP fields
    gbc.gridwidth = 1;
    gbc.gridy = 2;
    gbc.gridx = 0;
    add(new JLabel("SMTP Server:"), gbc);
    gbc.gridx = 1;
    smtpField = new JTextField(25);
    add(smtpField, gbc);

    gbc.gridy = 3;
    gbc.gridx = 0;
    add(new JLabel("Port:"), gbc);
    gbc.gridx = 1;
    portField = new JTextField(10);
    add(portField, gbc);

    gbc.gridy = 4;
    gbc.gridx = 0;
    add(new JLabel("Gebruikersnaam:"), gbc);
    gbc.gridx = 1;
    usernameField = new JTextField(25);
    add(usernameField, gbc);

    gbc.gridy = 5;
    gbc.gridx = 0;
    add(new JLabel("Wachtwoord:"), gbc);
    gbc.gridx = 1;
    passwordField = new JPasswordField(25);
    add(passwordField, gbc);
  }

  private void setProviderConfig(String provider) {
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
  }

  public void setSMTPConfig(String host, int port, String username, String password) {
    smtpField.setText(host);
    portField.setText(String.valueOf(port));
    usernameField.setText(username);
    passwordField.setText(password);
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
}