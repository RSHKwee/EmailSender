package services;

import java.io.File;
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

public class CreateMailMessage {
  private static final Logger LOGGER = MyLogger.getLogger();

  /**
   * 
   * @param session
   * @param from
   * @param to
   * @param cc
   * @param replyTo
   * @param subject
   * @param message
   * @param attachments
   * @param alias
   * @return
   */
  public static MimeMessage createMail(Session session, String from, String to, String cc, String replyTo,
      String subject, String message, List<File> attachments, String alias) {

    MimeMessage mimeMessage = new MimeMessage(session);
    try {
      // Stel headers in
      if (alias.isBlank()) {
        mimeMessage.setFrom(new InternetAddress(from));
      } else {
        mimeMessage.setFrom(new InternetAddress(from, alias));
      }

      mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
      if (!cc.isBlank()) {
        mimeMessage.setRecipient(Message.RecipientType.CC, new InternetAddress(cc));
      }
      if (!replyTo.isBlank()) {
        if (alias.isBlank()) {
          mimeMessage.setReplyTo(new Address[] { new InternetAddress(replyTo) });
        } else {
          mimeMessage.setReplyTo(new Address[] { new InternetAddress(replyTo, alias) });
        }
      }
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
}
