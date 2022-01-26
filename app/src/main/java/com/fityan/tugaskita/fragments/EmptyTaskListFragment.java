package com.fityan.tugaskita.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.activities.AddTaskActivity;

public class EmptyTaskListFragment extends Fragment {
  public EmptyTaskListFragment() {
    // Required empty public constructor
    super(R.layout.fragment_empty_task_list);
  }


  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
  ) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_empty_task_list, container, false);
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    // Initialize view elements.
    Button btnAddTask = view.findViewById(R.id.btnAddTask);

    // When Add Button is clicked, go to Add Task Activity.
    btnAddTask.setOnClickListener(
        onClickView -> startActivity(new Intent(getActivity(), AddTaskActivity.class)));

    // Call super method.
    super.onViewCreated(view, savedInstanceState);
  }
}
