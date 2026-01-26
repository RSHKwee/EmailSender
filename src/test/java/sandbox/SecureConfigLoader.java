package sandbox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import library.PasswordEncryptor;

public class SecureConfigLoader {
  private Properties props;
  private SecretKey encryptionKey;

  public SecureConfigLoader(String keyPath) throws Exception {
    // Laad versleutelingssleutel (bijv. uit apart bestand)
    this.encryptionKey = loadEncryptionKey(keyPath);
    this.props = new Properties();

    try (InputStream input = new FileInputStream("email_config.properties")) {
      props.load(input);
    }
  }

  public String getDecryptedPassword() throws Exception {
    String encrypted = props.getProperty("email.encrypted.password");
    if (encrypted.startsWith("ENC(") && encrypted.endsWith(")")) {
      encrypted = encrypted.substring(4, encrypted.length() - 1);
    }
    return PasswordEncryptor.decrypt(encrypted, encryptionKey);
  }

  public String getHost() {
    return props.getProperty("email.host");
  }

  public int getPort() {
    return Integer.parseInt(props.getProperty("email.port"));
  }

  public String getUsername() {
    return props.getProperty("email.username");
  }

  private SecretKey loadEncryptionKey(String keyPath) throws Exception {
    byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));
    return new SecretKeySpec(keyBytes, "AES");
  }
}
