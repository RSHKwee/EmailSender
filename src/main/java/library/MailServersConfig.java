package library;

import java.util.List;
import java.util.ArrayList;

public class MailServersConfig {

  private String applicatie;
  private String versie;
  private List<EmailServer> servers = new ArrayList<>();

  public String getApplicatie() {
    return applicatie;
  }

  public String getVersie() {
    return versie;
  }

  public void setApplicatie(String applicatie) {
    this.applicatie = applicatie;
  }

  public void setVersie(String versie) {
    this.versie = versie;
  }

  // Optioneel: custom methoden kunnen nog steeds toegevoegd worden
  public EmailServer findServerById(String id) {
    return servers.stream().filter(s -> id.equals(s.getId())).findFirst().orElse(null);
  }

  public List<EmailServer> getServers() {
    return servers;
  }

  public void setServers(List<EmailServer> servers) {
    this.servers = servers;
  }

  public static class EmailServer {
    private String id = "";
    private String host = "";
    private int port = -1;
    private String username = "";
    private String password = "";

    public void setEmailServer(EmailServer a_srv) {
      this.id = a_srv.getId();
      this.host = a_srv.getHost();
      this.port = a_srv.getPort();
      this.username = a_srv.getUsername();
      this.password = a_srv.getPassword();
    }

    // Getters en setters...
    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }
}
