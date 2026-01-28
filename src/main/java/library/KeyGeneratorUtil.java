package library;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

public class KeyGeneratorUtil {
  public static void generateAndSaveKey(String filePath) throws Exception {
    // Genereer AES-256 sleutel
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(256, new SecureRandom());
    SecretKey secretKey = keyGen.generateKey();

    byte[] keyBytes = secretKey.getEncoded();

    // Opslaan als RAW bytes (geen Base64)
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
      fos.write(keyBytes);
    }

    // Toon Base64 versie voor referentie
    String base64Key = Base64.getEncoder().encodeToString(keyBytes);
    System.out.println("Base64 sleutel (voor referentie): " + base64Key);
    System.out.println("Key lengte: " + keyBytes.length + " bytes");
  }
}