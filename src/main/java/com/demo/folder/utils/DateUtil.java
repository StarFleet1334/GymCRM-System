package com.demo.folder.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  public static Date parseDate(String dateStr) {
    try {
      return DATE_FORMAT.parse(dateStr);
    } catch (ParseException e) {
      System.out.println("Invalid date format. Please use yyyy-MM-dd.");
      return null;
    }
  }
}
