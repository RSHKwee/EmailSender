package library;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import kwee.logger.MyLogger;
import main.UserSetting;

public class MailMessage {
  private static final Logger LOGGER = MyLogger.getLogger();

  /**
   * Create mail a mail could also be send to the "from" address. Depends on User
   * setting.
   * 
   * @param session     Session
   * @param from        From address, also BCC address if no Reply to
   * @param to          To address
   * @param cc          CC address
   * @param replyTo     Reply to address, also BCC address
   * @param subject     Subject
   * @param message     Message text
   * @param attachments List of files to attach
   * @param alias       Alias for From and Alias address
   * @return Composed mail (as MimeMessage)
   */
  public static MimeMessage createMail(Session session, String from, String to, String cc, String replyTo,
      String subject, String message, List<File> attachments, String alias) {

    UserSetting m_params = UserSetting.getInstance();
    boolean toBCC = m_params.is_toBCC();

    MimeMessage mimeMessage = new MimeMessage(session);
    try {
      // Initialize headers
      if (alias.isBlank()) {
        mimeMessage.setFrom(new InternetAddress(from));
      } else {
        mimeMessage.setFrom(new InternetAddress(from, alias));
      }

      // CC
      mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
      if (!cc.isBlank()) {
        mimeMessage.setRecipient(Message.RecipientType.CC, new InternetAddress(cc));
      }
      // Reply-to
      if (!replyTo.isBlank()) {
        // BCC -> Reply-to
        if (toBCC) {
          mimeMessage.setRecipient(Message.RecipientType.BCC, new InternetAddress(replyTo));
        }
        if (alias.isBlank()) {
          mimeMessage.setReplyTo(new Address[] { new InternetAddress(replyTo) });
        } else {
          mimeMessage.setReplyTo(new Address[] { new InternetAddress(replyTo, alias) });
        }
      } else {
        // BCC -> FROM
        if (toBCC) {
          mimeMessage.setRecipient(Message.RecipientType.BCC, new InternetAddress(from));
        }
      }
      // Subject
      mimeMessage.setSubject(subject);
      mimeMessage.setSentDate(new Date());
      // Attachments
      if (attachments == null || attachments.isEmpty()) {
        // No attachments
        mimeMessage.setText(message, "utf-8");
      } else {
        // With attachments
        MimeMultipart multipart = new MimeMultipart();

        // Message body
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(message, "utf-8");
        multipart.addBodyPart(textPart);

        // Attachments
        for (File file : attachments) {
          if (file.exists() && file.canRead()) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            FileDataSource source = new FileDataSource(file);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(file.getName());
            multipart.addBodyPart(attachmentPart);
          } else {
            LOGGER.log(Level.INFO, "Waarschuwing: Bestand niet gevonden voor EML: " + file.getPath());
          }
        }
        mimeMessage.setContent(multipart);
      }
    } catch (Exception e) {
      LOGGER.log(Level.INFO, "CreateMimeMessage: " + e.getMessage());
    }
    return mimeMessage;
  }

  public static MimeMessage convertEmlFile(Session session, File emlFile) throws Exception {
    try (InputStream inputStream = new FileInputStream(emlFile)) {
      MimeMessage message = new MimeMessage(session, inputStream);
      return message;
    }
  }
}
