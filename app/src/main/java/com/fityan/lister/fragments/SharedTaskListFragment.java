package com.fityan.lister.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fityan.lister.R;
import com.fityan.lister.activities.ManageSharedTaskActivity;
import com.fityan.lister.adapters.SharedTaskAdapter;
import com.fityan.lister.collections.SharedTaskCollection;
import com.fityan.lister.models.SharedTaskModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class SharedTaskListFragment extends Fragment implements SharedTaskAdapter.OnItemListener {
  /**
   * Key to transfer value of shared task id using intent.
   */
  public static final String SHARED_TASK_ID_KEY = "sharedTaskId";

  /**
   * Key to get value of task id using intent.
   */
  private static final String TASK_ID_KEY = "taskId";

  // Shared task list.
  private final ArrayList<SharedTaskModel> sharedTasks = new ArrayList<>();

  // Collections .
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();

  // The task id.
  private String taskId;

  // The adapter.
  private SharedTaskAdapter sharedTaskAdapter;

  // View elements.
  private RecyclerView rvSharedTask;


  public SharedTaskListFragment() {
    // Required empty public constructor.
    super(R.layout.fragment_shared_task_list);
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    taskId = requireArguments().getString(TASK_ID_KEY);
  }


  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
  ) {
    // Inflate the layout for this fragment.
    return inflater.inflate(R.layout.fragment_shared_task_list, container, false);
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    // Initialize view elements.
    rvSharedTask = view.findViewById(R.id.rvSharedTask);
    // Initialize the shared task adapter.
    initSharedTaskAdapter();
    // Call super method.
    super.onViewCreated(view, savedInstanceState);
  }


  @Override
  public void onResume() {
    super.onResume();
  }


  @Override
  public void onStart() {
    // Load the shared tasks.
    loadSharedTasks();
    // Call super method.
    super.onStart();
  }


  private void initSharedTaskAdapter() {
    // Initialize the shared task adapter.
    sharedTaskAdapter = new SharedTaskAdapter(sharedTasks, this);

    // Set the adapter to recycle view.
    rvSharedTask.setAdapter(sharedTaskAdapter);
    rvSharedTask.setLayoutManager(new LinearLayoutManager(getContext()));
    rvSharedTask.setItemAnimator(new DefaultItemAnimator());
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
    // Go to Modify Shared Task Page.
    Intent intent = new Intent(getContext(), ManageSharedTaskActivity.class);
    intent.putExtra(SHARED_TASK_ID_KEY, sharedTasks.get(position).getId());
    startActivity(intent);
  }
}
