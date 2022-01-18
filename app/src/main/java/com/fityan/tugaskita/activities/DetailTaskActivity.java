package com.fityan.tugaskita.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.adapters.SharedTaskAdapter;
import com.fityan.tugaskita.collections.SharedTaskCollection;
import com.fityan.tugaskita.collections.TaskCollection;
import com.fityan.tugaskita.collections.UserCollection;
import com.fityan.tugaskita.components.ShareTaskDialog;
import com.fityan.tugaskita.helper.InputHelper;
import com.fityan.tugaskita.models.SharedTaskModel;
import com.fityan.tugaskita.models.TaskModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class DetailTaskActivity extends AppCompatActivity implements ShareTaskDialog.OnClickShareTaskDialogListener, SharedTaskAdapter.OnItemListener {
  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  /* Collections */
  private final TaskCollection taskCollection = new TaskCollection();
  private final UserCollection userCollection = new UserCollection();
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();

  /* Task */
  private final TaskModel task = new TaskModel();

  // Shared task list.
  private final ArrayList<SharedTaskModel> sharedTasks = new ArrayList<>();

  // View elements.
  private TextView tvTitle, tvDescription, tvDeadline;
  private Menu menu;
  private RecyclerView rvSharedTask;

  // The adapter.
  private SharedTaskAdapter sharedTaskAdapter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_task);

    /* Initialize view elements. */
    tvTitle = findViewById(R.id.tvTitle);
    tvDescription = findViewById(R.id.tvDescription);
    tvDeadline = findViewById(R.id.tvDeadline);
    rvSharedTask = findViewById(R.id.rvSharedTask);

    // Get task id.
    String taskId = getIntent().getStringExtra("taskId");

    // Get the task.
    taskCollection.findOne(taskId).addOnSuccessListener(documentSnapshot -> {
      /* Set task. */
      task.setId(documentSnapshot.getId());
      task.setTitle(documentSnapshot.getString(TaskModel.TITLE_FIELD));
      task.setDescription(documentSnapshot.getString(TaskModel.DESCRIPTION_FIELD));
      task.setDeadline(documentSnapshot.getTimestamp(TaskModel.DEADLINE_FIELD));
      task.setOwnerId(documentSnapshot.getString(TaskModel.OWNER_ID_FIELD));

      String strDeadline = InputHelper.dateToString(
          Objects.requireNonNull(task.getDeadline()).toDate(),
          InputHelper.DATE_FORMAT_HUMAN_LONG_US, Locale.US);

      /* Display the task data. */
      tvTitle.setText(documentSnapshot.getString(TaskModel.TITLE_FIELD));
      tvDescription.setText(documentSnapshot.getString(TaskModel.DESCRIPTION_FIELD));
      tvDeadline.setText(strDeadline);

      /* Set sharing access. */
      if (!task.getOwnerId().equals(user.getUid())) {
        MenuItem item = menu.findItem(R.id.shareItem);
        item.setVisible(false);
      }

      // Initialize the shared task adapter.
      initSharedTaskAdapter();
    });
  }


  @Override
  protected void onRestart() {
    super.onRestart();
    loadSharedTasks();
  }


  private void initSharedTaskAdapter() {
    // Initialize the shared task adapter.
    sharedTaskAdapter = new SharedTaskAdapter(sharedTasks, this);

    // Set the adapter to recycle view.
    rvSharedTask.setAdapter(sharedTaskAdapter);
    rvSharedTask.setLayoutManager(new LinearLayoutManager(this));
    rvSharedTask.setItemAnimator(new DefaultItemAnimator());

    // Retrieve shared tasks from database then displaying it.
    loadSharedTasks();
  }


  @SuppressLint("NotifyDataSetChanged")
  private void loadSharedTasks() {
    // If tasks is not empty.
    if (!sharedTasks.isEmpty()) {
      sharedTasks.clear();
      sharedTaskAdapter.notifyDataSetChanged();
    }

    sharedTaskCollection.findByTask(task.getId()).addOnSuccessListener(querySnapshot -> {
      for (DocumentSnapshot document : querySnapshot.getDocuments()) {
        SharedTaskModel sharedTask = new SharedTaskModel(document);

        // Add the shared task to list, then refresh the adapter on data changed.
        if (sharedTasks.add(sharedTask))
          sharedTaskAdapter.notifyItemInserted(sharedTasks.indexOf(sharedTask));
      }
    }).addOnFailureListener(e -> Log.e("getSharedTask", e.getMessage(), e));
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
    /* If Share Item is selected. */
    if (item.getItemId() == R.id.shareItem) {
      /* Show share task dialog. */
      ShareTaskDialog shareTaskDialog = new ShareTaskDialog();
      shareTaskDialog.show(getSupportFragmentManager(), "shareTaskDialog");
    }
    return super.onOptionsItemSelected(item);
  }


  @Override
  public void onClickShareButton(String email, boolean isWritable, boolean isDeletable) {
    /* Check if email is exists. */
    userCollection.findByEmail(email).addOnSuccessListener(querySnapshot -> {
      try {
        /* If email doesn't exists. */
        if (querySnapshot.isEmpty())
          throw new Exception("Email " + email + " doesn't exists.");

        /* If email doesn't exists. */
        if (email.equals(user.getEmail()))
          throw new Exception("Can't share the task to yourself.");

        /* If more than one email found. */
        if (querySnapshot.size() > 1)
          throw new Exception("Multiple user with " + email
              + " email is found. Please contact the admin to fix it.");

        /* If this task has been shared with this user. */
        sharedTaskCollection.findByTask(task.getId()).addOnSuccessListener(querySnapshot1 -> {
          try {
            String recipientId = querySnapshot.getDocuments().get(0).getId();

            for (DocumentSnapshot document : querySnapshot1) {
              if (Objects.equals(document.getString(SharedTaskModel.RECIPIENT_ID_FIELD),
                  recipientId)) {
                throw new Exception("This task has been shared with this user.");
              }
            }

            /* If all requirement valid, create new shared task. */
            sharedTaskCollection.insert(task.getId(), recipientId, isWritable, isDeletable)
                .addOnSuccessListener(documentReference -> {
                  /* If sharing is successful. */
                  Toast.makeText(this, "Task shared successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                  Log.e("sharingTask", "Failed to sharing the task.", e);
                  Toast.makeText(this, "Failed to sharing the task.", Toast.LENGTH_SHORT).show();
                });
          } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
      } catch (Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }


  @Override
  public void onItemClick(int position) {
    // TODO: Go to Modify Shared Task Page.
  }
}
