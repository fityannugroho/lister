package com.fityan.tugaskita.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.collections.UserCollection;
import com.fityan.tugaskita.helper.InputHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UpdateProfileActivity extends AppCompatActivity {
  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  /* Collections */ UserCollection userCollection = new UserCollection();

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

        UserProfileChangeRequest profileUpdates
            = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

        /* Update the user profiles. */
        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            /* Save to user collection. */
            userCollection.save(user.getUid(), user.getEmail(), user.getDisplayName())
                .addOnCompleteListener(task1 -> {
                  if (task1.isSuccessful()) {
                    finish();    // Finish this activity.
                  } else {
                    Toast.makeText(this, "Failed adding profile to collection.", Toast.LENGTH_SHORT)
                        .show();
                    Log.i("updateProfile", "Failed adding profile to collection.",
                        task.getException());
                  }
                });
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
