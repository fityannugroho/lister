package com.fityan.tugaskita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.collections.TaskCollection;
import com.fityan.tugaskita.helper.DateTimePickerDialog;
import com.fityan.tugaskita.helper.InputHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {
  /* Logged user. */
  private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
  private final Calendar calendar = Calendar.getInstance();
  private final TaskCollection taskCollection = new TaskCollection();

  /* View elements. */
  private Button btnAddTask;
  private EditText inputTitle, inputDescription, inputDeadline;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_task);

    /* Initialize view elements. */
    btnAddTask = findViewById(R.id.btnAddTask);
    inputTitle = findViewById(R.id.inputTitle);
    inputDescription = findViewById(R.id.inputDescription);
    inputDeadline = findViewById(R.id.inputDeadline);


    /* When Deadline Input is clicked. */
    inputDeadline.setOnClickListener(view -> {
      /* Initialize date time picker. */
      DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(this, calendar,
          (year, month, dayOfMonth, hourOfDay, minute) -> {
            /* Get input datetime. */
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


    /* When Add Task Button is clicked. */
    btnAddTask.setOnClickListener(view -> {
      /* Add new task. */
      try {
        String title = InputHelper.getRequiredInput(inputTitle);
        String description = InputHelper.getRequiredInput(inputDescription);
        String strDeadline = InputHelper.getRequiredInput(inputDeadline);

        Timestamp deadline = new Timestamp(InputHelper.inputToDate(strDeadline));

        /* Add new task. */
        taskCollection.insert(title, description, deadline, user.getUid())
            .addOnSuccessListener(documentReference -> {
              /* If success, go to Main Page. */
              Toast.makeText(this, "Task successfully added.", Toast.LENGTH_SHORT).show();
              startActivity(new Intent(this, MainActivity.class));
              finish();
            })
            .addOnFailureListener(e -> {
              Toast.makeText(this, "Failed to add task.", Toast.LENGTH_SHORT).show();
              Log.e("addTask", e.getMessage(), e);
            });
      } catch (NullPointerException e) {
        Log.i("inputValidation", e.getMessage(), e);
      } catch (ParseException e) {
        Log.e("inputValidation", "Failed to parse deadline.", e);
      }
    });
  }
}
