package com.fityan.tugaskita.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.collections.TaskCollection;
import com.fityan.tugaskita.collections.UserCollection;
import com.fityan.tugaskita.components.ShareTaskDialog;
import com.fityan.tugaskita.helper.InputHelper;
import com.fityan.tugaskita.models.TaskModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Objects;

public class DetailTaskActivity extends AppCompatActivity implements ShareTaskDialog.OnClickShareTaskDialogListener {
  /* Authentication. */
  private final FirebaseAuth auth = FirebaseAuth.getInstance();
  private final FirebaseUser user = auth.getCurrentUser();

  /* Collections */
  private final TaskCollection taskCollection = new TaskCollection();
  private final UserCollection userCollection = new UserCollection();

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


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.detail_task_menu, menu);
    return true;
  }


  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    /* If Share Item is selected. */
    if (item.getItemId() == R.id.shareItem) {
      /* Show share task dialog. */
      ShareTaskDialog shareTaskDialog = new ShareTaskDialog();
      shareTaskDialog.show(getSupportFragmentManager(), "shareTaskDialog");
    }
    return super.onOptionsItemSelected(item);
  }


  @Override
  public void onClickShareButton(String email, boolean isWritable, boolean isDeletable) {
    /* Check if email is exists. */
    userCollection.findByEmail(email).addOnSuccessListener(querySnapshot -> {
      try {
        /* If email doesn't exists. */
        if (querySnapshot.isEmpty())
          throw new Exception("Email " + email + " doesn't exists.");

        /* If email doesn't exists. */
        if (email.equals(user.getEmail()))
          throw new Exception("Can't share the task to yourself.");

        /* TODO: If email is valid, create new shared task. */
        // code here
      } catch (Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }
}
