package com.fityan.lister.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.fityan.lister.R;
import com.fityan.lister.collections.SharedTaskCollection;
import com.fityan.lister.fragments.EmptySharedTaskListFragment;
import com.fityan.lister.fragments.SharedTaskListFragment;

public class SharedTaskListActivity extends AppCompatActivity {
  /**
   * Key to transfer value of shared task id using intent.
   */
  public static final String SHARED_TASK_ID_KEY = "sharedTaskId";

  // Collections .
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();

  // The fragment manager.
  private FragmentManager fragmentManager;

  // Task id.
  private String taskId;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shared_task_list);

    // Get the task id.
    taskId = getIntent().getStringExtra(MainActivity.TASK_ID_KEY);

    // Set fragment for first time.
    if (savedInstanceState == null) {
      fragmentManager = getSupportFragmentManager();
      setFragmentContainerView();
    }
  }


  @Override
  protected void onResume() {
    // Update the fragment when activity is resumed.
    setFragmentContainerView();
    // Call super method.
    super.onResume();
  }


  @Override
  public boolean navigateUpTo(Intent upIntent) {
    upIntent.putExtra(MainActivity.TASK_ID_KEY, taskId);
    return super.navigateUpTo(upIntent);
  }


  private void setFragmentContainerView() {
    Bundle bundle = new Bundle();
    bundle.putString(MainActivity.TASK_ID_KEY, taskId);

    sharedTaskCollection.findByTask(taskId).addOnSuccessListener(querySnapshot -> {
      if (querySnapshot.isEmpty()) {
        // Load empty shared list fragment.
        fragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container_view, EmptySharedTaskListFragment.class, bundle)
            .commit();
      } else {
        // Load shared list fragment.
        fragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container_view, SharedTaskListFragment.class, bundle)
            .commit();
      }
    });
  }
}
