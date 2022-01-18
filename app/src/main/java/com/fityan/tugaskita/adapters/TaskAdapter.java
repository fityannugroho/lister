package com.fityan.tugaskita.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.collections.SharedTaskCollection;
import com.fityan.tugaskita.helper.InputHelper;
import com.fityan.tugaskita.models.SharedTaskModel;
import com.fityan.tugaskita.models.TaskModel;
import com.fityan.tugaskita.models.UserModel;

import java.util.ArrayList;
import java.util.Date;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskListViewHolder> {
  /**
   * List of task.
   */
  private final ArrayList<TaskModel> tasks;

  /**
   * Closure to handle actions of task item.
   */
  private final OnItemListener onItemListener;

  /**
   * Logged user.
   */
  private final UserModel loggedUser;

  /**
   * The collection of shared task.
   */
  private final SharedTaskCollection sharedTaskCollection = new SharedTaskCollection();


  public TaskAdapter(
      ArrayList<TaskModel> tasks, UserModel loggedUser, OnItemListener onItemListener
  ) {
    this.tasks = tasks;
    this.loggedUser = loggedUser;
    this.onItemListener = onItemListener;
  }


  @NonNull
  @Override
  public TaskListViewHolder onCreateViewHolder(
      @NonNull ViewGroup parent, int viewType
  ) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_task, parent, false);
    return new TaskListViewHolder(itemView, onItemListener);
  }


  @Override
  public void onBindViewHolder(
      @NonNull TaskListViewHolder holder, int position
  ) {
    TaskModel task = tasks.get(position);
    Date deadline = task.getDeadline().toDate();

    /* Set display of task items. */
    holder.tvTitle.setText(task.getTitle());
    holder.tvDeadline.setText(InputHelper.dateToString(deadline));

    /* Set modifier access. */
    if (task.getOwnerId().equals(loggedUser.getId())) {
      /* Full access is granted to task owner. */
      holder.btnEdit.setVisibility(View.VISIBLE);
      holder.btnDelete.setVisibility(View.VISIBLE);
    } else {
      /* Set modifier access for shared task. */
      sharedTaskCollection.find(task.getId(), loggedUser.getId())
          .addOnSuccessListener(querySnapshot -> {
            if (querySnapshot.size() == 1) {
              SharedTaskModel sharedTask = new SharedTaskModel(querySnapshot.getDocuments().get(0));

              if (sharedTask.isWritable())
                holder.btnEdit.setVisibility(View.VISIBLE);
              if (sharedTask.isDeletable())
                holder.btnDelete.setVisibility(View.VISIBLE);
            }
          })
          .addOnFailureListener(e -> Log.e("taskAccess", e.getMessage(), e));
    }
  }


  @Override
  public int getItemCount() {
    return tasks.size();
  }


  public interface OnItemListener {
    /**
     * Set actions when item is clicked.
     *
     * @param position The position of item.
     */
    void onItemClick(int position);

    /**
     * Set delete actions when Delete Button on item is clicked.
     *
     * @param position The position of item.
     */
    void onDeleteItem(int position);

    /**
     * Set edit actions when Edit Button on item is clicked.
     *
     * @param position The position of item.
     */
    void onEditItem(int position);
  }


  static class TaskListViewHolder extends RecyclerView.ViewHolder {
    /* View elements */
    protected final TextView tvTitle;
    protected final TextView tvDeadline;
    protected final ImageButton btnEdit;
    protected final ImageButton btnDelete;

    /**
     * Closure to handle actions of task item.
     */
    final TaskAdapter.OnItemListener onItemListener;


    public TaskListViewHolder(
        @NonNull View itemView, OnItemListener onItemListener
    ) {
      super(itemView);

      /* Initialize view elements. */
      tvTitle = itemView.findViewById(R.id.tvTitle);
      tvDeadline = itemView.findViewById(R.id.tvDeadline);
      btnEdit = itemView.findViewById(R.id.btnEdit);
      btnDelete = itemView.findViewById(R.id.btnDelete);

      /* Initialize the itemListener closure. */
      this.onItemListener = onItemListener;


      /* Set actions when item is clicked */
      itemView.setOnClickListener(view -> onItemListener.onItemClick(getAdapterPosition()));

      /* Set delete actions when Delete Button is clicked */
      btnDelete.setOnClickListener(view -> onItemListener.onDeleteItem(getAdapterPosition()));

      /* Set edit actions when Edit Button is clicked */
      btnEdit.setOnClickListener(view -> onItemListener.onEditItem(getAdapterPosition()));
    }
  }
}
