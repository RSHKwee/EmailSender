package sandbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import library.MailServerSettings;
import library.MailServersConfig;
import library.MailServersConfig.EmailServer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Gebruik:
public class ExampleMapper {
  public static void main(String[] args) throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    MailServerSettings srvset = new MailServerSettings("gmail");
    srvset.setApplicatie("TestApp", "00000");

    srvset.setHost("Aap");
    srvset.save();
    // Schrijven
    // EmailServersConfig config = new EmailServersConfig();
    // ... voeg servers toe

    // mapper.writeValue(new File("servers.yaml"), config);

    // Lezen
    String cnffile = "D:\\Dev\\Github\\tools\\EmailSender\\config.yaml";
    MailServersConfig gelezen = mapper.readValue(new File(cnffile), MailServersConfig.class);
    List<EmailServer> servers = gelezen.getServers();
    Map<String, EmailServer> servermap = new HashMap<>();
    servers.forEach(s -> {
      servermap.put(s.getId(), s);
    });
    System.out.println();
  }
}
