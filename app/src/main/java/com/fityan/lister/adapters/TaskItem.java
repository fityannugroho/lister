package com.fityan.lister.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fityan.lister.models.TaskModel;

import java.util.Date;


/**
 * The item of {@code TaskAdapter}.
 *
 * @see com.fityan.lister.adapters.TaskAdapter TaskAdapter
 */
public class TaskItem {
  private String id;
  private String title;
  private Date deadline;
  private String ownerName;


  public TaskItem(
      @NonNull String id, @NonNull String title, @NonNull Date deadline, @Nullable String ownerName
  ) {
    this.id = id;
    this.title = title;
    this.deadline = deadline;
    this.ownerName = ownerName;
  }


  public TaskItem(@NonNull TaskModel task) {
    this.id = task.getId();
    this.title = task.getTitle();
    this.deadline = task.getDeadline().toDate();
  }


  public String getId() {
    return id;
  }


  public void setId(String id) {
    this.id = id;
  }


  public String getTitle() {
    return title;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public Date getDeadline() {
    return deadline;
  }


  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }


  public String getOwnerName() {
    return ownerName;
  }


  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }
}
