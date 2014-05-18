package de.tubs.latexTool.core.config;

import com.beust.jcommander.Parameter;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class ArgsSettings {
  @Parameter(names = {"-cs", "--charset"}, description = "Charset of the tex file")
  private String mCharset = Charset.defaultCharset().name();
  @Parameter(names = {"-c", "--config"}, description = "Path to the config files")
  private List<String> mConfigs = new LinkedList<>();
  @Parameter(names = {"--default", "-dc"}, description = "Create a default config at the given path")
  private String mDefaultConfig = "";
  @Parameter(names = {"-nd", "--noDocument"}, description = "Not limited to the \"Document\" environment")
  private boolean mHasNoDocumentEnv = false;
  @Parameter(names = {"--help", "-h"}, help = true, description = "Show all CLI options")
  private boolean mHelp = false;
  @Parameter(names = {"-l", "--language"}, description = "Language of the tex file")
  private String mLanguage = "en";
  @Parameter(names = {"-lic", "--license"}, description = "Show the license")
  private boolean mLicense = false;
  @Parameter(names = {"--logLevel", "-ll"}, description = "The mLog level", validateWith = LogLevelValidator.class, hidden = true)
  private String mLogLevel = Level.INFO.getName();
  @Parameter(names = {"-nl", "--newline"}, description = "Print an empty newline after every mLog entry")
  private boolean mNewline = false;
  @Parameter(names = {"-s", "-t", "--source", "--tex"}, description = "Path to the tex file")
  private String mSource = "";
  @Parameter(names = {"-tp", "--thirdParty"}, description = "Show the third party software")
  private boolean mThirdParty = false;
  @Parameter(names = {"-abb", "--abbreviations"}, description = "Abbrevations are escaped with the abbreations list, set to false to use only the laguagetool (maybe it works better or worse for the sentence detection)")
  private boolean mUseAbbreviationsEscaping = true;
  @Parameter(names = {"--verbose", "-v"}, description = "Show \"is alive\" indicators")
  private boolean mVerbose = false;
  @Parameter(names = {"-ver", "--version"}, description = "Show the version")
  private boolean mVersion = false;

  String getCharset() {
    return mCharset;
  }

  List<String> getConfigs() {
    return mConfigs;
  }

  String getDefaultConfig() {
    return mDefaultConfig;
  }

  String getLanguage() {
    return mLanguage.toLowerCase(Locale.ENGLISH);
  }

  String getLogLevel() {
    return mLogLevel;
  }

  String getSource() {
    return mSource;
  }

  boolean isHasNoDocumentEnv() {
    return mHasNoDocumentEnv;
  }

  boolean isHelp() {
    return mHelp;
  }

  boolean isLicense() {
    return mLicense;
  }

  boolean isNewline() {
    return mNewline;
  }

  boolean isThirdParty() {
    return mThirdParty;
  }

  boolean isUseAbbreviationsEscaping() {
    return mUseAbbreviationsEscaping;
  }

  boolean isVerbose() {
    return mVerbose;
  }

  boolean isVersion() {
    return mVersion;
  }
}
