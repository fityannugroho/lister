package com.fityan.tugaskita.models;

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
