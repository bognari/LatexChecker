package de.tubs.latexTool.core.config;

import com.beust.jcommander.Parameter;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

class ArgsSettings {
  @Parameter(names = {"-cs", "--charset"}, description = "Charset of the tex file")
  String mCharset = Charset.defaultCharset().name();
  @Parameter(names = {"-c", "--config"}, description = "Path to the config files")
  List<String> mConfigs = new LinkedList<>();
  @Parameter(names = {"-dc", "--default"}, description = "Create a default config at the given path")
  String mDefaultConfig = "";
  @Parameter(names = {"-nd", "--noDocument"}, description = "Not limited to the \"Document\" environment")
  boolean mHasNoDocumentEnv = false;
  @Parameter(names = {"-h", "--help"}, help = true, description = "Show all CLI options")
  boolean mHelp = false;
  @Parameter(names = {"-l", "--language"}, description = "Language of the tex file")
  String mLanguage = "en";
  @Parameter(names = {"-lic", "--license"}, description = "Show the license")
  boolean mLicense = false;
  @Parameter(names = {"-ll", "--logLevel"}, description = "The mLog level", validateWith = LogLevelValidator.class, hidden = true)
  String mLogLevel = Level.INFO.getName();
  @Parameter(names = {"-nl", "--newline"}, description = "Print an empty newline after every mLog entry")
  boolean mNewline = false;
  @Parameter(names = {"-s", "-t", "--source", "--tex"}, description = "Path to the tex file")
  String mSource = "";
  @Parameter(names = {"-tp", "--thirdParty"}, description = "Show the third party software")
  boolean mThirdParty = false;
  @Parameter(names = {"-abb", "--abbreviations"}, description = "Abbreviations are escaped with the abbreviations list, set to false to use only the languagetool (maybe it works better or worse for the sentence detection)")
  boolean mUseAbbreviationsEscaping = true;
  @Parameter(names = {"-uc", "--useConverting"}, description = "Use the converting table")
  boolean mUseConverting = true;
  @Parameter(names = {"-v", "--verbose"}, description = "Show \"is alive\" indicators")
  boolean mVerbose = false;
  @Parameter(names = {"-ver", "--version"}, description = "Show the version")
  boolean mVersion = false;
}
