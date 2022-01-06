package com.fityan.tugaskita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {
  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  /* View elements. */
  private Button btnVerify;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verify_email);

    /* Initialize view elements. */
    btnVerify = findViewById(R.id.btnVerify);


    /* When Email Verify Button is clicked. */
    btnVerify.setOnClickListener(view -> {
      /* Reload user status. */
      user.reload().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          try {
            /* Check if email is verified. */
            if (!user.isEmailVerified())
              throw new FirebaseAuthEmailException("404", "Email is not verified");

            /* If email is verified, go to Main Page. */
            startActivity(new Intent(this, MainActivity.class));
            finish();
          } catch (FirebaseAuthEmailException e) {
            Toast.makeText(this, "Please verify your email first then try again.",
                Toast.LENGTH_SHORT).show();
          }
        }
      });
    });
  }
}