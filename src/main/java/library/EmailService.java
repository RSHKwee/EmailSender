package library;

import jakarta.mail.*;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.*;
import kwee.logger.MyLogger;

import java.io.File;
import java.util.List;
import java.util.Properties;
//import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService {
  private static final Logger LOGGER = MyLogger.getLogger();
  // private Consumer<String> logger;
  private String smtpHost;
  private int port;
  private String username;
  private String password;

  public EmailService() {
    // this.logger = logger;
  }

  public void configure(String smtpHost, int port, String username, String password) {
    this.smtpHost = smtpHost;
    this.port = port;
    this.username = username;
    this.password = password;
  }

  public void sendEmail(String to, String cc, String replyTo, String alias, String subject, String message,
      List<File> attachments) throws MessagingException {

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

    MimeMessage mimeMessage = MailMessage.createMail(session, username, to, cc, replyTo, subject, message, attachments,
        alias);

    try {
      // Verzend de e-mail
      Transport.send(mimeMessage);
      LOGGER.log(Level.INFO, "✓ E-mail succesvol verzonden naar: " + to);
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
        LOGGER.log(Level.INFO, "✓ SMTP verbinding succesvol met " + smtpHost + ":" + port);
        return true;
      } finally {
        if (transport != null) {
          transport.close();
        }
      }
    } catch (AuthenticationFailedException e) {
      LOGGER.log(Level.INFO, "✗ Authenticatie mislukt: " + e.getMessage());
      return false;
    } catch (MessagingException e) {
      LOGGER.log(Level.INFO, "✗ Verbinding mislukt: " + e.getMessage());
      return false;
    } catch (Exception e) {
      LOGGER.log(Level.INFO, "✗ Onverwachte fout: " + e.getMessage());
      return false;
    }
  }

  // Extra helper methoden
  public void sendBulkEmails(List<String> recipients, String cc, String replyTo, String alias, String subject,
      String messageTemplate, List<File> commonAttachments,
      java.util.function.Function<String, List<File>> personalAttachmentsProvider) {

    LOGGER.log(Level.INFO, "Bulk verzending gestart voor " + recipients.size() + " ontvangers");

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
        sendEmail(recipient, cc, replyTo, alias, subject, messageTemplate, allAttachments);

        // Kleine pauze om rate limiting te voorkomen
        Thread.sleep(1000);

      } catch (MessagingException e) {
        LOGGER.log(Level.INFO, "✗ Fout bij verzenden naar " + recipient + ": " + e.getMessage());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        LOGGER.log(Level.INFO, "Verzending onderbroken");
        break;
      } catch (Exception e) {
        LOGGER.log(Level.INFO, "✗ Onverwachte fout voor " + recipient + ": " + e.getMessage());
      }
    }

    LOGGER.log(Level.INFO, "Bulk verzending voltooid");
  }

  public void sendEmail(File emlFile) throws MessagingException {
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
      MimeMessage message = MailMessage.convertEmlFile(session, emlFile);
      // Verzend de e-mail
      Transport.send(message);
      LOGGER.log(Level.INFO, "✓ E-mail succesvol verzonden naar: " + message.getRecipients(RecipientType.TO));
    } catch (Exception e) {
      throw new MessagingException("Onverwachte fout: " + e.getMessage(), e);
    }
  }
}