package com.fityan.tugaskita.helper;

import android.widget.EditText;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputHelper {
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
   * Convert date to string by following "yyyy/MM/dd HH:mm" format.
   *
   * @param date The date.
   * @return The formatted date in string.
   */
  @NonNull
  public static String dateToString(Date date) {
    return new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).format(date);
  }


  /**
   * Convert string that following "yyyy/MM/dd HH:mm" format to date.
   *
   * @param strDate The suitable string date.
   * @return The date.
   * @throws ParseException If string doesn't match the format.
   */
  public static Date stringToDate(String strDate) throws ParseException {
    return new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).parse(strDate);
  }
}
