package com.fityan.tugaskita.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.helper.InputHelper;
import com.fityan.tugaskita.validation.EmailValidation;
import com.fityan.tugaskita.validation.EmailValidationException;

public class ShareTaskDialog extends AppCompatDialogFragment {
  /* View elements. */
  private EditText inputEmail;
  private CheckBox checkBoxWritable, checkBoxDeletable;

  private OnClickShareTaskDialogListener onClickListener;


  @NonNull
  @Override
  public Dialog onCreateDialog(
      @Nullable Bundle savedInstanceState
  ) {
    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View view = inflater.inflate(R.layout.share_task_dialog, null);

    /* Initialize view elements. */
    inputEmail = view.findViewById(R.id.inputEmail);
    checkBoxWritable = view.findViewById(R.id.checkBoxWritable);
    checkBoxDeletable = view.findViewById(R.id.checkBoxDeletable);

    AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity()).setView(view)
        .setTitle("Share Task")
        .setNegativeButton("Cancel", (dialogInterface, i) -> {})
        .setPositiveButton("Share", (dialogInterface, i) -> {
          try {
            String email = InputHelper.getRequiredInput(inputEmail);
            boolean isWritable = checkBoxWritable.isChecked();
            boolean isDeletable = checkBoxDeletable.isChecked();

            /* Validate email. */
            if (!EmailValidation.isEmailValid(email))
              throw new EmailValidationException("Invalid email format!");

            /* Execute on click share button listener. */
            onClickListener.onClickShareButton(email, isWritable, isDeletable);
          } catch (NullPointerException | EmailValidationException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });

    return alertDialog.create();
  }


  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    try {
      onClickListener = (OnClickShareTaskDialogListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException("OnClickListener is not implemented.");
    }
  }


  public interface OnClickShareTaskDialogListener {
    void onClickShareButton(
        String email, boolean isWritable, boolean isDeletable
    );
  }
}
