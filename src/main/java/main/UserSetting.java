package main;

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

  private int defaultWidth = 800;
  private int defaultHeight = 600;

  private String c_Level = "Level";
  private String c_LevelValue = "INFO";
  private String c_ConfirmOnExit = "ConfirmOnExit";
  private String c_toDisk = "ToDisk";
  private String c_LookAndFeel = "LookAndFeel";
  private String c_LookAndFeelVal = "Nimbus";
  private String c_LogDir = "LogDir";
  private String c_Language = "Language";

  private String c_Pref_Window_Width = "windowWidth";
  private String c_Pref_Window_Height = "windowHeight";
  private String c_Pref_Window_X = "windowX";
  private String c_Pref_Window_Y = "windowY";
  private String c_Pref_Split_Posion = "splitPosition";

  private String c_EmlDirectory = "EML_Directory";
  private String c_Subject = "Subject";
  private String c_Message = "Message";
  private String c_toBCC = "to_BCC";
  private String c_TimeStamp = "Timestamp";
  private String c_saveEml = "SaveEML";
  private String c_saveOnFail = "SaveOnFail";
  private String c_includeAttachments = "IncludeAttachments";
  private String c_mailConfigDirectory = "MailConfigDirectory";
  private String c_MailProvider = "MailProvider";
  private String c_From = "From";
  private String c_ReplyTo = "Reply-to";
  private String c_Alias = "Alias";
  private String c_CC = "CC";

  private String m_Level = c_LevelValue;
  private String m_LookAndFeel;
  private String m_LogDir = "";
  private String m_Language = "nl";
  private boolean m_ConfirmOnExit = false;
  private boolean m_toDisk = false;

  private int m_Pref_Window_Width = 0;
  private int m_Pref_Window_Height = 0;
  private int m_Pref_Window_X = 0;
  private int m_Pref_Window_Y = 0;
  private int m_Pref_Split_Posion = 0;

  private String m_EmlDirectory = "";
  private String m_Subject = "";
  private String m_Message = "";
  private boolean m_toBCC = true;
  private boolean m_TimeStamp = true;

  private boolean m_saveEml = true;
  private boolean m_saveOnFail = true;
  private boolean m_includeAttachments = true;
  private String m_mailConfigDirectory = "";
  private String m_MailProvider = "";
  private String m_From = "";
  private String m_ReplyTo = "";
  private String m_Alias = "";
  private String m_CC = "";

  private Preferences pref;
  private Preferences userPrefs = Preferences.userRoot();

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

    m_Pref_Window_Width = pref.getInt(c_Pref_Window_Width, defaultWidth);
    m_Pref_Window_Height = pref.getInt(c_Pref_Window_Height, defaultHeight);
    m_Pref_Window_X = pref.getInt(c_Pref_Window_X, -1);
    m_Pref_Window_Y = pref.getInt(c_Pref_Window_Y, -1);
    m_Pref_Split_Posion = pref.getInt(c_Pref_Window_Y, -1);

    m_EmlDirectory = pref.get(c_EmlDirectory, "");
    m_Subject = pref.get(c_Subject, "");
    m_Message = pref.get(c_Message, "");
    m_toBCC = pref.getBoolean(c_toBCC, true);
    m_TimeStamp = pref.getBoolean(c_TimeStamp, true);
    m_saveEml = pref.getBoolean(c_saveEml, true);
    m_saveOnFail = pref.getBoolean(c_saveOnFail, true);
    m_includeAttachments = pref.getBoolean(c_includeAttachments, true);
    m_From = pref.get(c_From, "");
    m_ReplyTo = pref.get(c_ReplyTo, "");
    m_Alias = pref.get(c_Alias, "");
    m_CC = pref.get(c_CC, "");

    String mailCnfDir = System.getProperty("user.home") + "\\emailsender";
    m_mailConfigDirectory = pref.get(c_mailConfigDirectory, mailCnfDir);
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

  public String get_Subject() {
    return m_Subject;
  }

  public String get_Message() {
    return m_Message;
  }

  public boolean is_toBCC() {
    return m_toBCC;
  }

  public boolean is_TimeStamp() {
    return m_TimeStamp;
  }

  public boolean is_saveEml() {
    return m_saveEml;
  }

  public boolean is_saveOnFail() {
    return m_saveOnFail;
  }

  public boolean is_includeAttachments() {
    return m_includeAttachments;
  }

  public String get_mailConfigDirectory() {
    return m_mailConfigDirectory;
  }

  public String get_MailProvider() {
    return m_MailProvider;
  }

  public String get_From() {
    return m_From;
  }

  public String get_ReplyTo() {
    return m_ReplyTo;
  }

  public String get_Alias() {
    return m_Alias;
  }

  public String get_CC() {
    return m_CC;
  }

  public int get_Pref_Window_Width() {
    return m_Pref_Window_Width;
  }

  public int get_Pref_Window_Height() {
    return m_Pref_Window_Height;
  }

  public int get_Pref_Window_X() {
    return m_Pref_Window_X;
  }

  public int get_Pref_Window_Y() {
    return m_Pref_Window_Y;
  }

  public int get_Pref_Split_Posion() {
    return m_Pref_Split_Posion;
  }

  // Setters for all parameters.
  public void set_Subject(String a_Subject) {
    pref.put(c_Subject, a_Subject);
    this.m_Subject = a_Subject;
  }

  public void set_Message(String a_Message) {
    pref.put(c_Message, a_Message);
    this.m_Message = a_Message;
  }

  public void set_LogDir(String a_LogDir) {
    pref.put(c_LogDir, a_LogDir);
    this.m_LogDir = a_LogDir;
  }

  public void set_Language(String a_Language) {
    pref.put(c_Language, a_Language);
    this.m_Language = a_Language;
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

  public void set_toBCC(boolean a_toBCC) {
    pref.putBoolean(c_toBCC, a_toBCC);
    this.m_toBCC = a_toBCC;
  }

  public void set_Timestamp(boolean a_timstamp) {
    pref.putBoolean(c_TimeStamp, a_timstamp);
    this.m_TimeStamp = a_timstamp;
  }

  public void set_saveEml(boolean a_saveEml) {
    pref.putBoolean(c_saveEml, a_saveEml);
    this.m_saveEml = a_saveEml;
  }

  public void set_saveOnFail(boolean a_saveOnFail) {
    pref.putBoolean(c_saveOnFail, a_saveOnFail);
    this.m_saveOnFail = a_saveOnFail;
  }

  public void set_includeAttachments(boolean a_includeAttachments) {
    pref.putBoolean(c_includeAttachments, a_includeAttachments);
    this.m_includeAttachments = a_includeAttachments;
  }

  public void set_mailConfigDirectory(String a_mailConfigDirectory) {
    pref.put(c_mailConfigDirectory, a_mailConfigDirectory);
    this.m_mailConfigDirectory = a_mailConfigDirectory;
  }

  public void set_MailProvider(String a_MailProvider) {
    pref.put(c_MailProvider, a_MailProvider);
    this.m_MailProvider = a_MailProvider;
  }

  public void set_From(String a_From) {
    pref.put(c_From, a_From);
    this.m_From = a_From;
  }

  public void set_ReplyTo(String a_ReplyTo) {
    pref.put(c_ReplyTo, a_ReplyTo);
    this.m_ReplyTo = a_ReplyTo;
  }

  public void set_Alias(String a_Alias) {
    pref.put(c_Alias, a_Alias);
    this.m_Alias = a_Alias;
  }

  public void set_CC(String a_cc) {
    pref.put(c_CC, a_cc);
    this.m_CC = a_cc;
  }

  public void set_Pref_Window_Width(int a_Pref_Window_Width) {
    pref.putInt(c_Pref_Window_Width, a_Pref_Window_Width);
    this.m_Pref_Window_Width = a_Pref_Window_Width;
  }

  public void set_Pref_Window_Heiht(int a_Pref_Window_Height) {
    pref.putInt(c_Pref_Window_Height, a_Pref_Window_Height);
    this.m_Pref_Window_Height = a_Pref_Window_Height;
  }

  public void set_Pref_Window_X(int a_Pref_Window_X) {
    pref.putInt(c_Pref_Window_X, a_Pref_Window_X);
    this.m_Pref_Window_X = a_Pref_Window_X;
  }

  public void set_Pref_Window_Y(int a_Pref_Window_Y) {
    pref.putInt(c_Pref_Window_Y, a_Pref_Window_Y);
    this.m_Pref_Window_Y = a_Pref_Window_Y;
  }

  public void set_Pref_Split_Posion(int a_Pref_Split_Posion) {
    pref.putInt(c_Pref_Split_Posion, a_Pref_Split_Posion);
    this.m_Pref_Split_Posion = a_Pref_Split_Posion;
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

      pref.putInt(c_Pref_Window_Width, m_Pref_Window_Width);
      pref.putInt(c_Pref_Window_Height, m_Pref_Window_Width);
      pref.putInt(c_Pref_Window_X, m_Pref_Window_X);
      pref.putInt(c_Pref_Window_Y, m_Pref_Window_Y);
      pref.putInt(c_Pref_Window_Y, m_Pref_Split_Posion);

      pref.put(c_EmlDirectory, m_EmlDirectory);
      pref.put(c_Subject, m_Subject);
      pref.put(c_Message, m_Message);
      pref.putBoolean(c_toBCC, m_toBCC);
      pref.putBoolean(c_TimeStamp, m_TimeStamp);
      pref.putBoolean(c_saveEml, m_saveEml);
      pref.putBoolean(c_saveOnFail, m_saveOnFail);
      pref.putBoolean(c_includeAttachments, m_includeAttachments);
      pref.put(c_mailConfigDirectory, m_mailConfigDirectory);
      pref.put(c_MailProvider, m_MailProvider);
      pref.put(c_From, m_From);
      pref.put(c_ReplyTo, m_ReplyTo);
      pref.put(c_Alias, m_Alias);
      pref.put(c_CC, m_CC);

      pref.flush();
    } catch (BackingStoreException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }
  }

  /**
   * Copy UserSetings,freeze for testing purpose
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

      freezeInstance.set_Pref_Window_Width(m_Pref_Window_Width);
      freezeInstance.set_Pref_Window_Heiht(m_Pref_Window_Height);
      freezeInstance.set_Pref_Window_X(m_Pref_Window_X);
      freezeInstance.set_Pref_Window_Y(m_Pref_Window_Y);
      freezeInstance.set_Pref_Split_Posion(m_Pref_Split_Posion);

      freezeInstance.set_EmlDirectory(m_EmlDirectory);
      freezeInstance.set_Message(m_Message);
      freezeInstance.set_Subject(m_Subject);
      freezeInstance.set_toBCC(m_toBCC);
      freezeInstance.set_Timestamp(m_TimeStamp);
      freezeInstance.set_saveEml(m_saveEml);
      freezeInstance.set_saveOnFail(m_saveOnFail);
      freezeInstance.set_includeAttachments(m_includeAttachments);
      freezeInstance.set_mailConfigDirectory(m_mailConfigDirectory);
      freezeInstance.set_MailProvider(m_MailProvider);
      freezeInstance.set_From(m_From);
      freezeInstance.set_ReplyTo(m_ReplyTo);
      freezeInstance.set_Alias(m_Alias);
      freezeInstance.set_CC(m_CC);

    } else {
      LOGGER.log(Level.INFO, "Nothing to freeze....");
    }
  }

  /**
   * Restore settings, for testing purpose.
   */
  public void unfreeze() {
    if (freezeInstance != null) {
      uniqueInstance.set_toDisk(m_toDisk);

      uniqueInstance.set_ConfirmOnExit(freezeInstance.is_ConfirmOnExit());
      uniqueInstance.set_Language(freezeInstance.get_Language());
      uniqueInstance.set_LookAndFeel(freezeInstance.get_LookAndFeel());
      uniqueInstance.set_Level(freezeInstance.get_Level());
      uniqueInstance.set_LogDir(freezeInstance.get_LogDir());

      uniqueInstance.set_Pref_Window_Width(freezeInstance.get_Pref_Window_Width());
      uniqueInstance.set_Pref_Window_Heiht(freezeInstance.get_Pref_Window_Height());
      uniqueInstance.set_Pref_Window_X(freezeInstance.get_Pref_Window_X());
      uniqueInstance.set_Pref_Window_Y(freezeInstance.get_Pref_Window_Y());
      uniqueInstance.set_Pref_Split_Posion(freezeInstance.get_Pref_Split_Posion());

      uniqueInstance.set_EmlDirectory(freezeInstance.get_EmlDirectory());
      uniqueInstance.set_Subject(freezeInstance.get_Subject());
      uniqueInstance.set_Message(freezeInstance.get_Message());
      uniqueInstance.set_toBCC(freezeInstance.is_toBCC());
      uniqueInstance.set_Timestamp(freezeInstance.is_TimeStamp());
      uniqueInstance.set_saveEml(freezeInstance.is_saveEml());
      uniqueInstance.set_saveOnFail(freezeInstance.is_saveOnFail());
      uniqueInstance.set_includeAttachments(freezeInstance.is_includeAttachments());
      uniqueInstance.set_mailConfigDirectory(freezeInstance.get_mailConfigDirectory());
      uniqueInstance.set_MailProvider(freezeInstance.get_MailProvider());
      uniqueInstance.set_From(freezeInstance.get_From());
      uniqueInstance.set_ReplyTo(freezeInstance.get_ReplyTo());
      uniqueInstance.set_Alias(freezeInstance.get_Alias());
      uniqueInstance.set_CC(freezeInstance.get_CC());

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

    l_line = l_line + c_Pref_Window_Width + ": " + m_Pref_Window_Width + "\n";
    l_line = l_line + c_Pref_Window_Height + ": " + m_Pref_Window_Width + "\n";
    l_line = l_line + c_Pref_Window_X + ": " + m_Pref_Window_X + "\n";
    l_line = l_line + c_Pref_Window_Y + ": " + m_Pref_Window_Y + "\n";
    l_line = l_line + c_Pref_Window_Y + ": " + m_Pref_Split_Posion + "\n";

    l_line = l_line + c_EmlDirectory + ": " + m_EmlDirectory + "\n";
    l_line = l_line + c_Subject + ": " + m_Subject + "\n";
    l_line = l_line + c_Message + ": " + m_Message + "\n";
    l_line = l_line + c_toBCC + ": " + m_toBCC + "\n";
    l_line = l_line + c_TimeStamp + ": " + m_TimeStamp + "\n";
    l_line = l_line + c_saveEml + ": " + m_saveEml + "\n";
    l_line = l_line + c_saveOnFail + ": " + m_saveOnFail + "\n";
    l_line = l_line + c_includeAttachments + ": " + m_includeAttachments + "\n";
    l_line = l_line + c_mailConfigDirectory + ": " + m_mailConfigDirectory + "\n";
    l_line = l_line + c_MailProvider + ": " + m_MailProvider + "\n";
    l_line = l_line + c_From + ": " + m_From + "\n";
    l_line = l_line + c_ReplyTo + ": " + m_ReplyTo + "\n";
    l_line = l_line + c_Alias + ": " + m_Alias + "\n";
    l_line = l_line + c_CC + ": " + m_CC + "\n";

    return l_line;
  }
}
