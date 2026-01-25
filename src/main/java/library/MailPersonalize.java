package library;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import models.EmailRecipient;

public class MailPersonalize {

  /**
   * 
   * @return List of Tags
   */
  static public String[] getTags() {
    String[] variables = { "{achternaam}", "{datum}", "{email}", "{id}", "{naam}", "{plaats}", "{postcode}",
        "{straat_nr}", "{tijd}", "{voornaam}" };
    return variables;
  }

  /**
   * 
   * @param template  Text with tags
   * @param recipient Data for tags
   * @return Personalized string, tags are replaced
   */
  static public String personalizeMessage(String template, EmailRecipient recipient) {
    String lstr = template.replace("{naam}", recipient.getNaam());
    lstr = lstr.replace("{voornaam}", recipient.getVoornaam());
    lstr = lstr.replace("{achternaam}", recipient.getAchternaam());
    lstr = lstr.replace("{straat_nr}", recipient.getStraatHnr());
    lstr = lstr.replace("{postcode}", recipient.getPostcode());
    lstr = lstr.replace("{plaats}", recipient.getPlaats());

    lstr = lstr.replace("{email}", recipient.getEmail());
    lstr = lstr.replace("{id}", recipient.getId());

    // Current date and time, with custom format
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDateTime now = LocalDateTime.now();
    lstr = lstr.replace("{datum}", now.format(formatter));

    formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    lstr = lstr.replace("{tijd}", now.format(formatter));

    return lstr;
  }
}
