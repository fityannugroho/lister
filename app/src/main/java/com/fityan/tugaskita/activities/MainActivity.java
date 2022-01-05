package com.fityan.tugaskita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.adapters.TaskAdapter;
import com.fityan.tugaskita.collections.TaskCollection;
import com.fityan.tugaskita.models.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnItemListener {
  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  /* Collections */
  private final TaskCollection taskCollection = new TaskCollection();

  /**
   * List of task.
   */
  private final ArrayList<TaskModel> tasks = new ArrayList<>();

  /**
   * View element to displaying task list.
   */
  private RecyclerView rvTask;

  /**
   * View element to navigate to add task page.
   */
  private FloatingActionButton btnAddTask;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    /* Initialize view elements. */
    rvTask = findViewById(R.id.rvTask);
    btnAddTask = findViewById(R.id.btnAdd);

    /* Retrieve tasks from database then displaying it. */
    loadTasks();

    /* When Add Button is clicked, */
    btnAddTask.setOnClickListener(view -> {
      /* go to Add Task Page. */
      startActivity(new Intent(this, AddTaskActivity.class));
    });
  }


  @Override
  protected void onStart() {
    super.onStart();

    if (Objects.requireNonNull(user.getDisplayName()).isEmpty()) {
      /* Go to Update Profile Page if display name is unset, */
      startActivity(new Intent(this, UpdateProfileActivity.class));
      finish();
    }
  }


  @Override
  protected void onRestart() {
    super.onRestart();

    tasks.clear();
    loadTasks();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }


  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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


  @Override
  public void onItemClick(int position) {
  }


  @Override
  public void onDeleteItem(int position) {
  }


  @Override
  public void onEditItem(int position) {
  }


  private void loadTasks() {
    /* Retrieve task data from database. */
    taskCollection.findAll(user.getUid(), TaskModel.DEADLINE_FIELD, true).addOnSuccessListener(queryDocumentSnapshots -> {
      for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
        tasks.add(new TaskModel(document.getId(), document.getString(TaskModel.TITLE_FIELD),
            document.getString(TaskModel.DESCRIPTION_FIELD),
            document.getTimestamp(TaskModel.DEADLINE_FIELD),
            document.getString(TaskModel.OWNER_ID_FIELD)));
      }

      /* Set the adapter to displaying task list. */
      rvTask.setAdapter(new TaskAdapter(tasks, this));
      rvTask.setLayoutManager(new LinearLayoutManager(this));
      rvTask.setItemAnimator(new DefaultItemAnimator());
    });
  }
}
