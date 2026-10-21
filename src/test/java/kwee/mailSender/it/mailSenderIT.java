package kwee.mailSender.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kwee.logger.MyLogger;

public class mailSenderIT {
  private static final Logger LOGGER = MyLogger.getLogger();
  private String c_MainClass = "kwee.mailSender.main.Main";
  private String c_MainClassPath = "kwee/mailSender/main/Main.class";

  private String jarPath;
  private String exePath;

  /**
   * Setup IT tests, search for jar
   */
  @BeforeEach
  public void setUp() {
    try {
      jarPath = findJar();
      assertTrue(new File(jarPath).exists());
    } catch (IOException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }
  }

  // In de met Failsafe uitgevoerde integratietest
  @Test
  public void jarShouldHaveCorrectManifest() throws Exception {
    LOGGER.log(Level.INFO, "jarShouldHaveCorrectManifest");
    try (JarFile jar = new JarFile(jarPath)) {
      Manifest manifest = jar.getManifest();
      assertNotNull(manifest);
      assertEquals(c_MainClass, manifest.getMainAttributes().getValue("Main-Class"));
      // Controleer of de hoofdklasse daadwerkelijk in het JAR aanwezig is
      assertNotNull(jar.getEntry(c_MainClassPath));
    }
  }

  @Test
  public void jarShouldStartSuccessfully() throws Exception {
    LOGGER.log(Level.INFO, "jarShouldStartSuccessfull");
    Process process = new ProcessBuilder("java", "-jar", jarPath).redirectErrorStream(true).start();

    // Wacht op het proces, maximaal 5 seconden
    boolean exited = process.waitFor(5, TimeUnit.SECONDS);

    if (exited) {
      // Het proces is afgesloten, wat betekent dat het niet blijft draaien — lees de
      // uitvoer om de foutreden te vinden
      String output = new String(process.getInputStream().readAllBytes());
      fail("Het proces is voortijdig afgesloten, uitvoer:\n" + output);
    } else {
      // Het proces draait nog — opstarten geslaagd
      process.destroyForcibly();
    }
  }

  @Test
  public void exeShouldStartSuccessfully() throws Exception {
    LOGGER.log(Level.INFO, "exeShouldStartSuccessfully");
    try {
      exePath = findExe();
      assertTrue(new File(exePath).exists());
    } catch (IOException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }

    File exe = new File(exePath);
    assertTrue(exe.exists());

    ProcessBuilder pb = new ProcessBuilder(exe.getAbsolutePath());
    pb.redirectErrorStream(true);
    // Werkmap instellen op target, zodat de exe indien nodig nabijgelegen bestanden
    // kan vinden
    pb.directory(exe.getParentFile());
    Process process = pb.start();

    boolean exited = process.waitFor(5, TimeUnit.SECONDS);
    if (exited) {
      String output = new String(process.getInputStream().readAllBytes());
      fail("osmmapper.exe is afgesloten, exitcode " + process.exitValue() + ", uitvoer:\n" + output);
    } else {
      // Verzamel eerst alle childprocessen en beëindig ze
      process.descendants().forEach(ProcessHandle::destroyForcibly);
      // Beëindig vervolgens het hoofdproces
      process.destroyForcibly();
      // Wacht even om te bevestigen dat het proces daadwerkelijk is afgesloten
      process.waitFor(3, TimeUnit.SECONDS);
    }
  }

  // Local
  /**
   * Find jar in target directory.
   * 
   * @return Found jar file
   * @throws IOException When no jar found
   */
  private String findJar() throws IOException {
    Path targetDir = Paths.get("target");
    try (Stream<Path> files = Files.list(targetDir)) {
      //@formatter:off
      return files
          .filter(p -> p.toString().endsWith("dependencies.jar"))
          .filter(p -> !p.toString().endsWith("-sources.jar"))
          .filter(p -> !p.toString().endsWith("-javadoc.jar"))
          .findFirst().map(Path::toString)
          .orElseThrow(() -> new IllegalStateException("Geen JAR-bestand gevonden in target-directory"));
      //@formatter:on
    }
  }

  /**
   * Find executable in target directory.
   * 
   * @return Found executable
   * @throws IOException When no executable found
   */
  private String findExe() throws IOException {
    Path targetDir = Paths.get("target");
    try (Stream<Path> files = Files.list(targetDir)) {
      //@formatter:off
      return files
          .filter(p -> p.toString().endsWith(".exe"))
          .filter(p -> !p.toString().endsWith("setup.exe"))
          .findFirst().map(Path::toString)
          .orElseThrow(() -> new IllegalStateException("Geen EXE-bestand gevonden in target-directory"));
      //@formatter:on
    }
  }
}
