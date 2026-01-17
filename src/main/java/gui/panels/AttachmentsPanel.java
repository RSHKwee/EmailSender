package gui.panels;

import models.AttachmentConfig;
import models.EmailRecipient;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.util.List;

public class AttachmentsPanel extends JPanel {
  private AttachmentConfig attachmentConfig;
  private DefaultListModel<File> commonAttachmentsModel;
  private DefaultListModel<File> personalAttachmentsModel;
  private JList<File> commonAttachmentsList;
  private JList<File> personalAttachmentsList;
  private JComboBox<EmailRecipient> recipientCombo;
  private JLabel commonCountLabel, personalCountLabel, totalSizeLabel;

  public AttachmentsPanel() {
    this.attachmentConfig = new AttachmentConfig();
    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createTitledBorder("Bijlagen Configuratie"));

    // Main panel met tabs
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Algemene Bijlagen", createCommonAttachmentsPanel());
    tabbedPane.addTab("Persoonlijke Bijlagen", createPersonalAttachmentsPanel());

    add(tabbedPane, BorderLayout.CENTER);
    add(createInfoPanel(), BorderLayout.SOUTH);
  }

  private JPanel createCommonAttachmentsPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));

    commonAttachmentsModel = new DefaultListModel<>();
    commonAttachmentsList = new JList<>(commonAttachmentsModel);
    commonAttachmentsList.setCellRenderer(new gui.renderers.AttachmentListCellRenderer());

    JScrollPane scrollPane = new JScrollPane(commonAttachmentsList);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton addBtn = new JButton("📎 Toevoegen");
    addBtn.addActionListener(e -> addCommonAttachment());

    JButton removeBtn = new JButton("🗑️ Verwijder");
    removeBtn.addActionListener(e -> removeCommonAttachment());

    JButton clearBtn = new JButton("🧹 Wissen");
    clearBtn.addActionListener(e -> clearCommonAttachments());

    buttonPanel.add(addBtn);
    buttonPanel.add(removeBtn);
    buttonPanel.add(clearBtn);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createPersonalAttachmentsPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));

    // Recipient selector
    JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    selectorPanel.add(new JLabel("Selecteer ontvanger:"));

    recipientCombo = new JComboBox<>();
    recipientCombo.addActionListener(e -> updatePersonalAttachmentsList());
    selectorPanel.add(recipientCombo);

    panel.add(selectorPanel, BorderLayout.NORTH);

    // Personal attachments list
    personalAttachmentsModel = new DefaultListModel<>();
    personalAttachmentsList = new JList<>(personalAttachmentsModel);
    personalAttachmentsList.setCellRenderer(new gui.renderers.AttachmentListCellRenderer());

    JScrollPane scrollPane = new JScrollPane(personalAttachmentsList);
    panel.add(scrollPane, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton addBtn = new JButton("📎 Toevoegen");
    addBtn.addActionListener(e -> addPersonalAttachment());

    JButton removeBtn = new JButton("🗑️ Verwijder");
    removeBtn.addActionListener(e -> removePersonalAttachment());

    JButton clearBtn = new JButton("🧹 Wissen");
    clearBtn.addActionListener(e -> clearPersonalAttachments());

    buttonPanel.add(addBtn);
    buttonPanel.add(removeBtn);
    buttonPanel.add(clearBtn);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createInfoPanel() {
    JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    commonCountLabel = new JLabel("Algemeen: 0 bestanden");
    personalCountLabel = new JLabel("Persoonlijk: 0 bestanden");
    totalSizeLabel = new JLabel("Totaal: 0 KB");

    panel.add(commonCountLabel);
    panel.add(personalCountLabel);
    panel.add(totalSizeLabel);

    return panel;
  }

  // Public methods
  public void updateRecipientsList(List<EmailRecipient> recipients) {
    recipientCombo.removeAllItems();
    for (EmailRecipient recipient : recipients) {
      recipientCombo.addItem(recipient);
    }
    if (recipientCombo.getItemCount() > 0) {
      recipientCombo.setSelectedIndex(0);
    }
  }

  public AttachmentConfig getAttachmentConfig() {
    return attachmentConfig;
  }

  public void updateAttachmentInfo() {
    int commonCount = attachmentConfig.getCommonAttachments().size();
    commonCountLabel.setText("Algemeen: " + commonCount + " bestanden");

    EmailRecipient selected = (EmailRecipient) recipientCombo.getSelectedItem();
    int personalCount = 0;
    if (selected != null) {
      personalCount = attachmentConfig.getPersonalAttachments(selected.getId()).size();
    }
    personalCountLabel.setText("Persoonlijk: " + personalCount + " bestanden");

    long totalSize = attachmentConfig.getTotalSize();
    if (totalSize > 1024 * 1024) {
      totalSizeLabel.setText(String.format("Totaal: %.2f MB", totalSize / (1024.0 * 1024.0)));
    } else {
      totalSizeLabel.setText(String.format("Totaal: %.1f KB", totalSize / 1024.0));
    }
  }

  // Private helper methods
  private void addCommonAttachment() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(true);
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      for (File file : fileChooser.getSelectedFiles()) {
        attachmentConfig.addCommonAttachment(file);
        commonAttachmentsModel.addElement(file);
      }
      updateAttachmentInfo();
    }
  }

  private void removeCommonAttachment() {
    int[] selected = commonAttachmentsList.getSelectedIndices();
    for (int i = selected.length - 1; i >= 0; i--) {
      File file = commonAttachmentsModel.get(selected[i]);
      attachmentConfig.removeCommonAttachment(file);
      commonAttachmentsModel.remove(selected[i]);
    }
    updateAttachmentInfo();
  }

  private void clearCommonAttachments() {
    attachmentConfig.clearCommonAttachments();
    commonAttachmentsModel.clear();
    updateAttachmentInfo();
  }

  private void addPersonalAttachment() {
    EmailRecipient recipient = (EmailRecipient) recipientCombo.getSelectedItem();
    if (recipient == null) {
      JOptionPane.showMessageDialog(this, "Selecteer eerst een ontvanger");
      return;
    }

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(true);
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      for (File file : fileChooser.getSelectedFiles()) {
        attachmentConfig.addPersonalAttachment(recipient.getId(), file);
        personalAttachmentsModel.addElement(file);
      }
      updateAttachmentInfo();
    }
  }

  private void removePersonalAttachment() {
    EmailRecipient recipient = (EmailRecipient) recipientCombo.getSelectedItem();
    if (recipient == null) {
      return;
    }

    int[] selected = personalAttachmentsList.getSelectedIndices();
    for (int i = selected.length - 1; i >= 0; i--) {
      File file = personalAttachmentsModel.get(selected[i]);
      attachmentConfig.removePersonalAttachment(recipient.getId(), file);
      personalAttachmentsModel.remove(selected[i]);
    }
    updateAttachmentInfo();
  }

  private void clearPersonalAttachments() {
    EmailRecipient recipient = (EmailRecipient) recipientCombo.getSelectedItem();
    if (recipient == null) {
      return;
    }

    attachmentConfig.clearPersonalAttachments(recipient.getId());
    personalAttachmentsModel.clear();
    updateAttachmentInfo();
  }

  private void updatePersonalAttachmentsList() {
    personalAttachmentsModel.clear();
    EmailRecipient recipient = (EmailRecipient) recipientCombo.getSelectedItem();
    if (recipient != null) {
      for (File file : attachmentConfig.getPersonalAttachments(recipient.getId())) {
        personalAttachmentsModel.addElement(file);
      }
    }
    updateAttachmentInfo();
  }

  public void clearAttachments() {
    // TODO Auto-generated method stub

  }
}