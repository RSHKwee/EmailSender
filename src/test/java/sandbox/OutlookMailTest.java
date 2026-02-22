package sandbox;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Connectietest voor Outlook.com met Jakarta Mail en OAuth2 Test zowel IMAP
 * (ophalen) als SMTP (verzenden)
 */
public class OutlookMailTest {

  private static final String IMAP_HOST = "outlook.office365.com";
  private static final String IMAP_PORT = "993";
  private static final String SMTP_HOST = "smtp.office365.com";
  private static final String SMTP_PORT = "587";

  private String email;
  private String accessToken;
  private Session session;

  /**
   * Constructor met je inloggegevens
   * 
   * @param email       Je Outlook e-mailadres
   * @param accessToken Je OAuth2 toegangstoken
   */
  public OutlookMailTest(String email, String accessToken) {
    this.email = email;
    this.accessToken = accessToken;
    this.session = createSession();
  }

  /**
   * Maak een Jakarta Mail sessie met de juiste configuratie
   */
  private Session createSession() {
    Properties props = new Properties();

    // Algemene properties
    props.put("mail.debug", "true"); // Uitgebreide logging
    props.put("mail.debug.auth", "true"); // Authenticatie logging

    // IMAP configuratie
    props.put("mail.imaps.host", IMAP_HOST);
    props.put("mail.imaps.port", IMAP_PORT);
    props.put("mail.imaps.starttls.enable", "true");
    props.put("mail.imaps.ssl.enable", "true");
    props.put("mail.imaps.auth", "true");
    props.put("mail.imaps.auth.mechanisms", "XOAUTH2");

    // Schakel andere IMAP auth methodes uit
    props.put("mail.imaps.auth.login.disable", "true");
    props.put("mail.imaps.auth.plain.disable", "true");

    // SMTP configuratie
    props.put("mail.smtp.host", SMTP_HOST);
    props.put("mail.smtp.port", SMTP_PORT);
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.auth.mechanisms", "XOAUTH2");

    // Schakel andere SMTP auth methodes uit
    props.put("mail.smtp.auth.login.disable", "true");
    props.put("mail.smtp.auth.plain.disable", "true");

    return Session.getInstance(props);
  }

  /**
   * Test de IMAP verbinding (e-mails ophalen)
   */
  public boolean testImapConnection() {
    System.out.println("\n=== TEST 1: IMAP VERBINDING TESTEN ===");
    System.out.println("Verbinden met: " + IMAP_HOST + ":" + IMAP_PORT);

    Store store = null;
    Folder inbox = null;

    try {
      store = session.getStore("imaps");
      store.connect(IMAP_HOST, Integer.parseInt(IMAP_PORT), email, accessToken);
      System.out.println("✅ IMAP verbinding succesvol!");

      inbox = store.getFolder("INBOX");
      inbox.open(Folder.READ_ONLY);

      int messageCount = inbox.getMessageCount();
      int unreadCount = inbox.getUnreadMessageCount();

      System.out.println("   - Totaal berichten in INBOX: " + messageCount);
      System.out.println("   - Ongelezen berichten: " + unreadCount);

      // Toon de eerste 3 berichten als preview
      if (messageCount > 0) {
        System.out.println("\n   Preview laatste berichten:");
        Message[] messages = inbox.getMessages(Math.max(1, messageCount - 2), messageCount);
        for (Message msg : messages) {
          System.out.println("   - " + msg.getSubject() + " (van: " + InternetAddress.toString(msg.getFrom()) + ")");
        }
      }

      return true;

    } catch (MessagingException e) {
      System.out.println("❌ IMAP verbinding mislukt!");
      System.out.println("   Fout: " + e.getMessage());
      e.printStackTrace();
      return false;
    } finally {
      // Netjes opruimen
      try {
        if (inbox != null && inbox.isOpen()) {
          inbox.close(false);
        }
        if (store != null && store.isConnected()) {
          store.close();
        }
      } catch (MessagingException e) {
        System.out.println("   Waarschuwing bij opruimen: " + e.getMessage());
      }
    }
  }

