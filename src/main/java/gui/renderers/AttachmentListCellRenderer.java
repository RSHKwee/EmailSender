package gui.renderers;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AttachmentListCellRenderer extends DefaultListCellRenderer {
  /**
  * 
  */
  private static final long serialVersionUID = -4220453631696897518L;

  @Override
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
      boolean cellHasFocus) {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    if (value instanceof File) {
      File file = (File) value;
      setText(getFileIcon(file.getName()) + " " + file.getName() + " (" + formatFileSize(file.length()) + ")");
      setToolTipText(file.getAbsolutePath());
    }

    return this;
  }

  private String getFileIcon(String filename) {
    String lower = filename.toLowerCase();
    if (lower.endsWith(".pdf")) {
      return "📕";
    }
    if (lower.endsWith(".doc") || lower.endsWith(".docx")) {
      return "📘";
    }
    if (lower.endsWith(".xls") || lower.endsWith(".xlsx")) {
      return "📗";
    }
    if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif")) {
      return "🖼️";
    }
    if (lower.endsWith(".zip") || lower.endsWith(".rar")) {
      return "📦";
    }
    if (lower.endsWith(".txt")) {
      return "📝";
    }
    return "📎";
  }

  private String formatFileSize(long bytes) {
    if (bytes > 1024 * 1024) {
      return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    } else if (bytes > 1024) {
      return String.format("%.0f KB", bytes / 1024.0);
    } else {
      return bytes + " bytes";
    }
  }
}