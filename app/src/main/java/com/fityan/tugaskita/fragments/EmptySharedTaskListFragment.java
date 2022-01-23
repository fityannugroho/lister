package com.fityan.tugaskita.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.activities.MainActivity;
import com.fityan.tugaskita.activities.ShareTaskActivity;

public class EmptySharedTaskListFragment extends Fragment {
  // Task id.
  private String taskId;

  // View elements.
  private Button btnShare;


  public EmptySharedTaskListFragment() {
    // Required empty public constructor
  }


  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    taskId = requireArguments().getString(MainActivity.TASK_ID_KEY);
  }


  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
  ) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_empty_shared_task_list, container, false);
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    // Initialize view elements.
    btnShare = view.findViewById(R.id.btnShare);

    // When Share Button is clicked.
    btnShare.setOnClickListener(viewOnClick -> {
      // Go to Share Task Page.
      Intent intent = new Intent(getActivity(), ShareTaskActivity.class);
      intent.putExtra(MainActivity.TASK_ID_KEY, taskId);
      startActivity(intent);
    });

    // Call super method.
    super.onViewCreated(view, savedInstanceState);
  }
}
