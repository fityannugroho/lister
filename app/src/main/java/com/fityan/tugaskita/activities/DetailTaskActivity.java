package com.fityan.tugaskita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.collections.SharedTaskCollection;
import com.fityan.tugaskita.collections.TaskCollection;
import com.fityan.tugaskita.helper.InputHelper;
import com.fityan.tugaskita.models.TaskModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Objects;

public class DetailTaskActivity extends AppCompatActivity {
  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  /* Collections */
  private final TaskCollection taskCollection = new TaskCollection();
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();

  /* Task */
  private final TaskModel task = new TaskModel();

  // View elements.
  private TextView tvTitle, tvDescription, tvDeadline, tvCountSharedTask;
  private Menu menu;
  private Button btnSeeSharedList;

  // Task id.
  private String taskId;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_task);

    /* Initialize view elements. */
    btnSeeSharedList = findViewById(R.id.btnSeeSharedList);
    tvTitle = findViewById(R.id.tvTitle);
    tvDescription = findViewById(R.id.tvDescription);
    tvDeadline = findViewById(R.id.tvDeadline);
    tvCountSharedTask = findViewById(R.id.tvCountSharedTask);

    // Get task id.
    taskId = getIntent().getStringExtra(MainActivity.TASK_ID_KEY);

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

      // Set access for task owner.
      if (task.getOwnerId().equals(user.getUid())) {
        // Enable sharing access.
        MenuItem item = menu.findItem(R.id.shareItem);
        item.setVisible(true);

        // Enable access to Shared Task List Page.
        btnSeeSharedList.setVisibility(View.VISIBLE);
      }

      // When See All Button is clicked, go to Shared Task List Page.
      btnSeeSharedList.setOnClickListener(view -> {
        Intent intent = new Intent(this, SharedTaskListActivity.class);
        intent.putExtra(MainActivity.TASK_ID_KEY, taskId);
        startActivity(intent);
      });

      // Display count shared task data.
      sharedTaskCollection.findByTask(task.getId()).addOnSuccessListener(querySnapshot -> {
        String label = getString(R.string.value_count_shared_task);
        tvCountSharedTask.setText(label.replace("null", String.valueOf(querySnapshot.size())));
      });
    });
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
      // Go to Share Task Activity.
      Intent intent = new Intent(this, ShareTaskActivity.class);
      intent.putExtra(MainActivity.TASK_ID_KEY, taskId);
      startActivity(intent);
    }

    return super.onOptionsItemSelected(item);
  }
}
