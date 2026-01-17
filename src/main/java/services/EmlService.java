package services;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class EmlService {
  private Consumer<String> logger;

  public EmlService(Consumer<String> logger) {
    this.logger = logger;
  }

  public File saveAsEml(String from, String to, String subject, String message, List<File> attachments, String saveDir,
      boolean success) {

    File emlFile = null;
    try {
      // Maak directory aan als deze niet bestaat
      File directory = new File(saveDir);
      if (!directory.exists()) {
        if (!directory.mkdirs()) {
          logger.accept("✗ Kan directory niet aanmaken: " + saveDir);
          return null;
        }
      }

      // Genereer bestandsnaam
      String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      String status = success ? "success" : "failed";
      String safeTo = to.replace("@", "_at_").replace(".", "_");
      String filename = String.format("email_%s_%s_%s.eml", safeTo, timestamp, status);

      emlFile = new File(directory, filename);

      // Maak een nieuwe mail sessie
      Session session = Session.getInstance(new Properties());
      MimeMessage mimeMessage = new MimeMessage(session);

      // Stel headers in
      mimeMessage.setFrom(new InternetAddress(from));
      mimeMessage.setRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
      mimeMessage.setSubject(subject);
      mimeMessage.setSentDate(new Date());

      if (attachments == null || attachments.isEmpty()) {
        // Zonder bijlagen
        mimeMessage.setText(message, "utf-8");
      } else {
        // Met bijlagen
        MimeMultipart multipart = new MimeMultipart();

        // Tekst deel
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(message, "utf-8");
        multipart.addBodyPart(textPart);

        // Bijlagen
        for (File file : attachments) {
          if (file.exists() && file.canRead()) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            FileDataSource source = new FileDataSource(file);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(file.getName());
            multipart.addBodyPart(attachmentPart);
          } else {
            logger.accept("Waarschuwing: Bestand niet gevonden voor EML: " + file.getPath());
          }
        }

        mimeMessage.setContent(multipart);
      }

      // Schrijf naar bestand
      try (FileOutputStream fos = new FileOutputStream(emlFile)) {
        mimeMessage.writeTo(fos);
        logger.accept("✓ EML opgeslagen: " + emlFile.getName());
        return emlFile;
      }

    } catch (Exception e) {
      logger.accept("✗ Fout bij opslaan EML voor " + to + ": " + e.getMessage());
      return null;
    }
  }

  public void saveBatchAsEml(String from, List<String> recipients, String subject, String messageTemplate,
      List<File> commonAttachments, java.util.function.Function<String, List<File>> personalAttachmentsProvider,
      String saveDir) {

    logger.accept("Batch EML opslag gestart voor " + recipients.size() + " ontvangers");
    int successCount = 0;
    int failCount = 0;

    for (String recipient : recipients) {
      try {
        // Haal persoonlijke bijlagen op
        List<File> personalAttachments = personalAttachmentsProvider != null
            ? personalAttachmentsProvider.apply(recipient)
            : java.util.Collections.emptyList();

        // Combineer bijlagen
        List<File> allAttachments = new java.util.ArrayList<>(commonAttachments);
        allAttachments.addAll(personalAttachments);

        // Persoonlijk bericht (indien nodig)
        String personalizedMessage = personalizeMessage(messageTemplate, recipient);

        // Sla op als EML
        File savedFile = saveAsEml(from, recipient, subject, personalizedMessage, allAttachments, saveDir, true);

        if (savedFile != null) {
          successCount++;
        } else {
          failCount++;
        }

      } catch (Exception e) {
        logger.accept("✗ Fout bij opslaan EML voor " + recipient + ": " + e.getMessage());
        failCount++;
      }
    }

    logger.accept(String.format("Batch EML opslag voltooid: %d succes, %d mislukt", successCount, failCount));
  }

  private String personalizeMessage(String template, String email) {
    // Eenvoudige personalisatie - kan uitgebreid worden
    String name = extractNameFromEmail(email);
    return template.replace("{naam}", name).replace("{email}", email)
        .replace("{datum}", new SimpleDateFormat("dd-MM-yyyy").format(new Date()))
        .replace("{tijd}", new SimpleDateFormat("HH:mm").format(new Date()));
  }

  private String extractNameFromEmail(String email) {
    try {
      String namePart = email.split("@")[0];
      namePart = namePart.replace(".", " ").replace("_", " ").replace("-", " ");
      // Capitalize first letter of each word
      String[] words = namePart.split(" ");
      StringBuilder result = new StringBuilder();
      for (String word : words) {
        if (!word.isEmpty()) {
          result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
        }
      }
      return result.toString().trim();
    } catch (Exception e) {
      return "Geachte heer/mevrouw";
    }
  }

  public List<File> getEmlFiles(String directory) {
    List<File> emlFiles = new java.util.ArrayList<>();
    File dir = new File(directory);

    if (dir.exists() && dir.isDirectory()) {
      File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".eml"));
      if (files != null) {
        // Sorteer op datum (nieuwste eerst)
        java.util.Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

        for (File file : files) {
          emlFiles.add(file);
        }
      }
    }

    return emlFiles;
  }

  public boolean deleteEmlFile(File emlFile) {
    if (emlFile != null && emlFile.exists()) {
      boolean deleted = emlFile.delete();
      if (deleted) {
        logger.accept("EML bestand verwijderd: " + emlFile.getName());
      } else {
        logger.accept("Kan EML bestand niet verwijderen: " + emlFile.getName());
      }
      return deleted;
    }
    return false;
  }
}