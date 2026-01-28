package library;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class YamlMailServer {

  public void loadYaml() {

  }

  /**
   * 
   */
  public void saveYaml() {
    try {
      // Configure YAML factory with options
      YAMLFactory yamlFactory = YAMLFactory.builder().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER) // Geen ---
                                                                                                            // aan begin
          .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES) // Minimale quotes
          .enable(YAMLGenerator.Feature.INDENT_ARRAYS) // Arrays inspringen
          .enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE) // Literal block style voor strings
          .build();

      ObjectMapper yamlMapper = new ObjectMapper(yamlFactory);

      // Create configuration
      Map<String, Object> config = new LinkedHashMap<>();
      config.put("applicatie", "MijnApplicatie");
      config.put("versie", "2.1.0");

      Map<String, Object> database = new LinkedHashMap<>();
      database.put("url", "jdbc:postgresql://localhost:5432/mydb");
      database.put("gebruiker", "admin");
      database.put("wachtwoord", "geheim123");
      database.put("poolSize", 10);
      config.put("database", database);

      database.put("url", "jdbc:postgresql://localhost:52/mydb");
      database.put("gebruiker", "adminhh");
      database.put("wachtwoord", "geheim123hhhh");
      database.put("poolSize", 1550);
      config.put("database", database);

      Map<String, Object> server = new LinkedHashMap<>();
      server.put("poort", 8080);
      server.put("timeout", 30000);
      server.put("ssl", true);
      config.put("server", server);

      List<Map<String, Object>> gebruikers = new ArrayList<>();
      gebruikers.add(Map.of("id", 1, "naam", "Jan", "rol", "admin"));
      gebruikers.add(Map.of("id", 2, "naam", "Piet", "rol", "gebruiker"));
      config.put("gebruikers", gebruikers);

      // Schrijf naar bestand
      yamlMapper.writeValue(new File("config.yaml"), config);

      // Toon in console
      String yaml = yamlMapper.writeValueAsString(config);
      System.out.println(yaml);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
