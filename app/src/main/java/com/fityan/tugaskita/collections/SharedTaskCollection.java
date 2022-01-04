package com.fityan.tugaskita.collections;

import com.fityan.tugaskita.models.SharedTaskModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SharedTaskCollection {

  public static final String COLLECTION_PATH = "SharedTasks";
  private final CollectionReference collection = FirebaseFirestore.getInstance()
      .collection(COLLECTION_PATH);


  public SharedTaskCollection() {}


  public Task<QuerySnapshot> findByTask(String taskId) {
    return collection.whereEqualTo(SharedTaskModel.TASK_ID_FIELD, taskId).get();
  }


  public Task<QuerySnapshot> findByRecipient(String recipientId) {
    return collection.whereEqualTo(SharedTaskModel.RECIPIENT_ID_FIELD, recipientId).get();
  }


  public Task<DocumentReference> insert(
      String taskId, String recipientId, boolean writable, boolean deletable
  ) {
    Map<String, Object> data = new HashMap<>();
    data.put(SharedTaskModel.TASK_ID_FIELD, taskId);
    data.put(SharedTaskModel.RECIPIENT_ID_FIELD, recipientId);
    data.put(SharedTaskModel.WRITABLE_FIELD, writable);
    data.put(SharedTaskModel.DELETABLE_FIELD, deletable);

    return collection.add(data);
  }


  public Task<Void> update(SharedTaskModel sharedTask) {
    return collection.document(sharedTask.getId())
        .update(SharedTaskModel.TASK_ID_FIELD, sharedTask.getTaskId(),
            SharedTaskModel.RECIPIENT_ID_FIELD, sharedTask.getRecipientId(),
            SharedTaskModel.WRITABLE_FIELD, sharedTask.isWritable(),
            SharedTaskModel.DELETABLE_FIELD, sharedTask.isDeletable());
  }


  public Task<Void> delete(String sharedTaskId) {
    return collection.document(sharedTaskId).delete();
  }
}
