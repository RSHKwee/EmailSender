package sandbox;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class JakartaMailExample {
    public static void main(String[] args) {
        Properties props = new Properties();
        
        // SMTP configuratie
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        
        // Jakarta Mail onderhandelt automatisch authenticatie!
        Session session = Session.getInstance(props,
            new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        "user@example.com", 
                        "password"
                    );
                }
            });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("user@example.com"));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("recipient@example.com"));
            message.setSubject("Jakarta Mail Test");
            message.setText("Hello from Jakarta Mail!");
            
            Transport.send(message);
            System.out.println("Bericht verstuurd!");
            
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}