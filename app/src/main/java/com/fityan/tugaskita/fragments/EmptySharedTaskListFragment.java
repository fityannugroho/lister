package com.fityan.tugaskita.fragments;

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

public class EmptySharedTaskListFragment extends Fragment {
  // View elements.
  private Button btnShared;


  public EmptySharedTaskListFragment() {
    // Required empty public constructor
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
    btnShared = view.findViewById(R.id.btnShared);

    // When Share Button is clicked.
    btnShared.setOnClickListener(viewOnClick -> {
      // TODO: Go to Share Task Page.
      Toast.makeText(getContext(), "Go to share task page...", Toast.LENGTH_SHORT).show();
    });

    // Call super method.
    super.onViewCreated(view, savedInstanceState);
  }
}
