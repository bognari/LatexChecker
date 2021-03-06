package de.tubs.latexTool.core.util;

import de.tubs.latexTool.core.Api;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse verwaltet Abkürzungen für verschiedene Sprachen.
 * <p/>
 * grep -o -E '^(([^ ])+\.( ([^ ])+\.)*)' abb | xargs printf "\"%s\", " > nabb
 */
public final class Abbreviation {

  private static final Object sLock = new Object();
  private static final Logger sLog = Logger.getLogger(Abbreviation.class.getName());
  /**
   * Dies ist eine absteigend sortierte Menge der Abkürzungen, nach Anzahl der Punkte
   */
  private static SortedSet<String> sAbbreviations = new TreeSet<>(new DotsComparator<String>('.'));
  /**
   * Tabelle für das Demaskieren
   */
  private static SortedMap<Pattern, String> sFrom = new TreeMap<>(new DotsComparator<Pattern>('#'));
  private static volatile boolean sIsNotReady = true;
  /**
   * Tabelle für das Maskieren
   */
  private static SortedMap<Pattern, String> sTo = new TreeMap<>(new DotsComparator<Pattern>('.'));

  private Abbreviation() {
  }

  /**
   * Gibt eine Sortierte Menge der Abkürzungen zurück
   *
   * @return sTo
   */
  public static SortedSet<String> geAbbreviations() {
    if (sIsNotReady) {
      synchronized (sLock) {
        if (sIsNotReady) {
          sLog.fine("getTo -> build");
          build();
        }
      }
    }
    return sAbbreviations;
  }

  /**
   * Erstellt alle Tabellen usw
   */
  private static void build() {
    String language = Api.settings().getLanguage();
    Map<String, List<String>> abbs = Api.settings().getAbb();

    if (abbs.get(language) != null) {
      if (sLog.isLoggable(Level.FINE)) {
        sLog.fine(String.format("buildDefault: start reading language = %s", language));
      }
      String masked;
      for (String abb : abbs.get(language)) {
        abb = abb.replaceAll("(\\(|\\))", "\\\\$1");
        abb = abb.replaceAll("([^\\s|\\.]+)\\s*\\.", "($1)\\\\. ");
        abb = abb.replaceAll("\\s*?\\\\\\)", "\\\\)");
        abb = abb.replaceAll("\\s+", " ").trim();

        if (!sAbbreviations.contains(abb)) {
          sAbbreviations.add(abb);
          abb = abb.replaceAll("\\s+", "(\\\\s\\*)");

          masked = abb.replaceAll(Pattern.quote("."), "#");
          Pattern to = Pattern.compile(Character.isLowerCase(abb.charAt(1)) ? String.format("\\b%s", abb) : abb, Pattern.CASE_INSENSITIVE);
          Pattern from = Pattern.compile(Character.isLowerCase(masked.charAt(1)) ? String.format("\\b%s", masked) : masked, Pattern.CASE_INSENSITIVE);

          sTo.put(to, pattern2groups(masked));
          sFrom.put(from, pattern2groups(abb));
        }
      }
      if (sLog.isLoggable(Level.FINE)) {
        sLog.fine(String.format("buildDefault: finish reading language = %s", language));
      }
    } else {
      sLog.warning(String.format("buildDefault: language = %s in config is not supported", language));
    }

    sTo = Collections.unmodifiableSortedMap(sTo);
    sFrom = Collections.unmodifiableSortedMap(sFrom);
    sAbbreviations = Collections.unmodifiableSortedSet(sAbbreviations);

    sIsNotReady = false;
  }

  private static String pattern2groups(String pattern) {
    StringBuilder stringBuilder = new StringBuilder();

    Pattern p = Pattern.compile("(\\(.*?\\))|(\\.|#)");
    Matcher matcher = p.matcher(pattern);

    int i = 1;
    while (matcher.find()) {
      if (".".equals(matcher.group()) || "#".equals(matcher.group())) {
        stringBuilder.append(matcher.group());
      } else {
        stringBuilder.append("$").append(i);
        i++;
      }
    }

    return stringBuilder.toString();
  }

  /**
   * Gibt den Eintrag der Demaskierungs-Tabelle zurück
   *
   * @return sFrom.get(input);
   */
  public static String getFrom(Pattern input) {
    if (sIsNotReady) {
      synchronized (sLock) {
        if (sIsNotReady) {
          sLog.fine("getFrom -> build");
          build();
        }
      }
    }
    return sFrom.get(input);
  }

  /**
   * Gibt eine geordnete Menge der Abkürzungen zurück
   *
   * @return sFrom.keySet();
   */
  public static Set<Pattern> getFromPatterns() {
    if (sIsNotReady) {
      synchronized (sLock) {
        if (sIsNotReady) {
          sLog.fine("getFromPatterns -> build");
          build();
        }
      }
    }
    return sFrom.keySet();
  }

  /**
   * Gibt den Eintrag der Maskierungs-Tabelle zurück
   *
   * @return sTo
   */
  public static String getTo(Pattern input) {
    if (sIsNotReady) {
      synchronized (sLock) {
        if (sIsNotReady) {
          sLog.fine("getTo -> build");
          build();
        }
      }
    }
    return sTo.get(input);
  }

  /**
   * Gibt eine geordnete Menge der Abkürzungen zurück
   *
   * @return sTo.keySet();
   */
  public static Set<Pattern> getToPatterns() {
    if (sIsNotReady) {
      synchronized (sLock) {
        if (sIsNotReady) {
          sLog.fine("getToPatterns -> build");
          build();
        }
      }
    }
    return sTo.keySet();
  }

  /**
   * Maskiert alle bekannten Abkürzungen
   *
   * @param input
   * @return
   */
  public static String masking(String input) {
    int length = input.length();
    for (Pattern abb : getToPatterns()) {
      input = abb.matcher(input).replaceAll(getTo(abb));
    }
    if (length != input.length()) {
      assert length == input.length() : "Laenge wurde veraendert! - masking";
    }
    return input;
  }

  /**
   * Unmaskiert die Abkürzungen
   *
   * @param input
   * @return
   */
  public static String unmasking(String input) {
    int length = input.length();
    if (!input.isEmpty()) {
      for (Pattern abb : getFromPatterns()) {
        input = abb.matcher(input).replaceAll(getFrom(abb));
      }
    }
    assert length == input.length() : "Laenge wurde veraendert! - unmasking";
    return input;
  }
}
