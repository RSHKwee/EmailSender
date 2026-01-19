package models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmailRecipient {
  private String email;
  private String id;
  private String voornaam;
  private String achternaam;

  private String straat_huisnr = "";
  private String postcode = "";
  private String plaats = "";

  private List<File> personalAttachments;
  private boolean enabled;

  public EmailRecipient(String email) {
    this.email = email;
    this.id = generateId(email);
    extractNameFromEmail(email);
    this.personalAttachments = new ArrayList<>();
    this.enabled = true;
  }

  public EmailRecipient(String email, String voornaam, String achternaam) {
    this.email = email;
    this.voornaam = voornaam;
    this.achternaam = achternaam;
    this.id = generateId(email);
    this.personalAttachments = new ArrayList<>();
    this.enabled = true;
  }

  private String generateId(String email) {
    return email.replace("@", "_at_").replace(".", "_");
  }

  private void extractNameFromEmail(String email) {
    String namePart = email.split("@")[0];
    namePart = namePart.replace(".", " ").replace("_", " ");
    String[] parts = namePart.split(" ");
    if (parts.length >= 2) {
      this.voornaam = parts[0];
      this.achternaam = parts[1];
    } else {
      this.voornaam = namePart;
      this.achternaam = "";
    }
  }

  // Getters and Setters
  public String getEmail() {
    return email;
  }

  public String getId() {
    return id;
  }

  public String getVoornaam() {
    return voornaam != null ? voornaam : "";
  }

  public String getAchternaam() {
    return achternaam != null ? achternaam : "";
  }

  public String getNaam() {
    return (voornaam != null ? voornaam + " " : "") + (achternaam != null ? achternaam : "");
  }

  public String getStraatHnr() {
    return straat_huisnr != null ? straat_huisnr : "";
  }

  public String getPlaats() {
    return plaats != null ? plaats : "";
  }

  public String getPostcode() {
    return postcode != null ? postcode : "";
  }

  public List<File> getPersonalAttachments() {
    return personalAttachments;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setVoornaam(String voornaam) {
    this.voornaam = voornaam;
  }

  public void setAchternaam(String achternaam) {
    this.achternaam = achternaam;
  }

  public void setStraatHnr(String straatHnr) {
    this.straat_huisnr = straatHnr;
  }

  public void setPlaats(String plaats) {
    this.plaats = plaats;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void addPersonalAttachment(File filePath) {
    if (!personalAttachments.contains(filePath)) {
      personalAttachments.add(filePath);
    }
  }

  public void removePersonalAttachment(File filePath) {
    personalAttachments.remove(filePath);
  }

  @Override
  public String toString() {
    return email + (voornaam != null ? " (" + voornaam + " " + achternaam + ")" : "");
  }
}