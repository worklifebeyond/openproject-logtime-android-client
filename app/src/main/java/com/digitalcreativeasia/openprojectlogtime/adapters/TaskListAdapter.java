package com.digitalcreativeasia.openprojectlogtime.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.digitalcreativeasia.openprojectlogtime.App;
import com.digitalcreativeasia.openprojectlogtime.R;
import com.digitalcreativeasia.openprojectlogtime.fragments.DescFragment;
import com.digitalcreativeasia.openprojectlogtime.pojos.task.TaskModel;
import com.digitalcreativeasia.openprojectlogtime.utils.ISO8601;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Credentials;
import okhttp3.Response;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private List<TaskModel> taskModels;
    private Context context;

    private ProgressDialog progressDialog;

    public interface SelectListener {
        void onSelect(TaskModel model);

        void onRefresh(boolean success);
    }

    private SelectListener listener;

    public TaskListAdapter(Context context, List<TaskModel> taskModels, SelectListener listener) {
        this.context = context;
        this.taskModels = taskModels;
        this.listener = listener;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Change percentage...");
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


        holder.buttonChange.setOnClickListener(view -> {
            if (holder.progress.isEnabled()) {
                holder.buttonChange.setText("CHANGE");
                holder.progress.setEnabled(false);
                updatePercentage(holder.progress.getProgress(),
                        model.getLockVersion(), String.valueOf(model.getId()));
            } else {
                holder.buttonChange.setText("DONE");
                holder.progress.setEnabled(true);
            }
        });

        holder.progress.setProgress(model.getPercentageDone());
        holder.progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                holder.textProgress.setText(String.valueOf(i) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        try {
            String lastActivity = ISO8601.toReadable(model.getUpdatedAt());
            holder.textLastActivity.setText(lastActivity);
        } catch (ParseException e) {
            holder.textLastActivity.setText(model.getUpdatedAt());
            e.printStackTrace();
        }
        holder.textProgress.setText(model.getPercentageDone() + "%");

        holder.textTitle.setText(
                String.format("[%d] %s", model.getId(), model.getSubject())
        );

        String projectName = (model.getLinks().getParent().getHref() == null) ?
                model.getLinks().getProject().getTitle() :
                model.getLinks().getParent().getTitle() + " > " + model.getLinks().getProject().getTitle();
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
        AppCompatSeekBar progress;
        TextView textLastActivity;
        TextView textTitle;
        TextView textProjectName;
        AppCompatButton buttonDesc, buttonChange;

        public ViewHolder(View view) {
            super(view);

            textProgress = view.findViewById(R.id.text_progress);
            progress = view.findViewById(R.id.progress);
            textLastActivity = view.findViewById(R.id.text_last_activity);
            textTitle = view.findViewById(R.id.text_title);
            textProjectName = view.findViewById(R.id.text_project);
            buttonDesc = view.findViewById(R.id.button_desc);
            buttonChange = view.findViewById(R.id.button_change);

            progress.setEnabled(false);
        }
    }


    void updatePercentage(int percentage, int lockVersion, String wpId) {

        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("lockVersion", lockVersion);
            object.put("percentageDone", percentage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = context.getString(R.string.baseUrl) + App.PATH.UPDATE_PERCENTAGE + wpId;
        String apiKey = App.getTinyDB().getString(App.KEY.API, "");
        AndroidNetworking.patch(url)
                .addHeaders("Authorization", Credentials.basic("apikey", apiKey))
                .addJSONObjectBody(object)
                .setPriority(Priority.HIGH)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        progressDialog.dismiss();
                        listener.onRefresh(true);
                    }

                    @Override
                    public void onError(ANError err) {
                        progressDialog.dismiss();
                        listener.onRefresh(false);
                    }
                });
    }

}
