package gui.panels;

import models.EmailRecipient;
import utils.EmailValidator;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class RecipientsPanel extends JPanel {
    private JTextArea emailsArea;
    private JLabel countLabel, validLabel;
    private Consumer<List<EmailRecipient>> recipientsUpdateCallback;
    private List<EmailRecipient> recipients;
    
    public RecipientsPanel(Consumer<List<EmailRecipient>> recipientsUpdateCallback) {
        this.recipientsUpdateCallback = recipientsUpdateCallback;
        this.recipients = new ArrayList<>();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Ontvangers"));
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(new JLabel("E-mailadressen (één per regel):"), BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        String[] buttons = {
            "📂 Laad bestand", "🗑️ Wissen", "📋 Plakken", 
            "🔍 Duplicaten", "✅ Valideer", "📤 Exporteer"
        };
        
        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.addActionListener(e -> handleButtonClick(text));
            buttonPanel.add(btn);
        }
        
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        
        // Text area
        emailsArea = new JTextArea(15, 50);
        emailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        emailsArea.setText("voorbeeld@email.com\ninfo@bedrijf.nl");
        
        emailsArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateRecipients(); }
            @Override public void removeUpdate(DocumentEvent e) { updateRecipients(); }
            @Override public void changedUpdate(DocumentEvent e) { updateRecipients(); }
        });
        
        add(new JScrollPane(emailsArea), BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countLabel = new JLabel("Totaal: 0");
        validLabel = new JLabel("Geldig: 0");
        
        statsPanel.add(countLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(validLabel);
        
        bottomPanel.add(statsPanel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        updateRecipients();
    }
    
    private void handleButtonClick(String action) {
        switch (action) {
            case "📂 Laad bestand":
                loadFromFile();
                break;
            case "🗑️ Wissen":
                emailsArea.setText("");
                break;
            case "📋 Plakken":
                emailsArea.paste();
                break;
            case "🔍 Duplicaten":
                removeDuplicates();
                break;
            case "✅ Valideer":
                validateEmails();
                break;
            case "📤 Exporteer":
                exportEmails();
                break;
        }
    }
    
    private void updateRecipients() {
        String text = emailsArea.getText().trim();
        String[] lines = text.isEmpty() ? new String[0] : text.split("\n");
        
        recipients.clear();
        int validCount = 0;
        
        for (String line : lines) {
            String email = line.trim();
            if (!email.isEmpty()) {
                EmailRecipient recipient = new EmailRecipient(email);
                recipients.add(recipient);
                if (EmailValidator.isValidEmail(email)) {
                    validCount++;
                }
            }
        }
        
        countLabel.setText("Totaal: " + recipients.size());
        validLabel.setText("Geldig: " + validCount);
        validLabel.setForeground(validCount == recipients.size() ? Color.GREEN.darker() : Color.RED);
        
        if (recipientsUpdateCallback != null) {
            recipientsUpdateCallback.accept(recipients);
        }
    }
    
    private void loadFromFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                emailsArea.setText(content.toString());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Fout: " + e.getMessage());
            }
        }
    }
    
    private void removeDuplicates() {
        String text = emailsArea.getText();
        Set<String> unique = new LinkedHashSet<>();
        for (String line : text.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                unique.add(trimmed.toLowerCase());
            }
        }
        
        StringBuilder result = new StringBuilder();
        for (String email : unique) {
            result.append(email).append("\n");
        }
        emailsArea.setText(result.toString());
    }
    
    public void validateEmails() {
        StringBuilder report = new StringBuilder("Validatierapport:\n\n");
        List<String> invalid = new ArrayList<>();
        
        for (EmailRecipient recipient : recipients) {
            if (!EmailValidator.isValidEmail(recipient.getEmail())) {
                invalid.add(recipient.getEmail());
            }
        }
        
        if (invalid.isEmpty()) {
            report.append("Alle e-mailadressen zijn geldig! ✓");
        } else {
            report.append("Ongeldige adressen (").append(invalid.size()).append("):\n");
            for (String email : invalid) {
                report.append("  • ").append(email).append("\n");
            }
        }
        
        JOptionPane.showMessageDialog(this, report.toString());
    }
    
    private void exportEmails() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("emails_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(chooser.getSelectedFile())) {
                writer.print(emailsArea.getText());
                JOptionPane.showMessageDialog(this, "Exporteren gelukt!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Fout: " + e.getMessage());
            }
        }
    }
    
    public List<EmailRecipient> getRecipients() {
        return recipients;
    }
}
