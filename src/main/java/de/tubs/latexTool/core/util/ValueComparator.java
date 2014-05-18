package de.tubs.latexTool.core.util;

import java.util.Comparator;
import java.util.Map;

/**
 * Sortiert eine Map den Values nach.
 */
public class ValueComparator implements Comparator<String> {

  private final Map<String, Integer> mBase;

  public ValueComparator(Map<String, Integer> base) {
    mBase = base;
  }

  // Note: this comparator imposes orderings that are inconsistent with equals.
  public int compare(String a, String b) {
    if (mBase.get(a) >= mBase.get(b)) {
      return 1;
    } else {
      return -1;
    } // returning 0 would merge keys
  }
}