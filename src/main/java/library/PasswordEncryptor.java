package library;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;
import java.security.SecureRandom;

public class PasswordEncryptor {
  private static final String ALGORITHM = "AES/GCM/NoPadding";

  // Versleutel wachtwoord
  public static String encrypt(String password, SecretKey key) throws Exception {
    byte[] iv = new byte[12]; // GCM aanbevolen IV grootte
    new SecureRandom().nextBytes(iv);

    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));

    byte[] encrypted = cipher.doFinal(password.getBytes("UTF-8"));

    // Combineer IV en versleutelde tekst voor opslag
    byte[] combined = new byte[iv.length + encrypted.length];
    System.arraycopy(iv, 0, combined, 0, iv.length);
    System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

    return Base64.getEncoder().encodeToString(combined);
  }

  // Ontsleutel wachtwoord
  public static String decrypt(String encryptedBase64, SecretKey key) throws Exception {
    byte[] combined = Base64.getDecoder().decode(encryptedBase64);

    byte[] iv = new byte[12];
    byte[] encrypted = new byte[combined.length - 12];
    System.arraycopy(combined, 0, iv, 0, 12);
    System.arraycopy(combined, 12, encrypted, 0, encrypted.length);

    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));

    byte[] decrypted = cipher.doFinal(encrypted);
    return new String(decrypted, "UTF-8");
  }
}
