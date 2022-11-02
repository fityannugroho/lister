package com.fityan.lister.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

public class SharedTaskModel {
  public static final String TASK_ID_FIELD = "taskId";
  public static final String RECIPIENT_ID_FIELD = "recipientId";
  public static final String WRITABLE_FIELD = "writable";
  public static final String DELETABLE_FIELD = "deletable";

  private String id;
  private String taskId;
  private String recipientId;
  private boolean writable;
  private boolean deletable;


  public SharedTaskModel() {}


  public SharedTaskModel(
      String id, String taskId, String recipientId, boolean writable, boolean deletable
  ) {
    this.id = id;
    this.taskId = taskId;
    this.recipientId = recipientId;
    this.writable = writable;
    this.deletable = deletable;
  }


  public SharedTaskModel(@NonNull DocumentSnapshot sharedTask) {
    this.id = sharedTask.getId();
    this.taskId = sharedTask.getString(TASK_ID_FIELD);
    this.recipientId = sharedTask.getString(RECIPIENT_ID_FIELD);

    // Get access modifier.
    Boolean writable = sharedTask.getBoolean(WRITABLE_FIELD);
    Boolean deletable = sharedTask.getBoolean(DELETABLE_FIELD);

    // If null, set access modifier to 'false'. Otherwise, set with the value.
    this.writable = writable != null && writable;
    this.deletable = deletable != null && deletable;
  }


  public String getId() {
    return id;
  }


  public void setId(String id) {
    this.id = id;
  }


  public String getTaskId() {
    return taskId;
  }


  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }


  public boolean isWritable() {
    return writable;
  }


  public void setWritable(boolean writable) {
    this.writable = writable;
  }


  public boolean isDeletable() {
    return deletable;
  }


  public void setDeletable(boolean deletable) {
    this.deletable = deletable;
  }


  public String getRecipientId() {
    return recipientId;
  }


  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }
}
