package com.fityan.lister.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.fityan.lister.R;
import com.fityan.lister.collections.SharedTaskCollection;
import com.fityan.lister.collections.TaskCollection;
import com.fityan.lister.fragments.EmptyTaskListFragment;
import com.fityan.lister.fragments.TaskListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
  /**
   * Key to transfer task id value using intent.
   */
  public static final String TASK_ID_KEY = "taskId";

  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  // Collections.
  private final TaskCollection taskCollection = new TaskCollection();
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();

  // The fragment manager.
  private FragmentManager fragmentManager;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState == null) {
      // Set fragment for first time.
      fragmentManager = getSupportFragmentManager();
    }
  }


  @Override
  protected void onStart() {
    super.onStart();

    // Set the fragment.
    setFragment();

    /* Go to Update Profile Page if display name is unset, */
    if (Objects.equals(user.getDisplayName(), "")) {
      startActivity(new Intent(this, UpdateProfileActivity.class));
      finish();
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }


  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    /* If Add Task Item is selected. */
    if (item.getItemId() == R.id.addTaskItem) {
      /* go to Add Task Page. */
      startActivity(new Intent(this, AddTaskActivity.class));
    }

    /* If Logout Item is selected. */
    if (item.getItemId() == R.id.logoutItem) {
      /* Show confirmation dialog. */
      new AlertDialog.Builder(this).setTitle("Logout")
          .setMessage("Are you sure to logout?")
          /* Cancel action. */
          .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
          /* Sign out then redirect to login activity. */
          .setPositiveButton("Logout", (dialogInterface, i) -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
          })
          .show();
    }
    return super.onOptionsItemSelected(item);
  }


  /**
   * Set fragment to the fragment container view. Will set {@code EmptyTaskListFragment} if there
   * are no task. Otherwise, {@code TaskListFragment} will be set.
   */
  private void setFragment() {
    taskCollection.findAll(user.getUid())
        .addOnSuccessListener(tasksSnapshot -> sharedTaskCollection.findByRecipient(user.getUid())
            .addOnSuccessListener(sharedTasksSnapshot -> {
              if (tasksSnapshot.isEmpty() && sharedTasksSnapshot.isEmpty()) {
                // Load empty task list fragment.
                fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, EmptyTaskListFragment.class, null)
                    .commit();
              } else {
                // Load task list fragment.
                fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, TaskListFragment.class, null)
                    .commit();
              }
            }));
  }
}
