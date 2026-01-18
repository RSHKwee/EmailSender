package models;

import java.util.ArrayList;
import java.util.List;

public class EmailRecipient {
  private String email;
  private String id;
  private String name;
  private String firstName;
  private String lastName;
  private String description;

  private List<String> personalAttachments;
  private boolean enabled;

  public EmailRecipient(String email) {
    this.email = email;
    this.id = generateId(email);
    this.name = extractNameFromEmail(email);
    this.personalAttachments = new ArrayList<>();
    this.enabled = true;
  }

  private String generateId(String email) {
    return email.replace("@", "_at_").replace(".", "_");
  }

  private String extractNameFromEmail(String email) {
    String namePart = email.split("@")[0];
    namePart = namePart.replace(".", " ").replace("_", " ");
    return namePart.substring(0, 1).toUpperCase() + namePart.substring(1);
  }

  // Getters and Setters
  public String getEmail() {
    return email;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<String> getPersonalAttachments() {
    return personalAttachments;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getDescription() {
    return description;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPersonalAttachments(List<String> personalAttachments) {
    this.personalAttachments = personalAttachments;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void addPersonalAttachment(String filePath) {
    if (!personalAttachments.contains(filePath)) {
      personalAttachments.add(filePath);
    }
  }

  public void removePersonalAttachment(String filePath) {
    personalAttachments.remove(filePath);
  }

  @Override
  public String toString() {
    return email + " (ID: " + id + ")";
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}