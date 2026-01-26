package library;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import kwee.library.FileUtils;
import kwee.logger.MyLogger;

import library.MailServersConfig.EmailServer;
import main.UserSetting;

public class MailServerSettings {
  private static final Logger LOGGER = MyLogger.getLogger();
  private UserSetting m_params = UserSetting.getInstance();
  private SecretKey keyenc;

  private String c_AppName = "EmailSender";
  private String c_ConfigFile = c_AppName + ".yaml";
  private String c_KeyFile = c_AppName + ".key";

  private String configFileName = "";
  private MailServersConfig mailServersCnf = null;
  private String id = "";

  public MailServerSettings() {
    mailServersCnf = new MailServersConfig();
    mailServersCnf.setApplicatie(c_AppName);
    mailServersCnf.setVersie("unknown");
  }

  /**
   * Constructor
   * 
   * @param a_id Active server
   */
  public MailServerSettings(String a_id) {
    load();
    setId(a_id);
  }

  public void setId(String a_id) {
    EmailServer srv = mailServersCnf.findServerById(id);
    if (srv == null) {
      List<EmailServer> srvs = mailServersCnf.getServers();
      srv = new EmailServer();
      srv.setId(a_id);
      srvs.add(srv);
      mailServersCnf.setServers(srvs);
    }
    id = a_id;
  }

  public void setApplicatie(String app, String versie) {
    mailServersCnf.setApplicatie(app);
    mailServersCnf.setVersie(versie);
  }

  /**
   * c_ConfigFile in config directory
   */
  public void load() {
    // Load configuration file
    configFileName = m_params.get_mailConfigDirectory() + "\\" + c_ConfigFile;
    File f_ConfigFile = new File(configFileName);
    if (f_ConfigFile.exists()) {
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      try {
        mailServersCnf = mapper.readValue(f_ConfigFile, MailServersConfig.class);
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
    }

    // Load encryption key
    FileUtils.checkCreateDirectory(m_params.get_mailConfigDirectory());
    String s_KeyFile = m_params.get_mailConfigDirectory() + "\\" + c_KeyFile;
    File f_KeyFile = new File(s_KeyFile);
    if (!f_KeyFile.exists()) {
      try {
        KeyGeneratorUtil.generateAndSaveKey(s_KeyFile);
        keyenc = loadEncryptionKey(s_KeyFile);
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
    } else {
      try {
        keyenc = loadEncryptionKey(s_KeyFile);
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
    }
  }

  /**
   * Save configuration
   */
  public void save() {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    FileUtils.checkCreateDirectory(m_params.get_mailConfigDirectory());
    try {
      FileUtils.backupFile(configFileName);
      mapper.writeValue(new File(configFileName), mailServersCnf);
    } catch (StreamWriteException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    } catch (DatabindException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
  }

  // == Getters =============================
  public String getHost() {
    EmailServer srv = mailServersCnf.findServerById(id);
    return srv.getHost();
  }

  public int getPort() {
    EmailServer srv = mailServersCnf.findServerById(id);
    return srv.getPort();
  }

  public String getUsername() {
    EmailServer srv = mailServersCnf.findServerById(id);
    return srv.getUsername();
  }

  public String getPassword() {
    EmailServer srv = mailServersCnf.findServerById(id);
    String clrPassword = "";
    try {
      clrPassword = PasswordEncryptor.decrypt(srv.getPassword(), keyenc);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
    return clrPassword;
  }

  public List<String> getIds() {
    List<EmailServer> srvList = mailServersCnf.getServers();
    List<String> idList = new ArrayList<String>();
    srvList.forEach(srv -> {
      idList.add(srv.getId());
    });
    return idList;
  }

  // == Setters =============================
  public void setHost(String host) {
    EmailServer srv = mailServersCnf.findServerById(id);
    srv.setHost(host);
    modifyServer(srv);
  }

  public void setPort(int port) {
    EmailServer srv = mailServersCnf.findServerById(id);
    srv.setPort(port);
    modifyServer(srv);
  }

  public void setUsername(String username) {
    EmailServer srv = mailServersCnf.findServerById(id);
    srv.setUsername(username);
    modifyServer(srv);
  }

  public void setPassword(String password) {
    EmailServer srv = mailServersCnf.findServerById(id);
    String crptPassword = password;
    try {
      crptPassword = PasswordEncryptor.encrypt(password, keyenc);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
    srv.setPassword(crptPassword);
    modifyServer(srv);
  }

  // ====== private =========================
  private static SecretKey loadEncryptionKey(String keyPath) throws Exception {
    byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));
    return new SecretKeySpec(keyBytes, "AES");
  }

  private void modifyServer(EmailServer a_srv) {
    List<EmailServer> srvList = mailServersCnf.getServers();
    List<EmailServer> n_srvList = new ArrayList<EmailServer>();
    Map<String, EmailServer> servermap = new HashMap<String, EmailServer>();
    srvList.forEach(srv -> {
      servermap.put(srv.getId(), srv);
    });
    servermap.put(a_srv.getId(), a_srv);
    Set<String> keys = servermap.keySet();
    keys.forEach(key -> {
      n_srvList.add(servermap.get(key));
    });
    mailServersCnf.setServers(n_srvList);
  }
}
