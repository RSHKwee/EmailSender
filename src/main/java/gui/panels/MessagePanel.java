package gui.panels;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessagePanel extends JPanel {
  /**
   * 
   */
  private static final long serialVersionUID = 5487177925410915193L;

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
    subjectField.setText("Belangrijke update - " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

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

    messageArea.setText(template);

    JScrollPane scrollPane = new JScrollPane(messageArea);
    messagePanel.add(scrollPane, BorderLayout.CENTER);

    add(messagePanel, BorderLayout.CENTER);

    // Bottom panel
    JPanel bottomPanel = new JPanel(new BorderLayout());

    // Personalization buttons
    JPanel personalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    personalPanel.add(new JLabel("Variabelen: "));

    String[] variables = { "{naam}", "{email}", "{id}", "{datum}", "{tijd}" };
    for (String var : variables) {
      JButton btn = new JButton(var);
      btn.addActionListener(e -> messageArea.insert(var, messageArea.getCaretPosition()));
      personalPanel.add(btn);
    }

    bottomPanel.add(personalPanel, BorderLayout.WEST);

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

  // Getters
  public String getSubject() {
    return subjectField.getText();
  }

  public String getMessage() {
    return messageArea.getText();
  }

  public void clearMessage() {
    // TODO Auto-generated method stub

  }
}
