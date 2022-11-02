package com.fityan.lister.components;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Calendar;

/**
 * A combination of {@code DatePickerDialog} and {@code TimePickerDialog}.
 *
 * @see DateTimePickerDialog
 * @see TimePickerDialog
 */
public class DateTimePickerDialog {
  private final Calendar calendar;
  private final Context context;
  private final OnDateTimeSetListener onDateTimeSetListener;


  /**
   * Create new {@code DateTimePickerDialog} that include both {@code DatePickerDialog} and
   * {@code TimePickerDialog}.
   *
   * @param context               The context.
   * @param initialDateTime       The initial date time.
   * @param onDateTimeSetListener The listener after {@code DatePickerDialog} and {@code
   *                              TimePickerDialog} is shown.
   */
  public DateTimePickerDialog(
      @NonNull Context context, Calendar initialDateTime,
      @NonNull OnDateTimeSetListener onDateTimeSetListener
  ) {
    this.context = context;
    this.calendar = initialDateTime;
    this.onDateTimeSetListener = onDateTimeSetListener;
  }


  /**
   * Showing {@code DatePickerDialog}, then {@code TimePickerDialog}.
   */
  public void show() {
    /* Initialize date picker dialog. */
    DatePickerDialog datePickerDialog = new DatePickerDialog(context);

    /* After the date is set. */
    datePickerDialog.setOnDateSetListener((datePicker, year, month, dayOfMonth) -> {
      /* After the time is set. */
      TimePickerDialog.OnTimeSetListener onTimeSetListener
          = (timePicker, hourOfDay, minute) -> onDateTimeSetListener.onDateTimeSet(year, month,
          dayOfMonth, hourOfDay, minute);

      /* Show time picker dialog. */
      new TimePickerDialog(context, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
          calendar.get(Calendar.MINUTE), true).show();
    });

    /* Update initial date. */
    datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH));

    /* Show date picker dialog. */
    datePickerDialog.show();
  }


  public interface OnDateTimeSetListener {
    void onDateTimeSet(int year, int month, int dayOfMonth, int hourOfDay, int minute);
  }
}
