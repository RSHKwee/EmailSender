package gui.panels;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import kwee.logger.MyLogger;
import main.UserSetting;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessagePanel extends JPanel {
  private static final Logger LOGGER = MyLogger.getLogger();
  /**
   * 
   */
  private static final long serialVersionUID = 5487177925410915193L;
  private UserSetting m_params = UserSetting.getInstance();

  private JTextField subjectField;
  private JTextArea messageArea;
  private JLabel charCountLabel;

  public MessagePanel() {
    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createTitledBorder("Bericht"));

    // Subject
    JPanel subjectPanel = new JPanel(new BorderLayout(5, 5));
    subjectPanel.add(new JLabel("Onderwerp:"), BorderLayout.WEST);

    subjectField = new JTextField(40);
    String subject = "Belangrijke update - " + new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    if (!m_params.get_Subject().isBlank()) {
      subject = m_params.get_Subject();
    }
    subjectField.setText(subject);

    subjectPanel.add(subjectField, BorderLayout.CENTER);
    add(subjectPanel, BorderLayout.NORTH);

    // Message
    JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
    messagePanel.add(new JLabel("Bericht:"), BorderLayout.NORTH);

    messageArea = new JTextArea(20, 50);
    messageArea.setFont(new Font("Arial", Font.PLAIN, 12));
    messageArea.setLineWrap(true);
    messageArea.setWrapStyleWord(true);

    String template = "Beste {naam},\n\n" + "Hierbij ontvangt u onze update.\n\n" + "Met vriendelijke groet,\n"
        + "Het Team\n\n" + "Datum: {datum}\n" + "E-mail: {email}";
    if (!m_params.get_Message().isBlank()) {
      template = m_params.get_Message();
    }
    messageArea.setText(template);
    messageArea.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        // Wordt aangeroepen wanneer het textarea focus verliest
        m_params.set_Message(messageArea.getText());
        m_params.set_Subject(subjectField.getText());
        m_params.save();
        LOGGER.log(Level.FINE, "Textarea heeft focus verlaten");
      }

      @Override
      public void focusGained(FocusEvent e) {
        // Wordt aangeroepen wanneer het textarea focus krijgt
        LOGGER.log(Level.FINE, "Textarea heeft focus gekregen");
      }
    });
    JScrollPane scrollPane = new JScrollPane(messageArea);
    messagePanel.add(scrollPane, BorderLayout.CENTER);

    add(messagePanel, BorderLayout.CENTER);

    // Bottom panel
    JPanel bottomPanel = new JPanel(new BorderLayout());

    // Personalization buttons
    JPanel personalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    personalPanel.add(new JLabel("Variabelen: "));

    // Maak een JComboBox met de variabelen
    String[] variables = { "{achternaam}", "{datum}", "{email}", "{id}", "{naam}", "{plaats}", "{postcode}",
        "{straat_nr}", "{voornaam}" };
    JComboBox<String> variableComboBox = new JComboBox<>(variables);
    bottomPanel.add(personalPanel, BorderLayout.WEST);

    // Voeg een actielistener toe voor wanneer een item geselecteerd wordt
    variableComboBox.addActionListener(e -> {
      String selectedVariable = (String) variableComboBox.getSelectedItem();
      if (selectedVariable != null) {
        messageArea.insert(selectedVariable, messageArea.getCaretPosition());
      }
    });

    // Voeg de dropdown toe aan het panel
    personalPanel.add(variableComboBox);

    // Character count
    charCountLabel = new JLabel("Tekens: 0");
    messageArea.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateCharCount();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateCharCount();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateCharCount();
      }
    });
    bottomPanel.add(charCountLabel, BorderLayout.EAST);
    add(bottomPanel, BorderLayout.SOUTH);

    updateCharCount();
  }

  private void updateCharCount() {
    int count = messageArea.getText().length();
    charCountLabel.setText("Tekens: " + count);
    charCountLabel.setForeground(count > 10000 ? Color.RED : Color.BLACK);
  }

  public void setMessage(String subject, String body) {
    subjectField.setText(subject);
    messageArea.setText(body);
    updateCharCount();
  }

  // Getters
  public String getSubject() {
    String subject = subjectField.getText();
    return subject;
  }

  public String getMessage() {
    String message = messageArea.getText();
    return message;
  }

  public void clearMessage() {
    // TODO Auto-generated method stub
  }
}
