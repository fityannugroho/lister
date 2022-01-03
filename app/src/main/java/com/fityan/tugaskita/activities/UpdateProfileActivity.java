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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UpdateProfileActivity extends AppCompatActivity {
  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  /* View elements. */
  private Button btnNext;
  private EditText inputName;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_update_profile);

    /* Initialize view elements. */
    btnNext = findViewById(R.id.btnNext);
    inputName = findViewById(R.id.inputName);

    /* Customize action bar. */
    try {
      customizeActionBar();
    } catch (NullPointerException e) {
      Log.i("actionBar", e.getMessage(), e);
    }


    /* When Next Button is clicked. */
    btnNext.setOnClickListener(view -> {
      try {
        String name = InputHelper.getRequiredInput(inputName);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
            .setDisplayName(name).build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            /* Go to Login Page. */
            startActivity(new Intent(this, LoginActivity.class));
            finish();
          } else {
            Toast.makeText(this, "Failed updating profile.", Toast.LENGTH_SHORT).show();
            Log.i("updateProfile", "Failed updating profile.", task.getException());
          }
        });
      } catch (NullPointerException e) {
        Log.i("inputName", e.getMessage(), e);
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

    /* Set title. */
    actionBar.setTitle("Update Profile");
  }
}
