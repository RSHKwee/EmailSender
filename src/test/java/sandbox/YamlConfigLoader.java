package sandbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import library.PasswordEncryptor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class YamlConfigLoader {
  private Map<String, Object> config;
  private SecretKey encryptionKey;

  @SuppressWarnings("unchecked")
  public YamlConfigLoader(String configPath, String keyPath) throws Exception {
    this.encryptionKey = loadEncryptionKey(keyPath);
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    this.config = mapper.readValue(new File(configPath), Map.class);
  }

  public String getDecryptedPassword() throws Exception {
    Map<String, Object> emailConfig = (Map<String, Object>) config.get("email");
    String encrypted = (String) emailConfig.get("encrypted_password");
    return PasswordEncryptor.decrypt(encrypted, encryptionKey);
  }

  private SecretKey loadEncryptionKey(String keyPath) throws Exception {
    byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));
    return new SecretKeySpec(keyBytes, "AES");
  }
}