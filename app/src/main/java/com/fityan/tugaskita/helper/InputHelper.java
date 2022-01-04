package com.fityan.tugaskita.helper;

import android.widget.EditText;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputHelper {
  public static final String DATE_FORMAT_HUMAN_US = "EE, MMM dd 'at' hh.mm a";


  /**
   * Get required input value.
   *
   * @param input The input element.
   * @return The input value in string.
   * @throws NullPointerException If value is empty.
   */
  @NonNull
  public static String getRequiredInput(@NonNull EditText input) {
    String value = input.getText().toString();

    if (value.isEmpty()) {
      input.setError(input.getHint() + " is required.");
      throw new NullPointerException(input.getHint() + " is required.");
    }

    return value;
  }


  /**
   * Convert date to string to human readable format (US).
   *
   * @param date The date.
   * @return The formatted date in string.
   */
  @NonNull
  public static String dateToString(Date date) {
    return new SimpleDateFormat(DATE_FORMAT_HUMAN_US, Locale.US).format(date);
  }


  /**
   * Convert date to string by following specified format.
   *
   * @param date   The date.
   * @param format The specified date format.
   * @return The formatted date in string.
   */
  @NonNull
  public static String dateToString(Date date, String format) {
    return new SimpleDateFormat(format, Locale.ROOT).format(date);
  }


  /**
   * Convert date from human readable format (US) string date.
   *
   * @param strDate The suitable string date.
   * @return The date.
   * @throws ParseException If string doesn't match the format.
   */
  public static Date stringToDate(String strDate) throws ParseException {
    return new SimpleDateFormat(DATE_FORMAT_HUMAN_US, Locale.US).parse(strDate);
  }


  /**
   * Convert date from specified format string date.
   *
   * @param strDate The suitable string date.
   * @param format  The specified date format.
   * @return The date.
   * @throws ParseException If string doesn't match the format.
   */
  public static Date stringToDate(String strDate, String format) throws ParseException {
    return new SimpleDateFormat(format, Locale.ROOT).parse(strDate);
  }
}
