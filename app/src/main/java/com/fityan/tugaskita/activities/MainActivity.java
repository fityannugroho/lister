package com.fityan.tugaskita.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.adapters.TaskAdapter;
import com.fityan.tugaskita.collections.SharedTaskCollection;
import com.fityan.tugaskita.collections.TaskCollection;
import com.fityan.tugaskita.models.SharedTaskModel;
import com.fityan.tugaskita.models.TaskModel;
import com.fityan.tugaskita.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnItemListener {
  /**
   * Key to transfer task id value using intent.
   */
  public static final String TASK_ID_KEY = "taskId";

  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  /* Collections */
  private final TaskCollection taskCollection = new TaskCollection();
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();

  /**
   * List of task.
   */
  private final ArrayList<TaskModel> tasks = new ArrayList<>();

  /**
   * Task adapter.
   */
  private TaskAdapter taskAdapter;

  /**
   * View element to displaying task list.
   */
  private RecyclerView rvTask;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    /* Initialize view elements. */
    rvTask = findViewById(R.id.rvTask);
    TextView tvUserName = findViewById(R.id.tvUserName);


    /* Show greetings on appearance. */
    if (!Objects.equals(user.getDisplayName(), "")) {
      tvUserName.setText(user.getDisplayName());
    }

    /* Initialize the task adapter. */
    initTaskAdapter();
  }


  @Override
  protected void onStart() {
    super.onStart();

    // Retrieve tasks from database then displaying it.
    loadTasks();

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


  @Override
  public void onItemClick(int position) {
    /* Go to Detail Task Page with bring task id. */
    Intent intent = new Intent(this, DetailTaskActivity.class);
    intent.putExtra(TASK_ID_KEY, tasks.get(position).getId());
    startActivity(intent);
  }


  @Override
  public void onDeleteItem(int position) {
    DialogInterface.OnClickListener onDeleteBtnClickListener = (dialogInterface, i) -> {
      String taskId = tasks.get(position).getId();

      /* Delete task */
      taskCollection.delete(taskId).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          /* Get all related shared tasks. */
          deleteRelatedSharedTasks(taskId);
          /* Remove task from list. */
          tasks.remove(position);
          /* Refresh the task list view */
          taskAdapter.notifyItemRemoved(position);
          Toast.makeText(this, "One task has been deleted.", Toast.LENGTH_SHORT).show();
        }
      }).addOnFailureListener(e -> {
        Toast.makeText(this, "Failed to delete task.", Toast.LENGTH_SHORT).show();
        Log.e("deleteTask", e.getMessage(), e);
      });

      /* Close the dialog. */
      dialogInterface.dismiss();
    };

    /* Show confirmation dialog. */
    new AlertDialog.Builder(this).setTitle("Delete Task")
        .setMessage("Are you sure to delete this task?")
        .setPositiveButton("Delete Task", onDeleteBtnClickListener)
        .setNegativeButton("Cancel", null)
        .show();
  }


  @Override
  public void onEditItem(int position) {
    /* Go to Edit Task Page with bring task id. */
    Intent intent = new Intent(this, EditTaskActivity.class);
    intent.putExtra(TASK_ID_KEY, tasks.get(position).getId());
    startActivity(intent);
  }


  /**
   * Initialize task adapter & recycler view.
   */
  private void initTaskAdapter() {
    /* Initialize task adapter. */
    taskAdapter = new TaskAdapter(tasks, new UserModel(user), this);

    /* Set the adapter to displaying task list. */
    rvTask.setAdapter(taskAdapter);
    rvTask.setLayoutManager(new LinearLayoutManager(this));
    rvTask.setItemAnimator(new DefaultItemAnimator());
  }


  @SuppressLint("NotifyDataSetChanged")
  private void loadTasks() {
    /* If tasks is not empty. */
    if (!tasks.isEmpty()) {
      tasks.clear();
      taskAdapter.notifyDataSetChanged();
    }

    /* Retrieve own task data. */
    taskCollection.findAll(user.getUid()).addOnSuccessListener(queryDocumentSnapshots -> {
      for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
        TaskModel task = new TaskModel(document);

        /* Add the task to list, then refresh the adapter on data changed. */
        if (tasks.add(task))
          taskAdapter.notifyItemInserted(tasks.indexOf(task));
      }
    });

    /* Retrieve shared task data. */
    sharedTaskCollection.findByRecipient(user.getUid()).addOnSuccessListener(querySnapshot -> {
      for (DocumentSnapshot sharedTask : querySnapshot.getDocuments()) {
        String taskId = sharedTask.getString(SharedTaskModel.TASK_ID_FIELD);

        /* Get the task. */
        taskCollection.findOne(taskId).addOnSuccessListener(documentSnapshot -> {
          TaskModel task = new TaskModel(documentSnapshot);

          /* Add the task to list, then refresh the adapter on data changed. */
          if (tasks.add(task))
            taskAdapter.notifyItemInserted(tasks.indexOf(task));
        });
      }
    });
  }


  private void deleteRelatedSharedTasks(String taskId) {
    sharedTaskCollection.findByTask(taskId).addOnSuccessListener(querySnapshot -> {
      /* Delete all related shared task. */
      for (DocumentSnapshot document : querySnapshot.getDocuments()) {
        sharedTaskCollection.delete(document.getId())
            .addOnFailureListener(e -> Log.e("deleteSharedTask", e.getMessage(), e));
      }
    }).addOnFailureListener(e -> Log.e("deleteSharedTask", e.getMessage(), e));
  }
}
