package services;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class EmailService {
  private Consumer<String> logger;
  private String smtpHost;
  private int port;
  private String username;
  private String password;

  public EmailService(Consumer<String> logger) {
    this.logger = logger;
  }

  public void configure(String smtpHost, int port, String username, String password) {
    this.smtpHost = smtpHost;
    this.port = port;
    this.username = username;
    this.password = password;
  }

  public void sendEmail(String to, String subject, String message, List<File> attachments) throws MessagingException {

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", String.valueOf(port));

    Session session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    try {
      MimeMessage mimeMessage = new MimeMessage(session);
      mimeMessage.setFrom(new InternetAddress(username));
      mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
      mimeMessage.setSubject(subject);

      if (attachments == null || attachments.isEmpty()) {
        // Zonder bijlagen
        mimeMessage.setText(message, "utf-8");
      } else {
        // Met bijlagen
        Multipart multipart = new MimeMultipart();

        // Tekst deel
        MimeBodyPart messagePart = new MimeBodyPart();
        messagePart.setText(message, "utf-8");
        multipart.addBodyPart(messagePart);

        // Bijlagen
        for (File file : attachments) {
          if (file.exists() && file.canRead()) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            FileDataSource source = new FileDataSource(file);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(file.getName());
            multipart.addBodyPart(attachmentPart);
          } else {
            logger.accept("Waarschuwing: Bestand niet gevonden of niet leesbaar: " + file.getPath());
          }
        }

        mimeMessage.setContent(multipart);
      }

      // Verzend de e-mail
      Transport.send(mimeMessage);
      logger.accept("✓ E-mail succesvol verzonden naar: " + to);

    } catch (AddressException e) {
      throw new MessagingException("Ongeldig e-mailadres: " + to, e);
    } catch (MessagingException e) {
      throw new MessagingException("Fout bij verzenden naar " + to + ": " + e.getMessage(), e);
    } catch (Exception e) {
      throw new MessagingException("Onverwachte fout: " + e.getMessage(), e);
    }
  }

  public boolean testConnection() {
    try {
      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", smtpHost);
      props.put("mail.smtp.port", String.valueOf(port));
      props.put("mail.smtp.connectiontimeout", "10000");
      props.put("mail.smtp.timeout", "10000");

      Session session = Session.getInstance(props, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      });

      // Test de verbinding
      Transport transport = session.getTransport("smtp");
      try {
        transport.connect();
        logger.accept("✓ SMTP verbinding succesvol met " + smtpHost + ":" + port);
        return true;
      } finally {
        if (transport != null) {
          transport.close();
        }
      }
    } catch (AuthenticationFailedException e) {
      logger.accept("✗ Authenticatie mislukt: " + e.getMessage());
      return false;
    } catch (MessagingException e) {
      logger.accept("✗ Verbinding mislukt: " + e.getMessage());
      return false;
    } catch (Exception e) {
      logger.accept("✗ Onverwachte fout: " + e.getMessage());
      return false;
    }
  }

  // Extra helper methoden
  public void sendBulkEmails(List<String> recipients, String subject, String messageTemplate,
      List<File> commonAttachments, java.util.function.Function<String, List<File>> personalAttachmentsProvider) {

    logger.accept("Bulk verzending gestart voor " + recipients.size() + " ontvangers");

    for (String recipient : recipients) {
      try {
        // Haal persoonlijke bijlagen op voor deze ontvanger
        List<File> personalAttachments = personalAttachmentsProvider != null
            ? personalAttachmentsProvider.apply(recipient)
            : java.util.Collections.emptyList();

        // Combineer algemene en persoonlijke bijlagen
        List<File> allAttachments = new java.util.ArrayList<>(commonAttachments);
        allAttachments.addAll(personalAttachments);

        // Verzend de e-mail
        sendEmail(recipient, subject, messageTemplate, allAttachments);

        // Kleine pauze om rate limiting te voorkomen
        Thread.sleep(1000);

      } catch (MessagingException e) {
        logger.accept("✗ Fout bij verzenden naar " + recipient + ": " + e.getMessage());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.accept("Verzending onderbroken");
        break;
      } catch (Exception e) {
        logger.accept("✗ Onverwachte fout voor " + recipient + ": " + e.getMessage());
      }
    }

    logger.accept("Bulk verzending voltooid");
  }
}