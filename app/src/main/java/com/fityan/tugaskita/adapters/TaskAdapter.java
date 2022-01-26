package com.fityan.tugaskita.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fityan.tugaskita.R;
import com.fityan.tugaskita.helper.InputHelper;
import com.fityan.tugaskita.models.TaskModel;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskListViewHolder> {
  /**
   * List of task.
   */
  private final ArrayList<TaskModel> tasks;

  /**
   * Closure to handle actions of task item.
   */
  private final OnItemListener onItemListener;


  public TaskAdapter(ArrayList<TaskModel> tasks, OnItemListener onItemListener) {
    this.tasks = tasks;
    this.onItemListener = onItemListener;
  }


  @NonNull
  @Override
  public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_task, parent, false);
    return new TaskListViewHolder(itemView, onItemListener);
  }


  @Override
  public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
    TaskModel task = tasks.get(position);

    // Set display of task items.
    holder.tvTitle.setText(task.getTitle());
    holder.tvDeadline.setText(InputHelper.dateToString(task.getDeadline().toDate()));
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
  }


  static class TaskListViewHolder extends RecyclerView.ViewHolder {
    // View elements.
    protected final TextView tvTitle;
    protected final TextView tvDeadline;

    /**
     * Closure to handle actions of task item.
     */
    final TaskAdapter.OnItemListener onItemListener;


    public TaskListViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
      super(itemView);

      // Initialize view elements.
      tvTitle = itemView.findViewById(R.id.tvTitle);
      tvDeadline = itemView.findViewById(R.id.tvDeadline);

      // Initialize the itemListener closure.
      this.onItemListener = onItemListener;

      // Set actions when item is clicked
      itemView.setOnClickListener(view -> onItemListener.onItemClick(getAdapterPosition()));
    }
  }
}
