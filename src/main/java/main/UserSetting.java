package main;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import kwee.logger.MyLogger;

/**
 * User setting persistence.
 * 
 * @author rshkw
 *
 */
public class UserSetting {
  private static UserSetting uniqueInstance;
  private static UserSetting freezeInstance = null;

  private static final Logger LOGGER = MyLogger.getLogger();
  public static String NodePrefName = "kwee.emailsender";

  private String c_Level = "Level";
  private String c_LevelValue = "INFO";
  private String c_ConfirmOnExit = "ConfirmOnExit";
  private String c_toDisk = "ToDisk";
  private String c_LookAndFeel = "LookAndFeel";
  private String c_LookAndFeelVal = "Nimbus";
  private String c_LogDir = "LogDir";
  private String c_Language = "Language";

  private String c_EmlDirectory = "EML_Directory";

  private String m_Level = c_LevelValue;
  private String m_LookAndFeel;
  private String m_LogDir = "";
  private String m_Language = "nl";
  private boolean m_ConfirmOnExit = false;
  private boolean m_toDisk = false;

  private String m_EmlDirectory = "";

  private Preferences pref;
  private Preferences userPrefs = Preferences.userRoot();

  /**
   * Constructor Initialize settings
   */

  /**
   * Get "access" to Singleton.
   * 
   * @return Instance
   */
  public static UserSetting getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new UserSetting();
    }
    return uniqueInstance;
  }

  /**
   * Private constructor and initialization.
   */
  private UserSetting() {
    // Navigate to the preference node that stores the user setting
    pref = userPrefs.node(NodePrefName);

    m_toDisk = pref.getBoolean(c_toDisk, false);
    m_ConfirmOnExit = pref.getBoolean(c_ConfirmOnExit, false);
    m_LookAndFeel = pref.get(c_LookAndFeel, c_LookAndFeelVal);
    m_Language = pref.get(c_Language, "nl");

    m_Level = pref.get(c_Level, c_LevelValue);
    m_LogDir = pref.get(c_LogDir, "");

    m_EmlDirectory = pref.get(c_EmlDirectory, "");

  }

  // Getters for all parameters
  public String get_Language() {
    return m_Language;
  }

  public Level get_Level() {
    return Level.parse(m_Level);
  }

  public String get_LogDir() {
    return m_LogDir;
  }

  public String get_LookAndFeel() {
    return m_LookAndFeel;
  }

  public boolean is_toDisk() {
    return m_toDisk;
  }

  public boolean is_ConfirmOnExit() {
    return m_ConfirmOnExit;
  }

  public String get_EmlDirectory() {
    return m_EmlDirectory;
  }

  // Setters for all parameters.

  public void set_LogDir(String m_LogDir) {
    this.m_LogDir = m_LogDir;
  }

  public void set_Language(String m_Language) {
    this.m_Language = m_Language;
  }

  public void set_toDisk(boolean a_toDisk) {
    pref.putBoolean(c_toDisk, a_toDisk);
    this.m_toDisk = a_toDisk;
  }

  public void set_Level(Level a_Level) {
    pref.put(c_Level, a_Level.toString());
    this.m_Level = a_Level.toString();
  }

  public void set_LookAndFeel(String a_LookAndFeel) {
    pref.put(c_LookAndFeel, a_LookAndFeel);
    this.m_LookAndFeel = a_LookAndFeel;
  }

  public void set_ConfirmOnExit(boolean a_ConfirmOnExit) {
    pref.putBoolean(c_ConfirmOnExit, a_ConfirmOnExit);
    this.m_ConfirmOnExit = a_ConfirmOnExit;
  }

  public void set_EmlDirectory(String a_EmlDirectory) {
    pref.put(c_EmlDirectory, a_EmlDirectory);
    this.m_EmlDirectory = a_EmlDirectory;
  }

  /**
   * Save all settings
   */
  public void save() {
    try {
      pref.putBoolean(c_toDisk, m_toDisk);
      pref.putBoolean(c_ConfirmOnExit, m_ConfirmOnExit);
      pref.put(c_Language, m_Language);
      pref.put(c_LookAndFeel, m_LookAndFeel);
      pref.put(c_Level, m_Level);
      pref.put(c_LogDir, m_LogDir);

      pref.put(c_EmlDirectory, m_EmlDirectory);

      pref.flush();
    } catch (BackingStoreException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }
  }

  /**
   * Copy UserSetings
   * 
   * @return Copy of UserSetings
   */
  public void freeze() {
    if (freezeInstance == null) {
      freezeInstance = new UserSetting();

      freezeInstance.set_toDisk(m_toDisk);
      freezeInstance.set_ConfirmOnExit(m_ConfirmOnExit);
      freezeInstance.set_Language(m_Language);
      freezeInstance.set_LookAndFeel(m_LookAndFeel);
      freezeInstance.set_Level(Level.parse(m_Level));
      freezeInstance.set_LogDir(m_LogDir);

      freezeInstance.set_EmlDirectory(m_EmlDirectory);

    } else {
      LOGGER.log(Level.INFO, "Nothing to freeze....");
    }
  }

  public void unfreeze() {
    if (freezeInstance != null) {
      uniqueInstance.set_toDisk(m_toDisk);

      uniqueInstance.set_ConfirmOnExit(freezeInstance.is_ConfirmOnExit());
      uniqueInstance.set_Language(freezeInstance.get_Language());
      uniqueInstance.set_LookAndFeel(freezeInstance.get_LookAndFeel());
      uniqueInstance.set_Level(freezeInstance.get_Level());
      uniqueInstance.set_LogDir(freezeInstance.get_LogDir());

      uniqueInstance.set_EmlDirectory(freezeInstance.get_EmlDirectory());

      freezeInstance = null;
    } else {
      LOGGER.log(Level.INFO, "Nothing to unfreeze....");
    }
  }

  /**
   * Print settings, convert to a String
   * 
   * @return String with Setting info
   */
  public String print() {
    String l_line = "User setting \n";
    l_line = l_line + "Name: " + pref.name() + "\n";
    l_line = l_line + c_toDisk + ": " + m_toDisk + "\n";
    l_line = l_line + c_Language + ": " + m_Language + "\n";

    l_line = l_line + c_ConfirmOnExit + ": " + m_ConfirmOnExit + "\n";
    l_line = l_line + c_LookAndFeel + ": " + m_LookAndFeel + "\n";
    l_line = l_line + c_Level + ": " + m_Level + "\n";
    l_line = l_line + c_LogDir + ": " + m_LogDir + "\n";

    return l_line;
  }

  // Local functions to convert File to String and vice versa
  private String c_StringDelim = ";";

  /**
   * Convert list of Files to String for storage.
   * 
   * @param a_Files List of Files
   * @return String
   */
  private String FilesToString(File[] a_Files) {
    String l_files = "";
    for (int i = 0; i < a_Files.length; i++) {
      l_files = l_files + a_Files[i].getAbsolutePath() + c_StringDelim;
    }
    return l_files;
  }

  /**
   * Convert String to Files.
   * 
   * @param a_Files String with list of Files.
   * @return List of Files
   */
  private File[] StringToFiles(String a_Files) {
    String[] ls_files = a_Files.split(c_StringDelim);
    File[] l_files = new File[ls_files.length];

    for (int i = 0; i < ls_files.length; i++) {
      File ll_file = new File(ls_files[i]);
      l_files[i] = ll_file;
    }
    return l_files;
  }
}