  /**
   * Test de SMTP verbinding (e-mails verzenden)
   */
  public boolean testSmtpConnection() {
    System.out.println("\n=== TEST 2: SMTP VERBINDING TESTEN ===");
    System.out.println("Verbinden met: " + SMTP_HOST + ":" + SMTP_PORT);

    Transport transport = null;

    try {
      transport = session.getTransport("smtp");
      transport.connect(SMTP_HOST, Integer.parseInt(SMTP_PORT), email, accessToken);
      System.out.println("✅ SMTP verbinding succesvol!");
      return true;

    } catch (MessagingException e) {
      System.out.println("❌ SMTP verbinding mislukt!");
      System.out.println("   Fout: " + e.getMessage());
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (transport != null && transport.isConnected()) {
          transport.close();
        }
      } catch (MessagingException e) {
        System.out.println("   Waarschuwing bij opruimen: " + e.getMessage());
      }
    }
  }

  /**
   * Stuur een test e-mail (optioneel)
   */
  public boolean sendTestEmail(String toAddress) {
    System.out.println("\n=== TEST 3: TEST E-MAIL VERSTUREN ===");

    Transport transport = null;

    try {
      // Maak een testbericht
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(email));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
      message.setSubject("Test e-mail van OutlookMailTest");
      message.setText("Dit is een test e-mail verzonden via Jakarta Mail met OAuth2.\n\n"
          + "Als je dit bericht ontvangt, werkt de SMTP verbinding correct!");

      // Verbind en verstuur
      transport = session.getTransport("smtp");
      transport.connect(SMTP_HOST, Integer.parseInt(SMTP_PORT), email, accessToken);
      transport.sendMessage(message, message.getAllRecipients());

      System.out.println("✅ Test e-mail succesvol verzonden naar: " + toAddress);
      return true;

    } catch (MessagingException e) {
      System.out.println("❌ Versturen test e-mail mislukt!");
      System.out.println("   Fout: " + e.getMessage());
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (transport != null && transport.isConnected()) {
          transport.close();
        }
      } catch (MessagingException e) {
        System.out.println("   Waarschuwing bij opruimen: " + e.getMessage());
      }
    }
  }

  /**
   * Help functie voor veelvoorkomende foutmeldingen
   */
  public static void analyzeError(String errorMessage) {
    System.out.println("\n=== FOUTANALYSE ===");

    if (errorMessage.contains("535 5.7.3")) {
      System.out.println("⚠️  FOUT 535 5.7.3 - Authenticatie mislukt:");
      System.out.println("   Mogelijke oorzaken:");
      System.out.println("   • OAuth token is verlopen");
      System.out.println("   • Token heeft niet de juiste scopes (SMTP.Send ontbreekt)");
      System.out.println("   • Gebruikersnaam formaat is incorrect");
      System.out.println("\n   Oplossing:");
      System.out.println("   • Vraag een nieuw token aan met scopes:");
      System.out.println("     - https://outlook.office.com/IMAP.AccessAsUser.All");
      System.out.println("     - https://outlook.office.com/SMTP.Send");
      System.out.println("     - openid");

    } else if (errorMessage.contains("451 4.7.0")) {
      System.out.println("⚠️  FOUT 451 4.7.0 - Tijdelijke serverfout:");
      System.out.println("   Mogelijke oorzaak:");
      System.out.println("   • 'openid' scope ontbreekt in token");
      System.out.println("\n   Oplossing:");
      System.out.println("   • Voeg 'openid' toe aan je token scopes");

    } else if (errorMessage.contains("connection timeout") || errorMessage.contains("Connection refused")) {
      System.out.println("⚠️  FOUT - Netwerkverbinding probleem:");
      System.out.println("   Mogelijke oorzaken:");
      System.out.println("   • Firewall blokkeert de verbinding");
      System.out.println("   • Proxy vereist maar niet geconfigureerd");
      System.out.println("   • Verkeerde host/poort");

    } else if (errorMessage.contains("AuthenticationFailedException")) {
      System.out.println("⚠️  FOUT - Algemene authenticatiefout:");
      System.out.println("   Controleer:");
      System.out.println("   • Of XOAUTH2 mechanisme is ingeschakeld");
      System.out.println("   • Of andere auth mechanismes zijn uitgeschakeld");
      System.out.println("   • Of het token nog geldig is");
    }
  }

  /**
   * Hoofdprogramma om de test uit te voeren
   */
  public static void main(String[] args) {
    // Vervang deze waarden met je eigen gegevens
    String jouwEmail = "hoevelaken.duurzaam@outlook.com";
    String jouwToken = "qfygmplzhboujbza";
    String testOntvanger = "rsh.kwee@gmail.com"; // Optioneel: voor test e-mail

    System.out.println("=================================");
    System.out.println("OUTLOOK MAIL CONNECTIETEST");
    System.out.println("=================================");
    System.out.println("E-mail: " + jouwEmail);
    System.out.println("Token: " + (jouwToken.length() > 10 ? jouwToken.substring(0, 10) + "..." : "te kort"));

    // Maak test object
    OutlookMailTest test = new OutlookMailTest(jouwEmail, jouwToken);

    // Voer tests uit
    boolean imapOk = test.testImapConnection();
    boolean smtpOk = test.testSmtpConnection();

    // Optioneel: stuur een test e-mail
    boolean sendOk = true;
    if (imapOk && smtpOk && !testOntvanger.equals("test@voorbeeld.com")) {
      sendOk = test.sendTestEmail(testOntvanger);
    } else if (imapOk && smtpOk) {
      System.out.println("\nℹ️  Geen test e-mail verstuurd (wijzig testOntvanger om dit te testen)");
    }

    // Samenvatting
    System.out.println("\n=================================");
    System.out.println("TEST SAMENVATTING");
    System.out.println("=================================");
    System.out.println("IMAP verbinding: " + (imapOk ? "✅ GELUKT" : "❌ MISLUKT"));
    System.out.println("SMTP verbinding: " + (smtpOk ? "✅ GELUKT" : "❌ MISLUKT"));
    if (!testOntvanger.equals("test@voorbeeld.com")) {
      System.out.println("E-mail versturen: " + (sendOk ? "✅ GELUKT" : "❌ MISLUKT"));
    }

    // Als er fouten zijn, probeer analyse
    if (!imapOk || !smtpOk || !sendOk) {
      analyzeError("Controleer de debug output hierboven voor specifieke foutmeldingen");
    }

    System.out.println("\nℹ️  Bekijk de debug output boven deze samenvatting voor details.");
  }
}