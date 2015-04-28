package com.glomming.shared.mcs.test;

import java.util.List;

public class TestHelper {
  public static final int DEFAULT_NR_ITEMS = 5;
  public static final long ONE_DAY = 1000 * 60 * 60 * 24;
  public static final long TEN_DAYS = ONE_DAY * 10;
  public static final String SOFT_CURRENCY = "SC";
  public static final int SOFT_CURRENCY_PRICE = 321;
  public static final String HARD_CURRENCY = "HC";
  public static final int HARD_CURRENCY_PRICE = 123;

  private static final String ALPHA_NUMERICS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final String NON_ALPHA_NUMERICS = "!@#$%^&*()_+-={}[]\\\"\'|;:?/>.<,";

  public static String getRandomSpecialString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < 8; i++) {
      int index = (int) (Math.random() * (double) NON_ALPHA_NUMERICS.length());
      stringBuilder.append(NON_ALPHA_NUMERICS.charAt(index));
    }
    return getRandomString(8) + stringBuilder.toString();
  }

  public static String getRandomString(int n) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < n; i++) {
      int index = (int) (Math.random() * (double) ALPHA_NUMERICS.length());
      stringBuilder.append(ALPHA_NUMERICS.charAt(index));
    }
    return stringBuilder.toString();
  }

  public static String getRandomString() {
    return getRandomString(16);
  }

  public static String getShortRandomString() {
    return getRandomString(8);
  }

  public static String getLongRandomString() {
    return getRandomString(1024);
  }

  public static String makeCommasSeparatedList(String[] input) {
    boolean first = true;
    StringBuilder sb = new StringBuilder();

    for (String in : input) {
      if (!first)
        sb.append(",");
      sb.append(in);
      first = false;
    }

    return sb.toString();
  }

  public static String makeCommasSeparatedList(List<String> input) {
    boolean first = true;
    StringBuilder sb = new StringBuilder();

    for (String in : input) {
      if (!first)
        sb.append(",");
      sb.append(in);
      first = false;
    }

    return sb.toString();
  }
}
