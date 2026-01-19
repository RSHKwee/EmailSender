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
  public static String NodePrefName = "kwee.ing2ofx";

  private String c_Level = "Level";
  private String c_LevelValue = "INFO";

  private String c_GnuCashExe = "GnuCashExe";
  private String c_GnuCashExeValue = "C:/Program Files (x86)/gnucash/bin/gnucash.exe";
  private String c_Synonym_file = "SynonymFile";

  private String c_ConfirmOnExit = "ConfirmOnExit";
  private String c_toDisk = "ToDisk";
  // private String c_AccountSepOfx = "AccountSepOfx";
  private String c_ConvertDecimalSeparator = "ConvertDecimalSeperator";
  private String c_ConvertDateFormat = "ConvertDateFormat";
  private String c_SeparatorComma = "SeparatorComma";
  private String c_OutputFolder = "OutputFolder";
  private String c_CsvFiles = "CsvFiles";
  private String c_LookAndFeel = "LookAndFeel";
  private String c_LookAndFeelVal = "Nimbus";
  private String c_Interest = "Interest";
  private String c_Savings = "Savings";
  private String c_Java = "UseJava";
  private String c_LogDir = "LogDir";
  private String c_ClearTransactions = "ClearTransactions";
  private String c_Language = "Language";

  private String m_Level = c_LevelValue;
  private String m_LookAndFeel;
  private String m_GnuCashExecutable = c_LookAndFeelVal;
  private String m_OutputFolder = "";
  private File[] m_CsvFiles = null;
  private String m_LogDir = "";
  private String m_Synonym_file = "";
  private String m_Language = "nl";

  private boolean m_ConfirmOnExit = false;
  private boolean m_toDisk = false;
