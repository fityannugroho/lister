package com.fityan.tugaskita.collections;

import com.fityan.tugaskita.models.TaskModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class TaskCollection {
  public static final String COLLECTION_PATH = "Tasks";
  private final CollectionReference collection = FirebaseFirestore.getInstance()
      .collection(COLLECTION_PATH);


  public TaskCollection() {}


  public Task<DocumentSnapshot> findOne(String taskId) {
    return collection.document(taskId).get();
  }


  public Task<QuerySnapshot> findAll(String ownerId) {
    return collection.whereEqualTo(TaskModel.OWNER_ID_FIELD, ownerId).get();
  }


  public Task<QuerySnapshot> findAll(String ownerId, String orderByField, boolean isAscending) {
    return collection.whereEqualTo(TaskModel.OWNER_ID_FIELD, ownerId)
        .orderBy(orderByField, isAscending ? Query.Direction.ASCENDING : Query.Direction.DESCENDING)
        .get();
  }


  public Task<DocumentReference> insert(
      String title, String description, Timestamp deadline, String ownerId
  ) {
    Map<String, Object> data = new HashMap<>();
    data.put(TaskModel.TITLE_FIELD, title);
    data.put(TaskModel.DESCRIPTION_FIELD, description);
    data.put(TaskModel.DEADLINE_FIELD, deadline);
    data.put(TaskModel.OWNER_ID_FIELD, ownerId);

    return collection.add(data);
  }


  public Task<Void> update(TaskModel task) {
    return collection.document(task.getId())
        .update(TaskModel.TITLE_FIELD, task.getTitle(), TaskModel.DESCRIPTION_FIELD,
            task.getDescription(), TaskModel.DEADLINE_FIELD, task.getDeadline());
  }


  public Task<Void> delete(String taskId) {
    return collection.document(taskId).delete();
  }
}
