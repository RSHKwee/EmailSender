package main;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gui.EmailSenderGUI;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        // Gebruik system look and feel
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // Optioneel: verbeter de UI
        UIManager.put("TabbedPane.selected", new Color(66, 165, 245));
        UIManager.put("TabbedPane.selectHighlight", new Color(66, 165, 245));

      } catch (Exception e) {
        e.printStackTrace();
      }

      // Toon de GUI
      EmailSenderGUI gui = new EmailSenderGUI();
      gui.setVisible(true);
    });
  }
}
