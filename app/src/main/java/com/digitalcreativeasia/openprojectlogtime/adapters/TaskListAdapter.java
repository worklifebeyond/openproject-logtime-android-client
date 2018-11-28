package com.digitalcreativeasia.openprojectlogtime.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.digitalcreativeasia.openprojectlogtime.R;
import com.digitalcreativeasia.openprojectlogtime.fragments.DescFragment;
import com.digitalcreativeasia.openprojectlogtime.pojos.task.TaskModel;
import com.digitalcreativeasia.openprojectlogtime.utils.ISO8601;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;

import java.text.ParseException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private List<TaskModel> taskModels;
    private Context context;

    public interface SelectListener{
        void onSelect(TaskModel model);
    }

    private SelectListener listener;

    public TaskListAdapter(Context context, List<TaskModel> taskModels, SelectListener listener) {
        this.context = context;
        this.taskModels = taskModels;
        this.listener = listener;
    }



    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        TaskModel model = taskModels.get(position);
        holder.progress.setProgress(model.getPercentageDone());
        try {
            String lastActivity = ISO8601.toReadable(model.getUpdatedAt());
            holder.textLastActivity.setText(lastActivity);
        } catch (ParseException e) {
            holder.textLastActivity.setText(model.getUpdatedAt());
            e.printStackTrace();
        }
        holder.textProgress.setText(model.getPercentageDone()+"%");

        holder.textTitle.setText(
                String.format("[%d] %s", model.getId(), model.getSubject())
        );

        String projectName = (model.getLinks().getParent().getHref() == null) ?
                model.getLinks().getProject().getTitle() :
                model.getLinks().getParent().getTitle()+" > "+model.getLinks().getProject().getTitle();
        holder.textProjectName.setText(projectName);
        holder.itemView.setOnClickListener(view -> listener.onSelect(model));
        holder.buttonDesc.setOnClickListener(view -> {

            Bundle arg = new Bundle();
            arg.putString(DescFragment.ARGS_CONTENT, model.getDescription().getHtml());
            new FullScreenDialogFragment.Builder(context)
                    .setTitle("Description")
                    //.setConfirmButton("OK")
                    .setContent(DescFragment.class, arg)
                    .build()
            .show(((AppCompatActivity) context).getSupportFragmentManager(), "dialog");

        });

    }


    public void removeItem(int position) {
        taskModels.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return taskModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textProgress;
        ProgressBar progress;
        TextView textLastActivity;
        TextView textTitle;
        TextView textProjectName;
        AppCompatButton buttonDesc;

        public ViewHolder(View view) {
            super(view);

            textProgress = view.findViewById(R.id.text_progress);
            progress = view.findViewById(R.id.progress);
            textLastActivity = view.findViewById(R.id.text_last_activity);
            textTitle = view.findViewById(R.id.text_title);
            textProjectName = view.findViewById(R.id.text_project);
            buttonDesc = view.findViewById(R.id.button_desc);
        }
    }

}
