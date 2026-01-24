package services;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import java.io.*;

public class JakartaEmlConverter {

  public static MimeMessage convertEmlFile(Session session, File emlFile) throws Exception {
    try (InputStream inputStream = new FileInputStream(emlFile)) {
      MimeMessage message = new MimeMessage(session, inputStream);
      return message;
    }
  }
}
