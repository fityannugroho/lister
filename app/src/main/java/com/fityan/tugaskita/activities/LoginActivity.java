package com.fityan.tugaskita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.helper.InputHelper;
import com.fityan.tugaskita.validation.EmailValidation;
import com.fityan.tugaskita.validation.EmailValidationException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  /* View elements. */
  private Button btnForgotPassword, btnLogin, btnRegister;
  private EditText inputEmail, inputPassword;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    /* Initialize view elements. */
    btnForgotPassword = findViewById(R.id.btnForgotPassword);
    btnLogin = findViewById(R.id.btnLogin);
    btnRegister = findViewById(R.id.btnRegister);
    inputEmail = findViewById(R.id.inputEmail);
    inputPassword = findViewById(R.id.inputPassword);


    /* Customize action bar. */
    try {
      customizeActionBar();
    } catch (NullPointerException e) {
      Log.i("actionBar", e.getMessage(), e);
    }


    /* When Forgot Password Button is clicked. */
    btnForgotPassword.setOnClickListener(view -> {
      /* TODO: go to Forgot Password Page. */
      startActivity(new Intent(this, ForgotPasswordActivity.class));
    });


    /* When Login Button is clicked. */
    btnLogin.setOnClickListener(view -> {
      try {
        String email = InputHelper.getRequiredInput(inputEmail);
        String password = InputHelper.getRequiredInput(inputPassword);

        /* Validate email */
        if (!EmailValidation.isEmailValid(email))
          throw new EmailValidationException("Invalid email format.");

        /* Login */
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
          /* If success, go to Main Page. */
          try {
            authorizedUser(authResult.getUser());
          } catch (FirebaseAuthEmailException e) {
            Toast
                .makeText(this, "Please verify your email first then try again.", Toast.LENGTH_LONG)
                .show();
          } catch (FirebaseAuthInvalidUserException e) {
            Log.w("login", "User not found", e);
          }
        }).addOnFailureListener(e -> {
          /* If failed. */
          try {
            throw e;
          } catch (FirebaseAuthInvalidUserException | FirebaseAuthInvalidCredentialsException authException) {
            /* Display message if email or password is invalid. */
            Toast
                .makeText(this, "Login failed! Email or password may be wrong.", Toast.LENGTH_SHORT)
                .show();
            Log.e("login", authException.getMessage(), authException);
          } catch (Exception exception) {
            Log.e("login", exception.getMessage(), exception);
          }
        });
      } catch (EmailValidationException e) {
        /* If email is invalid. */
        Log.i("inputValidation", e.getMessage(), e);
        inputEmail.setError(e.getMessage());
      } catch (NullPointerException e) {
        /* If some input is empty. */
        Log.i("inputValidation", e.getMessage(), e);
      }
    });


    /* When Register Button is clicked. */
    btnRegister.setOnClickListener(view -> {
      /* Go to Register Page. */
      startActivity(new Intent(this, RegisterActivity.class));
      finish();
    });
  }


  @Override
  protected void onStart() {
    super.onStart();

    /* Check if user is authorized. */
    try {
      authorizedUser(user);
    } catch (FirebaseAuthEmailException e) {
      Toast.makeText(this, "Please verify your email first then try again.", Toast.LENGTH_LONG)
          .show();
    } catch (FirebaseAuthInvalidUserException e) {
      Log.w("login", "User not found", e);
    }
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


  /**
   * Authorize user before go to Main Page.
   *
   * @param user The logged in user.
   * @throws FirebaseAuthInvalidUserException If there are no logged in user.
   * @throws FirebaseAuthEmailException       if the logged in user hasn't verified its email.
   */
  private void authorizedUser(
      FirebaseUser user
  ) throws FirebaseAuthInvalidUserException, FirebaseAuthEmailException {
    if (user == null)
      throw new FirebaseAuthInvalidUserException("404", "User not found.");
    if (!user.isEmailVerified()) {
      auth.signOut();
      throw new FirebaseAuthEmailException("404", "Email is not verified.");
    }

    /* If user authorized, go to Main Page. */
    goToMainActivity();
  }


  /**
   * Go to main activity.
   */
  private void goToMainActivity() {
    startActivity(new Intent(LoginActivity.this, MainActivity.class));
    finish();    // Stop current activity.
  }
}
