package sandbox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import library.KeyGeneratorUtil;
import library.PasswordEncryptor;

public class Main {

  public static void main(String[] args) {
    Map<String, Object> config = new HashMap<String, Object>();
    Map<String, Object> emailConfig = new HashMap<String, Object>();
    // String configFile = "D:\\Data\\Hoevelaken\\configMail.yaml";
    String configFile = "D:\\Dev\\Github\\tools\\EmailSender\\config.yaml";

    String keyFile = "D:\\Data\\Hoevelaken\\configMail.key";

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    try {
      config = mapper.readValue(new File(configFile), Map.class);
      emailConfig = (Map<String, Object>) config.get("servers");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println(config.toString());
    System.out.println(emailConfig.toString());

    config.put("emailother", emailConfig);

    // ======================
    try {
      KeyGeneratorUtil.generateAndSaveKey(keyFile);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    String pasenc = "";
    String passclr = "";
    try {
      SecretKey keyenc = loadEncryptionKey(keyFile);
      pasenc = PasswordEncryptor.encrypt("Baracud2308", keyenc);
      System.out.println(" Encrypted password: " + pasenc);

      passclr = PasswordEncryptor.decrypt(pasenc, keyenc);
      System.out.println("Password clear: " + passclr);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private static SecretKey loadEncryptionKey(String keyPath) throws Exception {
    byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));
    return new SecretKeySpec(keyBytes, "AES");
  }

}
