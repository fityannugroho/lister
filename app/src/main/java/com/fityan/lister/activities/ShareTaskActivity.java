package com.fityan.lister.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fityan.lister.R;
import com.fityan.lister.collections.SharedTaskCollection;
import com.fityan.lister.collections.UserCollection;
import com.fityan.lister.helper.InputHelper;
import com.fityan.lister.models.SharedTaskModel;
import com.fityan.lister.models.UserModel;
import com.fityan.lister.validation.EmailValidation;
import com.fityan.lister.validation.EmailValidationException;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShareTaskActivity extends AppCompatActivity {
  // Authentication.
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  // Collections.
  private final UserCollection userCollection = new UserCollection();
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();

  // The task id.
  private String taskId;

  // View elements.
  private Button btnShare;
  private EditText inputEmail;
  private SwitchMaterial switchWritable, switchDeletable;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share_task);

    // Initialize view elements.
    btnShare = findViewById(R.id.btnShare);
    inputEmail = findViewById(R.id.inputEmail);
    switchWritable = findViewById(R.id.switchWritable);
    switchDeletable = findViewById(R.id.switchDeletable);

    // Get task id.
    taskId = getIntent().getStringExtra(MainActivity.TASK_ID_KEY);

    // When Share Button is clicked.
    btnShare.setOnClickListener(view -> {
      try {
        // Get value
        String email = InputHelper.getRequiredInput(inputEmail);
        boolean isWritable = switchWritable.isChecked();
        boolean isDeletable = switchDeletable.isChecked();

        // Validate email.
        if (!EmailValidation.isEmailValid(email))
          throw new EmailValidationException("Invalid email format");

        // Share the task.
        shareTask(email, isWritable, isDeletable);
      } catch (NullPointerException | EmailValidationException e) {
        inputEmail.setError(e.getMessage());
      }
    });
  }


  @Override
  public boolean navigateUpTo(Intent upIntent) {
    upIntent.putExtra(MainActivity.TASK_ID_KEY, taskId);
    return super.navigateUpTo(upIntent);
  }


  private void shareTask(String email, boolean isWritable, boolean isDeletable) {
    userCollection.findByEmail(email).addOnSuccessListener(querySnapshot -> {
      try {
        // If email doesn't exists.
        if (querySnapshot.isEmpty())
          throw new Exception("Email " + email + " doesn't exists.");
        // If email is same with logged user's email.
        if (email.equals(user.getEmail()))
          throw new Exception("Can't share the task to yourself.");
        // If more than one email found.
        if (querySnapshot.size() > 1)
          throw new Exception("Anomaly detected: This email is used to multiple user.");

        // The recipient.
        UserModel recipient = new UserModel(querySnapshot.getDocuments().get(0));

        // If this task has been shared with this user.
        sharedTaskCollection.find(taskId, recipient.getId())
            .addOnSuccessListener(querySharedTask -> {
              try {
                if (!querySharedTask.isEmpty())
                  throw new Exception("This task has been shared with this user.");

                // If all requirement valid, create new shared task.
                SharedTaskModel sharedTask = new SharedTaskModel(null, taskId, recipient.getId(),
                    isWritable, isDeletable);

                sharedTaskCollection.insert(sharedTask).addOnSuccessListener(document -> {
                  // If sharing is successful, finish the activity.
                  Toast.makeText(this, "Task shared successfully", Toast.LENGTH_SHORT).show();
                  finish();
                }).addOnFailureListener(e -> {
                  Toast.makeText(this, "Failed to sharing the task.", Toast.LENGTH_SHORT).show();
                  Log.e("sharingTask", e.getMessage(), e);
                });
              } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
              }
            });
      } catch (Exception e) {
        inputEmail.setError(e.getMessage());
      }
    });
  }
}
