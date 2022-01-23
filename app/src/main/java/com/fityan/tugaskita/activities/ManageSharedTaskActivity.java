package com.fityan.tugaskita.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.collections.SharedTaskCollection;
import com.fityan.tugaskita.collections.UserCollection;
import com.fityan.tugaskita.models.SharedTaskModel;
import com.fityan.tugaskita.models.UserModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ManageSharedTaskActivity extends AppCompatActivity {
  // Collections.
  SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();
  UserCollection userCollection = new UserCollection();

  // The shared task.
  private SharedTaskModel sharedTask;

  // The recipient.
  private UserModel recipient;

  // View elements.
  private Button btnRemoveAccess;
  private TextView tvEmail, tvName;
  private SwitchMaterial checkBoxWritable, checkBoxDeletable;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manage_shared_task);

    // Initialize view elements.
    btnRemoveAccess = findViewById(R.id.btnRemoveAccess);
    tvEmail = findViewById(R.id.tvEmail);
    tvName = findViewById(R.id.tvName);
    checkBoxWritable = findViewById(R.id.switchWritable);
    checkBoxDeletable = findViewById(R.id.switchDeletable);


    // Get the shared task id & displaying it.
    String sharedTaskId = getIntent().getStringExtra(SharedTaskListActivity.SHARED_TASK_ID_KEY);
    displayingData(sharedTaskId);


    // When Write Access Toggle is clicked, update the write access.
    checkBoxWritable.setOnClickListener(view -> {
      sharedTask.setWritable(checkBoxWritable.isChecked());
      sharedTaskCollection.update(sharedTask).addOnFailureListener(e -> {
        // If failed.
        Toast.makeText(this, "Failed to update write access.", Toast.LENGTH_SHORT).show();
        Log.e("updateWritable", e.getMessage(), e);
      });
    });


    // When Delete Access Toggle is clicked, update the delete access.
    checkBoxDeletable.setOnClickListener(view -> {
      sharedTask.setDeletable(checkBoxDeletable.isChecked());
      sharedTaskCollection.update(sharedTask).addOnFailureListener(e -> {
        // If failed.
        Toast.makeText(this, "Failed to update delete access.", Toast.LENGTH_SHORT).show();
        Log.e("updateDeletable", e.getMessage(), e);
      });
    });


    // When Remove Access Button is clicked
    btnRemoveAccess.setOnClickListener(view -> {
      // Set on confirmed action.
      DialogInterface.OnClickListener onRemoveBtnClickListener = (dialogInterface, i) -> {
        // Remove the access by deleting the shared task.
        sharedTaskCollection.delete(sharedTask.getId()).addOnSuccessListener(unused -> {
          // If success.
          Toast.makeText(this, "Access for " + recipient.getEmail() + " has been removed.",
              Toast.LENGTH_SHORT).show();
          finish();
        }).addOnFailureListener(e -> {
          // If failed.
          Toast.makeText(this, "Failed to remove the access for " + recipient.getEmail() + ".",
              Toast.LENGTH_SHORT).show();
          Log.e("deleteSharedTask", e.getMessage(), e);
        });
      };

      // Show confirm dialog.
      new AlertDialog.Builder(this).setTitle("Remove Access")
          .setMessage("Are you sure to remove the access for " + recipient.getEmail() + "?")
          .setPositiveButton("Remove Access", onRemoveBtnClickListener)
          .setNegativeButton("Cancel", null)
          .show();
    });
  }


  @Override
  public boolean navigateUpTo(Intent upIntent) {
    // Send the task id of this shared task.
    upIntent.putExtra(MainActivity.TASK_ID_KEY, sharedTask.getTaskId());
    return super.navigateUpTo(upIntent);
  }


  private void displayingData(String sharedTaskId) {
    // Get shared task data.
    sharedTaskCollection.findOne(sharedTaskId).addOnSuccessListener(sharedTaskDoc -> {
      sharedTask = new SharedTaskModel(sharedTaskDoc);

      // Display the access data.
      checkBoxWritable.setChecked(sharedTask.isWritable());
      checkBoxDeletable.setChecked(sharedTask.isDeletable());

      // Get recipient data.
      userCollection.findOne(sharedTask.getRecipientId()).addOnSuccessListener(userDoc -> {
        recipient = new UserModel(userDoc);

        // Display the recipient data.
        tvEmail.setText(recipient.getEmail());
        tvName.setText(recipient.getName());
      }).addOnFailureListener(e -> {
        Toast.makeText(this, "Failed to get data", Toast.LENGTH_SHORT).show();
        Log.e("getData", e.getMessage(), e);
      });
    }).addOnFailureListener(e -> {
      Toast.makeText(this, "Failed to get data", Toast.LENGTH_SHORT).show();
      Log.e("getData", e.getMessage(), e);
    });
  }
}
