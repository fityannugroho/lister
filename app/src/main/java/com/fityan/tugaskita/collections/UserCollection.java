package com.fityan.tugaskita.collections;

import com.fityan.tugaskita.models.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserCollection {
  public static final String COLLECTION_PATH = "Users";
  private final CollectionReference collection = FirebaseFirestore.getInstance()
      .collection(COLLECTION_PATH);


  public UserCollection() {}


  public Task<DocumentSnapshot> findOne(String userId) {
    return collection.document(userId).get();
  }


  /**
   * Update the existing document if id is already exist. Otherwise, add to new document.
   *
   * @param id    The id of user, that will be set as the id of document.
   * @param email The email of user.
   * @param name  The name of user.
   * @return Task
   */
  public Task<Void> save(String id, String email, String name) {
    Map<String, Object> data = new HashMap<>();
    data.put(UserModel.EMAIL_FIELD, email);
    data.put(UserModel.NAME_FIELD, name);

    return collection.document(id).set(data);
  }


  public Task<Void> update(UserModel user) {
    return collection.document(user.getId())
        .update(UserModel.EMAIL_FIELD, user.getEmail(), UserModel.NAME_FIELD, user.getName());
  }


  public Task<Void> delete(String userId) {
    return collection.document(userId).delete();
  }
}
