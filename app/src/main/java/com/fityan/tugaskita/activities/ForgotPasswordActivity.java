package com.fityan.tugaskita.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.helper.InputHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  /* View Elements. */
  private Button btnSend;
  private EditText inputEmail;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);

    /* Initialize view elements. */
    btnSend = findViewById(R.id.btnSend);
    inputEmail = findViewById(R.id.inputEmail);


    /* Customize action bar. */
    try {
      customizeActionBar();
    } catch (NullPointerException e) {
      Log.i("actionBar", e.getMessage(), e);
    }

    /* When Send Request Button is clicked. */
    btnSend.setOnClickListener(view -> {
      try {
        /* Get email */
        String email = InputHelper.getRequiredInput(inputEmail);

        /* Temporarily disable the button. */
        btnSend.setEnabled(false);
        btnSend.setText(R.string.label_button_sending);

        /* Send password reset email. */
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
          try {
            if (!task.isSuccessful())
              throw Objects.requireNonNull(task.getException());

            /* If success. */
            Toast.makeText(this, "Password reset email has been sent.", Toast.LENGTH_SHORT).show();
            finish();
          } catch (FirebaseAuthInvalidUserException e) {
            Toast.makeText(this, "This email is unregistered.", Toast.LENGTH_SHORT).show();
          } catch (FirebaseAuthInvalidCredentialsException e) {
            inputEmail.setError("Invalid email format.");
          } catch (Exception e) {
            Toast.makeText(this, "There are unexpected error.", Toast.LENGTH_SHORT).show();
            Log.e("sendEmail", e.getMessage(), e);
          }

          /* Reactivate the button. */
          btnSend.setEnabled(true);
          btnSend.setText(R.string.label_button_send);
        });
      } catch (NullPointerException e) {
        Log.e("inputValidation", e.getMessage(), e);
      }
    });
  }


  /**
   * Customize the action bar for this activity here.
   */
  private void customizeActionBar() {
    ActionBar actionBar = getSupportActionBar();

    if (actionBar == null)
      throw new NullPointerException("There are no action bar in this activity.");

    /* Set the title of action bar. */
    actionBar.setTitle("Forgot Password");
  }
}
