package com.fityan.tugaskita.helper;

import android.widget.EditText;

public class InputHelper {
  /**
   * Get required input value.
   *
   * @param input The input element.
   * @return The input value in string.
   * @throws NullPointerException If value is empty.
   */
  public static String getRequiredInput(EditText input) {
    String value = input.getText().toString();

    if (value.isEmpty()) {
      input.setError(input.getHint() + " is required.");
      throw new NullPointerException(input.getHint() + " is required.");
    }

    return value;
  }
}
