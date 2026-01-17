package models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttachmentConfig {
  private List<File> commonAttachments;
  private Map<String, List<File>> personalAttachments;

  public AttachmentConfig() {
    commonAttachments = new ArrayList<>();
    personalAttachments = new HashMap<>();
  }

  public List<File> getCommonAttachments() {
    return commonAttachments;
  }

  public void addCommonAttachment(File file) {
    if (!commonAttachments.contains(file)) {
      commonAttachments.add(file);
    }
  }

  public void removeCommonAttachment(File file) {
    commonAttachments.remove(file);
  }

  public void clearCommonAttachments() {
    commonAttachments.clear();
  }

  public List<File> getPersonalAttachments(String recipientId) {
    return personalAttachments.getOrDefault(recipientId, new ArrayList<>());
  }

  public void addPersonalAttachment(String recipientId, File file) {
    personalAttachments.computeIfAbsent(recipientId, k -> new ArrayList<>()).add(file);
  }

  public void removePersonalAttachment(String recipientId, File file) {
    List<File> attachments = personalAttachments.get(recipientId);
    if (attachments != null) {
      attachments.remove(file);
      if (attachments.isEmpty()) {
        personalAttachments.remove(recipientId);
      }
    }
  }

  public void clearPersonalAttachments(String recipientId) {
    personalAttachments.remove(recipientId);
  }

  public List<File> getAllAttachmentsForRecipient(String recipientId) {
    List<File> allAttachments = new ArrayList<>(commonAttachments);
    allAttachments.addAll(getPersonalAttachments(recipientId));
    return allAttachments;
  }

  public int getTotalAttachmentCount() {
    int count = commonAttachments.size();
    for (List<File> personal : personalAttachments.values()) {
      count += personal.size();
    }
    return count;
  }

  public long getTotalSize() {
    long size = 0;
    for (File file : commonAttachments) {
      size += file.length();
    }
    for (List<File> personal : personalAttachments.values()) {
      for (File file : personal) {
        size += file.length();
      }
    }
    return size;
  }
}