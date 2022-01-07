package com.fityan.tugaskita.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.collections.TaskCollection;
import com.fityan.tugaskita.components.DateTimePickerDialog;
import com.fityan.tugaskita.helper.InputHelper;
import com.fityan.tugaskita.models.TaskModel;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {
  private final Calendar calendar = Calendar.getInstance();
  private final TaskCollection taskCollection = new TaskCollection();
  private TaskModel taskModel;

  /* View elements. */
  private Button btnEditTask;
  private EditText inputTitle, inputDescription, inputDeadline;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_task);

    /* Initialize view elements. */
    btnEditTask = findViewById(R.id.btnEditTask);
    inputTitle = findViewById(R.id.inputTitle);
    inputDescription = findViewById(R.id.inputDescription);
    inputDeadline = findViewById(R.id.inputDeadline);


    /* Customize action bar. */
    try {
      customizeActionBar();
    } catch (NullPointerException e) {
      Log.i("actionBar", e.getMessage(), e);
    }


    /* When Deadline Input is clicked. */
    inputDeadline.setOnClickListener(view -> {
      /* Initialize date time picker. */
      DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(this, calendar,
          (year, month, dayOfMonth, hourOfDay, minute) -> {
            /* Set calendar with deadline input. */
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            /* Format the date, then put to input field. */
            inputDeadline.setText(InputHelper.dateToInput(calendar.getTime()));
          });

      /* Show date time picker. */
      dateTimePickerDialog.show();
    });


    /* When Edit Button is clicked. */
    btnEditTask.setOnClickListener(view -> {
      /* Edit the task. */
      try {
        String title = InputHelper.getRequiredInput(inputTitle);
        String description = InputHelper.getRequiredInput(inputDescription);
        String strDeadline = InputHelper.getRequiredInput(inputDeadline);

        Timestamp deadline = new Timestamp(InputHelper.inputToDate(strDeadline));

        taskModel.setTitle(title);
        taskModel.setDescription(description);
        taskModel.setDeadline(deadline);

        /* Edit new task. */
        taskCollection.update(taskModel).addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            /* If success, finish this activity. */
            Toast.makeText(this, "Task successfully edited.", Toast.LENGTH_SHORT).show();
            finish();
          } else {
            Toast.makeText(this, "Failed to edit task.", Toast.LENGTH_SHORT).show();
            Log.e("editTask", "Failed to edit task.", task.getException());
          }
        });
      } catch (NullPointerException e) {
        Log.i("inputValidation", e.getMessage(), e);
      } catch (ParseException e) {
        Log.e("inputValidation", "Failed to parse deadline.", e);
        inputDeadline.setError("Invalid deadline format");
      }
    });
  }


  @Override
  protected void onStart() {
    super.onStart();

    /* Get the task. */
    taskCollection.findOne(getIntent().getStringExtra("taskId"))
        .addOnSuccessListener(documentSnapshot -> {
          taskModel = new TaskModel(documentSnapshot.getId(),
              documentSnapshot.getString(TaskModel.TITLE_FIELD),
              documentSnapshot.getString(TaskModel.DESCRIPTION_FIELD),
              documentSnapshot.getTimestamp(TaskModel.DEADLINE_FIELD),
              documentSnapshot.getString(TaskModel.OWNER_ID_FIELD));

          /* Set calendar to deadline value. */
          calendar.setTime(taskModel.getDeadline().toDate());

          /* Display the task data. */
          inputTitle.setText(taskModel.getTitle());
          inputDescription.setText(taskModel.getDescription());
          inputDeadline.setText(InputHelper.dateToInput(taskModel.getDeadline().toDate()));
        });
  }


  /**
   * Customize the action bar for this activity here.
   */
  private void customizeActionBar() {
    ActionBar actionBar = getSupportActionBar();

    if (actionBar == null)
      throw new NullPointerException("There are no action bar in this activity.");

    /* Set the title of action bar. */
    actionBar.setTitle("Edit Task");
  }
}
