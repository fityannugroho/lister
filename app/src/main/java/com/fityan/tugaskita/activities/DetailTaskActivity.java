package com.fityan.tugaskita.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.collections.TaskCollection;
import com.fityan.tugaskita.helper.InputHelper;
import com.fityan.tugaskita.models.TaskModel;
import com.google.firebase.Timestamp;

import java.util.Locale;
import java.util.Objects;

public class DetailTaskActivity extends AppCompatActivity {
  /* Collections */
  private final TaskCollection taskCollection = new TaskCollection();
  /* View elements. */
  private TextView tvTitle, tvDescription, tvDeadline;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_task);

    /* Initialize view elements. */
    tvTitle = findViewById(R.id.tvTitle);
    tvDescription = findViewById(R.id.tvDescription);
    tvDeadline = findViewById(R.id.tvDeadline);
  }


  @Override
  protected void onStart() {
    super.onStart();

    /* Get the task. */
    taskCollection.findOne(getIntent().getStringExtra("taskId"))
        .addOnSuccessListener(documentSnapshot -> {
          /* Display the task data. */
          Timestamp deadline = documentSnapshot.getTimestamp(TaskModel.DEADLINE_FIELD);
          String strDeadline = InputHelper.dateToString(Objects.requireNonNull(deadline).toDate(),
              InputHelper.DATE_FORMAT_HUMAN_LONG_US, Locale.US);

          tvTitle.setText(documentSnapshot.getString(TaskModel.TITLE_FIELD));
          tvDescription.setText(documentSnapshot.getString(TaskModel.DESCRIPTION_FIELD));
          tvDeadline.setText(strDeadline);
        });
  }
}
