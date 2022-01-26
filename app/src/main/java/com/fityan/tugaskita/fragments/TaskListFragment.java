package com.fityan.tugaskita.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.activities.DetailTaskActivity;
import com.fityan.tugaskita.activities.EditTaskActivity;
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

public class TaskListFragment extends Fragment implements TaskAdapter.OnItemListener {
  // Collections.
  private final TaskCollection taskCollection = new TaskCollection();
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();

  // List of task.
  private final ArrayList<TaskModel> tasks = new ArrayList<>();

  private FirebaseUser user;

  // Task adapter.
  private TaskAdapter taskAdapter;

  // View elements.
  private RecyclerView rvTask;


  public TaskListFragment() {
    // Required empty public constructor
    super(R.layout.fragment_task_list);
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Authentication.
    user = FirebaseAuth.getInstance().getCurrentUser();
  }


  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
  ) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_task_list, container, false);
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    // Initialize view elements.
    rvTask = view.findViewById(R.id.rvTask);

    initTaskAdapter();

    // Call super method.
    super.onViewCreated(view, savedInstanceState);
  }


  @Override
  public void onStart() {
    // Load the tasks.
    loadTasks();

    // Call super method.
    super.onStart();
  }


  /**
   * Initialize task adapter & recycler view.
   */
  private void initTaskAdapter() {
    // Initialize task adapter.
    taskAdapter = new TaskAdapter(tasks, new UserModel(user), this);

    // Set the adapter to displaying task list.
    rvTask.setAdapter(taskAdapter);
    rvTask.setLayoutManager(new LinearLayoutManager(getContext()));
    rvTask.setItemAnimator(new DefaultItemAnimator());
  }


  @Override
  public void onItemClick(int position) {
    // Go to Detail Task Page with bring task id.
    Intent intent = new Intent(getContext(), DetailTaskActivity.class);
    intent.putExtra("taskId", tasks.get(position).getId());
    startActivity(intent);
  }


  @Override
  public void onDeleteItem(int position) {
    DialogInterface.OnClickListener onDeleteBtnClickListener = (dialogInterface, i) -> {
      String taskId = tasks.get(position).getId();

      // Delete task.
      taskCollection.delete(taskId).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          // Get all related shared tasks.
          deleteRelatedSharedTasks(taskId);

          // Remove task from list.
          tasks.remove(position);

          // Refresh the task list view.
          taskAdapter.notifyItemRemoved(position);
          Toast.makeText(getContext(), "One task has been deleted.", Toast.LENGTH_SHORT).show();
        }
      }).addOnFailureListener(e -> {
        Toast.makeText(getContext(), "Failed to delete task.", Toast.LENGTH_SHORT).show();
        Log.e("deleteTask", e.getMessage(), e);
      });

      // Close the dialog.
      dialogInterface.dismiss();
    };

    // Show confirmation dialog.
    new AlertDialog.Builder(requireContext()).setTitle("Delete Task")
        .setMessage("Are you sure to delete this task?")
        .setPositiveButton("Delete Task", onDeleteBtnClickListener)
        .setNegativeButton("Cancel", null)
        .show();
  }


  @Override
  public void onEditItem(int position) {
    // Go to Edit Task Page with bring task id.
    Intent intent = new Intent(getContext(), EditTaskActivity.class);
    intent.putExtra("taskId", tasks.get(position).getId());
    startActivity(intent);
  }


  @SuppressLint("NotifyDataSetChanged")
  private void loadTasks() {
    // If tasks is not empty.
    if (!tasks.isEmpty()) {
      tasks.clear();
      taskAdapter.notifyDataSetChanged();
    }

    // Retrieve own task data.
    taskCollection.findAll(user.getUid()).addOnSuccessListener(queryDocumentSnapshots -> {
      for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
        TaskModel task = new TaskModel(document);

        // Add the task to list, then refresh the adapter on data changed.
        if (tasks.add(task))
          taskAdapter.notifyItemInserted(tasks.indexOf(task));
      }
    });

    // Retrieve shared task data.
    sharedTaskCollection.findByRecipient(user.getUid()).addOnSuccessListener(querySnapshot -> {
      for (DocumentSnapshot sharedTask : querySnapshot.getDocuments()) {
        String taskId = sharedTask.getString(SharedTaskModel.TASK_ID_FIELD);

        // Get the task.
        taskCollection.findOne(taskId).addOnSuccessListener(documentSnapshot -> {
          TaskModel task = new TaskModel(documentSnapshot);

          // Add the task to list, then refresh the adapter on data changed.
          if (tasks.add(task))
            taskAdapter.notifyItemInserted(tasks.indexOf(task));
        });
      }
    });
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
