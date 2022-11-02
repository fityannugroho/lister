package com.fityan.lister.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fityan.lister.R;
import com.fityan.lister.collections.UserCollection;
import com.fityan.lister.models.SharedTaskModel;
import com.fityan.lister.models.UserModel;

import java.util.ArrayList;

public class SharedTaskAdapter extends RecyclerView.Adapter<SharedTaskAdapter.SharedTaskListViewHolder> {
  /**
   * List of shared task.
   */
  private final ArrayList<SharedTaskModel> sharedTasks;

  /**
   * Closure to handle actions of shared task item.
   */
  private final SharedTaskAdapter.OnItemListener onItemListener;

  /**
   * User collection in FireStore.
   */
  private final UserCollection userCollection = new UserCollection();


  public SharedTaskAdapter(ArrayList<SharedTaskModel> sharedTasks, OnItemListener onItemListener) {
    this.sharedTasks = sharedTasks;
    this.onItemListener = onItemListener;
  }


  @NonNull
  @Override
  public SharedTaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_shared_task, parent, false);
    return new SharedTaskListViewHolder(itemView, onItemListener);
  }


  @Override
  public void onBindViewHolder(@NonNull SharedTaskListViewHolder holder, int position) {
    SharedTaskModel sharedTask = sharedTasks.get(position);

    // Get recipient data.
    userCollection.findOne(sharedTask.getRecipientId()).addOnSuccessListener(documentSnapshot -> {
      UserModel recipient = new UserModel(documentSnapshot);
      // Display user data.
      holder.tvName.setText(recipient.getName());
      holder.tvEmail.setText(recipient.getEmail());
    }).addOnFailureListener(e -> Log.e("getUser", e.getMessage(), e));

    // Display access modifier info.
    if (sharedTask.isWritable())
      holder.imgEditAccess.setVisibility(View.VISIBLE);
    if (sharedTask.isDeletable())
      holder.imgDeleteAccess.setVisibility(View.VISIBLE);
  }


  @Override
  public int getItemCount() {
    return sharedTasks.size();
  }


  public interface OnItemListener {
    /**
     * Set actions when item is clicked.
     *
     * @param position The position of item.
     */
    void onItemClick(int position);
  }


  static class SharedTaskListViewHolder extends RecyclerView.ViewHolder {
    // View elements
    protected final TextView tvName;
    protected final TextView tvEmail;
    protected final ImageView imgReadAccess;
    protected final ImageView imgEditAccess;
    protected final ImageView imgDeleteAccess;

    /**
     * Closure to handle actions of shared task item.
     */
    final SharedTaskAdapter.OnItemListener onItemListener;


    public SharedTaskListViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
      super(itemView);

      tvName = itemView.findViewById(R.id.tvName);
      tvEmail = itemView.findViewById(R.id.tvEmail);
      imgReadAccess = itemView.findViewById(R.id.imgReadAccess);
      imgEditAccess = itemView.findViewById(R.id.imgEditAccess);
      imgDeleteAccess = itemView.findViewById(R.id.imgDeleteAccess);

      // Initialize the itemListener closure.
      this.onItemListener = onItemListener;

      /* Set actions when item is clicked */
      itemView.setOnClickListener(view -> onItemListener.onItemClick(getAdapterPosition()));
    }
  }
}
