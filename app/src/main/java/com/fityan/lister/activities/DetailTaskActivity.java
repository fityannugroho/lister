package com.fityan.lister.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fityan.lister.R;
import com.fityan.lister.collections.SharedTaskCollection;
import com.fityan.lister.collections.TaskCollection;
import com.fityan.lister.helper.InputHelper;
import com.fityan.lister.models.SharedTaskModel;
import com.fityan.lister.models.TaskModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;

public class DetailTaskActivity extends AppCompatActivity {
  // Authentication.
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  // Collections.
  private final TaskCollection taskCollection = new TaskCollection();
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();

  // Task id.
  private String taskId;

  // The task.
  private TaskModel task;

  // View elements.
  private TextView tvTitle, tvDescription, tvDeadline, tvCountSharedTask;
  private Menu menu;
  private Button btnSeeSharedList;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_task);

    // Initialize view elements.
    btnSeeSharedList = findViewById(R.id.btnSeeSharedList);
    tvTitle = findViewById(R.id.tvTitle);
    tvDescription = findViewById(R.id.tvDescription);
    tvDeadline = findViewById(R.id.tvDeadline);
    tvCountSharedTask = findViewById(R.id.tvCountSharedTask);

    // Get task id.
    taskId = getIntent().getStringExtra("taskId");

    // When See All Button is clicked, go to Shared Task List Page.
    btnSeeSharedList.setOnClickListener(view -> {
      Intent intent = new Intent(this, SharedTaskListActivity.class);
      intent.putExtra("taskId", taskId);
      startActivity(intent);
    });
  }


  @Override
  protected void onStart() {
    loadTask();
    super.onStart();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    this.menu = menu;

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.detail_task_menu, menu);
    return true;
  }


  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    // If Share Item is selected.
    if (item.getItemId() == R.id.shareItem)
      onShareItemSelected();

    // If Edit Item is selected.
    if (item.getItemId() == R.id.editItem)
      onEditItemSelected();

    // If Delete Item is selected.
    if (item.getItemId() == R.id.deleteItem)
      onDeleteItemSelected();

    return super.onOptionsItemSelected(item);
  }


  private void onShareItemSelected() {
    // Go to Share Task Activity.
    Intent intent = new Intent(this, ShareTaskActivity.class);
    intent.putExtra("taskId", taskId);
    startActivity(intent);
  }


  private void onEditItemSelected() {
    // Go to Edit Task Activity.
    Intent intent = new Intent(this, EditTaskActivity.class);
    intent.putExtra("taskId", taskId);
    startActivity(intent);
  }


  private void onDeleteItemSelected() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

    alertDialog.setTitle("Delete Task")
        .setMessage("Are you sure to delete this task?")
        .setNegativeButton("Cancel", null)
        .setPositiveButton("Delete Task", (dialogInterface, i) -> {
          // Delete task.
          taskCollection.delete(taskId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
              deleteRelatedSharedTasks(taskId);    // Get all related shared tasks.
              finish();    // Finish this activity.
              Toast.makeText(this, "One task has been deleted.", Toast.LENGTH_SHORT).show();
            }
          }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to delete task.", Toast.LENGTH_SHORT).show();
            Log.e("deleteTask", e.getMessage(), e);
          });
        });

    // Show confirmation dialog.
    alertDialog.show();
  }


  private void loadTask() {
    taskCollection.findOne(taskId).addOnSuccessListener(documentSnapshot -> {
      // Set task.
      task = new TaskModel(documentSnapshot);

      // Set access.
      setTaskAccess();

      // Display the task data.
      tvTitle.setText(task.getTitle());
      tvDescription.setText(task.getDescription());
      tvDeadline.setText(InputHelper.dateToString(task.getDeadline().toDate(),
          InputHelper.DATE_FORMAT_HUMAN_LONG_US, Locale.US));

      // Display count shared task data.
      sharedTaskCollection.findByTask(task.getId()).addOnSuccessListener(querySnapshot -> {
        String label = getString(R.string.value_count_shared_task);
        tvCountSharedTask.setText(label.replace("null", String.valueOf(querySnapshot.size())));
      });
    }).addOnFailureListener(e -> {
      Log.e("loadTask", e.getMessage(), e);
      Toast.makeText(this, "Failed to get task. Try again later", Toast.LENGTH_SHORT).show();
      finish();    // Finish the activity.
    });
  }


  private void setTaskAccess() {
    MenuItem shareItem = menu.findItem(R.id.shareItem);
    MenuItem editItem = menu.findItem(R.id.editItem);
    MenuItem deleteItem = menu.findItem(R.id.deleteItem);

    if (task.getOwnerId().equals(user.getUid())) {
      // Set access for task owner.
      shareItem.setVisible(true);     // Enable sharing access.
      editItem.setVisible(true);      // Enable edit access.
      deleteItem.setVisible(true);    // Enable delete access.
      btnSeeSharedList.setVisibility(View.VISIBLE);    // Enable access to Shared Task List Page.
    } else {
      // Set modifier access for shared task.
      sharedTaskCollection.find(task.getId(), user.getUid()).addOnSuccessListener(querySnapshot -> {
        SharedTaskModel sharedTask = new SharedTaskModel(querySnapshot.getDocuments().get(0));
        if (sharedTask.isWritable())
          editItem.setVisible(true);    // Enable edit access.
        if (sharedTask.isDeletable())
          deleteItem.setVisible(true);    // Enable delete access.
      }).addOnFailureListener(e -> Log.e("taskAccess", e.getMessage(), e));
    }
  }


  private void deleteRelatedSharedTasks(String taskId) {
    sharedTaskCollection.findByTask(taskId).addOnSuccessListener(querySnapshot -> {
      // Delete all related shared task.
      for (DocumentSnapshot document : querySnapshot.getDocuments()) {
        sharedTaskCollection.delete(document.getId())
            .addOnFailureListener(e -> Log.e("deleteSharedTask", e.getMessage(), e));
      }
    }).addOnFailureListener(e -> Log.e("deleteSharedTask", e.getMessage(), e));
  }
}
