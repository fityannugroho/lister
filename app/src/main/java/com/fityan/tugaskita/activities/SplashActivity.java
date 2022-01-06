package com.fityan.tugaskita.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    /* Customize action bar. */
    customizeActionBar();

    /* Initialize handler */
    Handler handler = new Handler();

    /* Start handler for 1 second */
    handler.postDelayed(() -> {
      /* Go to Login Page */
      startActivity(new Intent(this, LoginActivity.class));

      /* Stop this splash activity */
      finish();
    }, 1000);
  }


  /**
   * Customize the action bar for this activity here.
   */
  private void customizeActionBar() {
    ActionBar actionBar = getSupportActionBar();

    if (actionBar == null)
      throw new NullPointerException("There are no action bar in this activity.");

    /* Hide the action bar. */
    actionBar.hide();
  }
}