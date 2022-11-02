package com.fityan.lister.helper;

import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityHelper {
  /**
   * Hide the action bar in specific activity, if exists.
   *
   * @param activity The activity.
   */
  public static void hideActionBar(AppCompatActivity activity) {
    try {
      ActionBar actionBar = activity.getSupportActionBar();

      // Verify if action bar is exists.
      if (actionBar == null)
        throw new NullPointerException(
            "There are no action bar in " + activity.getLocalClassName());

      // Hide the action bar.
      actionBar.hide();
    } catch (NullPointerException e) {
      Log.e("hideActionBar", e.getMessage(), e);
    }
  }
}
