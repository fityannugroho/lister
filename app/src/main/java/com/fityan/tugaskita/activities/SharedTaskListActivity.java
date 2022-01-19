package com.fityan.tugaskita.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.adapters.SharedTaskAdapter;
import com.fityan.tugaskita.collections.SharedTaskCollection;
import com.fityan.tugaskita.models.SharedTaskModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class SharedTaskListActivity extends AppCompatActivity implements SharedTaskAdapter.OnItemListener {
  // Shared task list.
  private final ArrayList<SharedTaskModel> sharedTasks = new ArrayList<>();

  // Collections .
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();

  // Task id.
  private String taskId;

  // The adapter.
  private SharedTaskAdapter sharedTaskAdapter;

  // View elements.
  private RecyclerView rvSharedTask;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shared_task_list);

    // Initialize view elements.
    rvSharedTask = findViewById(R.id.rvSharedTask);

    // Get the task id.
    taskId = getIntent().getStringExtra(MainActivity.TASK_ID_KEY);

    // Initialize the shared task adapter.
    initSharedTaskAdapter();
  }


  @Override
  public boolean navigateUpTo(Intent upIntent) {
    upIntent.putExtra(MainActivity.TASK_ID_KEY, taskId);
    return super.navigateUpTo(upIntent);
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

    sharedTaskCollection.findByTask(taskId).addOnSuccessListener(querySnapshot -> {
      for (DocumentSnapshot document : querySnapshot.getDocuments()) {
        SharedTaskModel sharedTask = new SharedTaskModel(document);

        // Add the shared task to list, then refresh the adapter on data changed.
        if (sharedTasks.add(sharedTask))
          sharedTaskAdapter.notifyItemInserted(sharedTasks.indexOf(sharedTask));
      }
    }).addOnFailureListener(e -> Log.e("getSharedTask", e.getMessage(), e));
  }


  @Override
  public void onItemClick(int position) {
    // TODO: Go to Modify Shared Task Page.
  }
}