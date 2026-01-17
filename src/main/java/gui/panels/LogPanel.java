package gui.panels;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogPanel extends JPanel {
  /**
  * 
  */
  private static final long serialVersionUID = 1787781473202823348L;
  private JTextArea logArea;

  public LogPanel() {
    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("Log"));

    logArea = new JTextArea(15, 60);
    logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    logArea.setEditable(false);

    add(new JScrollPane(logArea), BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton clearBtn = new JButton("Wissen");
    clearBtn.addActionListener(e -> logArea.setText(""));

    JButton saveBtn = new JButton("Opslaan");
    saveBtn.addActionListener(e -> saveLog());

    buttonPanel.add(clearBtn);
    buttonPanel.add(saveBtn);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  public void log(String message) {
    SwingUtilities.invokeLater(() -> {
      String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
      logArea.append("[" + timestamp + "] " + message + "\n");
      logArea.setCaretPosition(logArea.getDocument().getLength());
    });
  }

  private void saveLog() {
    JFileChooser chooser = new JFileChooser();
    chooser
        .setSelectedFile(new File("email_log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));

    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      try (PrintWriter writer = new PrintWriter(chooser.getSelectedFile())) {
        writer.write(logArea.getText());
        log("Log opgeslagen: " + chooser.getSelectedFile().getName());
      } catch (Exception e) {
        log("Fout bij opslaan log: " + e.getMessage());
      }
    }
  }

  public void clearLog() {
    logArea.setText("");
  }
}
