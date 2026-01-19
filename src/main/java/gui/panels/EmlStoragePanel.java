package gui.panels;

import javax.swing.*;

import main.UserSetting;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class EmlStoragePanel extends JPanel {
  /**
  * 
  */
  private static final long serialVersionUID = -6201832735110220981L;
  private UserSetting m_params = UserSetting.getInstance();

  private JTextField directoryField;
  private JCheckBox saveEmlCheckbox;

  public EmlStoragePanel() {
    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createTitledBorder("EML Opslag"));

    // Directory selection
    JPanel dirPanel = new JPanel(new BorderLayout(5, 5));
    dirPanel.add(new JLabel("Opslagmap:"), BorderLayout.NORTH);

    JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));

    String emlDir = m_params.get_EmlDirectory();
    if (emlDir.isBlank()) {
      emlDir = System.getProperty("user.home") + "/eml_storage";
    }
    directoryField = new JTextField(emlDir);
    JButton browseBtn = new JButton("Bladeren");
    browseBtn.addActionListener(e -> browseDirectory());

    fieldPanel.add(directoryField, BorderLayout.CENTER);
    fieldPanel.add(browseBtn, BorderLayout.EAST);
    dirPanel.add(fieldPanel, BorderLayout.CENTER);

    add(dirPanel, BorderLayout.NORTH);

    // Options
    JPanel optionsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
    saveEmlCheckbox = new JCheckBox("EML bestanden opslaan bij verzending", true);
    JCheckBox saveOnFailCheckbox = new JCheckBox("Altijd opslaan bij mislukking", true);
    JCheckBox includeAttachmentsCheckbox = new JCheckBox("Bijlagen opslaan in EML", true);

    optionsPanel.add(saveEmlCheckbox);
    optionsPanel.add(saveOnFailCheckbox);
    optionsPanel.add(includeAttachmentsCheckbox);

    add(optionsPanel, BorderLayout.CENTER);

    // File list
    DefaultListModel<String> fileModel = new DefaultListModel<>();
    JList<String> fileList = new JList<>(fileModel);

    JPanel listPanel = new JPanel(new BorderLayout());
    listPanel.add(new JLabel("Opgeslagen EML bestanden:"), BorderLayout.NORTH);
    listPanel.add(new JScrollPane(fileList), BorderLayout.CENTER);

    JPanel listButtons = new JPanel(new FlowLayout());
    JButton refreshBtn = new JButton("Vernieuwen");
    refreshBtn.addActionListener(e -> refreshFileList(fileModel));

    JButton openBtn = new JButton("Openen");
    openBtn.addActionListener(e -> openSelectedFile(fileList.getSelectedValue()));

    listButtons.add(refreshBtn);
    listButtons.add(openBtn);
    listPanel.add(listButtons, BorderLayout.SOUTH);

    add(listPanel, BorderLayout.SOUTH);

    // Initial refresh
    refreshFileList(fileModel);
  }

  private void browseDirectory() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
      m_params.set_EmlDirectory(chooser.getSelectedFile().getAbsolutePath());
      m_params.save();
    }
  }

  private void refreshFileList(DefaultListModel<String> model) {
    model.clear();
    File dir = new File(directoryField.getText());
    if (dir.exists() && dir.isDirectory()) {
      File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".eml"));
      if (files != null) {
        for (File file : files) {
          model.addElement(file.getName());
        }
      }
    }
  }

  private void openSelectedFile(String filename) {
    if (filename != null) {
      File file = new File(directoryField.getText(), filename);
      if (file.exists()) {
        try {
          Desktop.getDesktop().open(file);
        } catch (IOException e) {
          JOptionPane.showMessageDialog(this, "Kan bestand niet openen: " + e.getMessage());
        }
      }
    }
  }

  public void setStorage(String emlDir) {
    directoryField.setText(emlDir);
  }

  // Getters
  public String getSaveDirectory() {
    return directoryField.getText();
  }

  public boolean shouldSaveEml() {
    return saveEmlCheckbox.isSelected();
  }
}