//  private boolean m_AcountSeparateOFX = true;
  private boolean m_ConvertDecimalSeparator = false;
  private boolean m_ConvertDateFormat = false;
  private boolean m_SeparatorComma = false;
  private boolean m_Interest = true;
  private boolean m_Savings = false;
  private boolean m_Java = true;
  private boolean m_ClearTransactions = true;

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
//    m_AcountSeparateOFX = pref.getBoolean(c_AccountSepOfx, true);
    m_ConvertDecimalSeparator = pref.getBoolean(c_ConvertDecimalSeparator, false);
    m_ConvertDateFormat = pref.getBoolean(c_ConvertDateFormat, false);
    m_SeparatorComma = pref.getBoolean(c_SeparatorComma, false);
    m_Interest = pref.getBoolean(c_Interest, true);
    m_Savings = pref.getBoolean(c_Savings, false);
    m_Java = pref.getBoolean(c_Java, true);
    m_ClearTransactions = pref.getBoolean(c_ClearTransactions, true);

    m_LookAndFeel = pref.get(c_LookAndFeel, c_LookAndFeelVal);
    m_GnuCashExecutable = pref.get(c_GnuCashExe, c_GnuCashExeValue);
    m_OutputFolder = pref.get(c_OutputFolder, "");
    m_Synonym_file = pref.get(c_Synonym_file, "");

    m_Language = pref.get(c_Language, "nl");

    String l_CsvFiles = pref.get(c_CsvFiles, "");
    m_CsvFiles = StringToFiles(l_CsvFiles);

    m_Level = pref.get(c_Level, c_LevelValue);
    m_LogDir = pref.get(c_LogDir, "");
  }

  // Getters for all parameters
  public String get_GnuCashExecutable() {
    return m_GnuCashExecutable;
  }

  public String get_OutputFolder() {
    return m_OutputFolder;
  }

  public File[] get_CsvFiles() {
    return m_CsvFiles;
  }

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

  public String get_Synonym_file() {
    return m_Synonym_file;
  }

  public boolean is_toDisk() {
    return m_toDisk;
  }

  public boolean is_ConfirmOnExit() {
    return m_ConfirmOnExit;
  }

  public boolean is_ConvertDateFormat() {
    return m_ConvertDateFormat;
  }

  public boolean is_ConvertDecimalSeparator() {
    return m_ConvertDecimalSeparator;
  }

  public boolean is_Interest() {
    return m_Interest;
  }

  public boolean is_Savings() {
    return m_Savings;
  }

  public boolean is_SeparatorComma() {
    return m_SeparatorComma;
  }

  public boolean is_Java() {
    return m_Java;
  }

  public boolean is_ClearTransactions() {
    return m_ClearTransactions;
  }

  // Setters for all parameters.
  public void set_GnuCashExecutable(File a_GnuCashExecutable) {
    pref.put(c_GnuCashExe, a_GnuCashExecutable.getAbsolutePath());
    this.m_GnuCashExecutable = a_GnuCashExecutable.getAbsolutePath();
  }

  public void set_LogDir(String m_LogDir) {
    this.m_LogDir = m_LogDir;
  }

  public void set_OutputFolder(File a_OutputFolder) {
    pref.put(c_OutputFolder, a_OutputFolder.getAbsolutePath());
    this.m_OutputFolder = a_OutputFolder.getAbsolutePath();
  }

  public void set_OutputFolder(String a_OutputFolder) {
    pref.put(c_OutputFolder, a_OutputFolder);
    this.m_OutputFolder = a_OutputFolder;
  }

  public void set_CsvFiles(File[] a_CsvFiles) {
    pref.put(c_CsvFiles, FilesToString(a_CsvFiles));
    this.m_CsvFiles = a_CsvFiles;
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

  // public void set_AcountSeparateOFX(boolean a_AcountSeparateOFX) {
  // pref.putBoolean(c_AccountSepOfx, a_AcountSeparateOFX);
//    this.m_AcountSeparateOFX = a_AcountSeparateOFX;
  // }

  public void set_ConvertDecimalSeparator(boolean a_ConvertDecimalSeparator) {
    pref.putBoolean(c_ConvertDecimalSeparator, a_ConvertDecimalSeparator);
    this.m_ConvertDecimalSeparator = a_ConvertDecimalSeparator;
  }

  public void set_ConvertDateFormat(boolean a_ConvertDateFormat) {
    pref.putBoolean(c_ConvertDateFormat, a_ConvertDateFormat);
    this.m_ConvertDateFormat = a_ConvertDateFormat;
  }

  public void set_SeparatorComma(boolean a_SeperatorComma) {
    pref.putBoolean(c_SeparatorComma, a_SeperatorComma);
    this.m_SeparatorComma = a_SeperatorComma;
  }

  public void set_LookAndFeel(String a_LookAndFeel) {
    pref.put(c_LookAndFeel, a_LookAndFeel);
    this.m_LookAndFeel = a_LookAndFeel;
  }

  public void set_Interest(boolean a_Interest) {
    pref.putBoolean(c_Interest, a_Interest);
    this.m_Interest = a_Interest;
  }

  public void set_Savings(boolean a_Savings) {
    pref.putBoolean(c_Savings, a_Savings);
    this.m_Savings = a_Savings;
  }

  public void set_Java(boolean a_Java) {
    pref.putBoolean(c_Java, a_Java);
    this.m_Java = a_Java;
  }

  public void set_ConfirmOnExit(boolean a_ConfirmOnExit) {
    pref.putBoolean(c_ConfirmOnExit, a_ConfirmOnExit);
    this.m_ConfirmOnExit = a_ConfirmOnExit;
  }

  public void set_ClearTransactions(boolean a_ClearTransactions) {
    pref.putBoolean(c_ClearTransactions, a_ClearTransactions);
    this.m_ClearTransactions = a_ClearTransactions;
  }

  /**
   * Save all settings
   */
  public void save() {
    try {
      pref.putBoolean(c_toDisk, m_toDisk);

//      pref.putBoolean(c_AccountSepOfx, m_AcountSeparateOFX);
      pref.putBoolean(c_ConvertDecimalSeparator, m_ConvertDecimalSeparator);
      pref.putBoolean(c_ConvertDateFormat, m_ConvertDateFormat);
      pref.putBoolean(c_SeparatorComma, m_SeparatorComma);
      pref.putBoolean(c_Interest, m_Interest);
      pref.putBoolean(c_Savings, m_Savings);
      pref.putBoolean(c_ConfirmOnExit, m_ConfirmOnExit);
      pref.putBoolean(c_Java, m_Java);
      pref.putBoolean(c_ClearTransactions, m_ClearTransactions);

      pref.put(c_Language, m_Language);
      pref.put(c_LookAndFeel, m_LookAndFeel);
      pref.put(c_GnuCashExe, m_GnuCashExecutable);
      pref.put(c_Synonym_file, m_Synonym_file);
      pref.put(c_OutputFolder, m_OutputFolder);
      pref.put(c_CsvFiles, FilesToString(m_CsvFiles));
      pref.put(c_Level, m_Level);
      pref.put(c_LogDir, m_LogDir);

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

//      freezeInstance.set_AcountSeparateOFX(m_AcountSeparateOFX);
      freezeInstance.set_ConvertDecimalSeparator(m_ConvertDecimalSeparator);
      freezeInstance.set_ConvertDateFormat(m_ConvertDateFormat);
      freezeInstance.set_SeparatorComma(m_SeparatorComma);
      freezeInstance.set_Interest(m_Interest);
      freezeInstance.set_Savings(m_Savings);
      freezeInstance.set_ConfirmOnExit(m_ConfirmOnExit);
      freezeInstance.set_Java(m_Java);
      freezeInstance.set_ClearTransactions(m_ClearTransactions);
      freezeInstance.set_Language(m_Language);

      freezeInstance.set_LookAndFeel(m_LookAndFeel);
      freezeInstance.set_GnuCashExecutable(new File(m_GnuCashExecutable));
      freezeInstance.set_OutputFolder(m_OutputFolder);

      freezeInstance.set_CsvFiles(m_CsvFiles);
      freezeInstance.set_Level(Level.parse(m_Level));
      freezeInstance.set_LogDir(m_LogDir);
    } else {
      LOGGER.log(Level.INFO, "Nothing to freeze....");
    }
  }

  public void unfreeze() {
    if (freezeInstance != null) {
      uniqueInstance.set_toDisk(m_toDisk);

//      uniqueInstance.set_AcountSeparateOFX(freezeInstance.is_AcountSeparateOFX());
      uniqueInstance.set_ConvertDecimalSeparator(freezeInstance.is_ConvertDecimalSeparator());
      uniqueInstance.set_ConvertDateFormat(freezeInstance.is_ConvertDateFormat());
      uniqueInstance.set_SeparatorComma(freezeInstance.is_SeparatorComma());
      uniqueInstance.set_Interest(freezeInstance.is_Interest());
      uniqueInstance.set_Savings(freezeInstance.is_Savings());
      uniqueInstance.set_ConfirmOnExit(freezeInstance.is_ConfirmOnExit());
      uniqueInstance.set_Java(freezeInstance.is_Java());
      uniqueInstance.set_ClearTransactions(freezeInstance.is_ClearTransactions());
      uniqueInstance.set_Language(freezeInstance.get_Language());

      uniqueInstance.set_LookAndFeel(freezeInstance.get_LookAndFeel());
      uniqueInstance.set_GnuCashExecutable(new File(freezeInstance.get_GnuCashExecutable()));
      uniqueInstance.set_OutputFolder(freezeInstance.get_OutputFolder());

      uniqueInstance.set_CsvFiles(freezeInstance.get_CsvFiles());
      uniqueInstance.set_Level(freezeInstance.get_Level());
      uniqueInstance.set_LogDir(freezeInstance.get_LogDir());

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
    l_line = l_line + c_ConvertDecimalSeparator + ": " + m_ConvertDecimalSeparator + "\n";
    l_line = l_line + c_Language + ": " + m_Language + "\n";

    l_line = l_line + c_ConvertDateFormat + ": " + m_ConvertDateFormat + "\n";
    l_line = l_line + c_SeparatorComma + ": " + m_SeparatorComma + "\n";
    l_line = l_line + c_Interest + ": " + m_Interest + "\n";
    l_line = l_line + c_Savings + ": " + m_Savings + "\n";
    l_line = l_line + c_ConfirmOnExit + ": " + m_ConfirmOnExit + "\n";
    l_line = l_line + c_Java + ": " + m_Java + "\n";
    l_line = l_line + c_ClearTransactions + ": " + m_ClearTransactions + "\n";

    l_line = l_line + c_LookAndFeel + ": " + m_LookAndFeel + "\n";
    l_line = l_line + c_GnuCashExe + ": " + m_GnuCashExecutable + "\n";
    l_line = l_line + c_Synonym_file + ": " + m_Synonym_file + "\n";
    l_line = l_line + c_OutputFolder + ": " + m_OutputFolder + "\n";
    l_line = l_line + c_CsvFiles + ": " + FilesToString(m_CsvFiles) + "\n";
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
