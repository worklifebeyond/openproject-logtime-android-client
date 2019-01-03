package com.digitalcreativeasia.openprojectlogtime.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.digitalcreativeasia.openprojectlogtime.App;
import com.digitalcreativeasia.openprojectlogtime.R;
import com.digitalcreativeasia.openprojectlogtime.fragments.DescFragment;
import com.digitalcreativeasia.openprojectlogtime.pojos.StatusModel;
import com.digitalcreativeasia.openprojectlogtime.pojos.task.TaskModel;
import com.digitalcreativeasia.openprojectlogtime.utils.Commons;
import com.digitalcreativeasia.openprojectlogtime.utils.ISO8601;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.angmarch.views.NiceSpinner;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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

        if (Commons.isStatusStored()) {
            int statPos = 0;
            List<Object> statuses = App.getTinyDB().getListObject(App.KEY.LIST_STATUSES, StatusModel.class);
            List<String> statStrings = new ArrayList<>();
            for (int i = 0; i < statuses.size(); i++) {
                StatusModel stat = (StatusModel) statuses.get(i);
                statStrings.add(stat.getName());
                if (stat.getName().equals(model.getLinks().getStatus().getTitle())) {
                    statPos = i;
                }
            }
            holder.textType.setText(model.getLinks().getType().getTitle());
            holder.spinnerStatus.attachDataSource(statStrings);
            holder.spinnerStatus.setSelectedIndex(statPos);
            holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    updateStatus((StatusModel) statuses.get(i),
                            model.getLockVersion(), String.valueOf(model.getId()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            List<String> status = new ArrayList<>();
            status.add(model.getLinks().getStatus().getTitle());
            holder.spinnerStatus.attachDataSource(status);
        }


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
        holder.buttonTimeEntry.setOnClickListener(view -> listener.onSelect(model));
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

        holder.textFrom.setPaintFlags(holder.textFrom.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.textTo.setPaintFlags(holder.textTo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (model.getStartDate() == null) {
            holder.textFrom.setText("No Start Date");
        } else holder.textFrom.setText(model.getStartDate());

        if (model.getDueDate() == null) {
            holder.textTo.setText("No Due Date");
        } else holder.textTo.setText(model.getDueDate());


        holder.textFrom.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    (v, year, monthOfYear, dayOfMonth) -> {
                        String date = "" + year + "-" +
                                Commons.normalizeNonZero((++monthOfYear)) + "-" + Commons.normalizeNonZero(dayOfMonth);
                        holder.textFrom.setText(date);
                        updateDate(date, model.getLockVersion(), String.valueOf(model.getId()), true);
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(((AppCompatActivity) context).getSupportFragmentManager(), "Datepickerdialog");
        });



        holder.textTo.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    (v, year, monthOfYear, dayOfMonth) -> {
                        String date = "" + year + "-" +
                                Commons.normalizeNonZero((++monthOfYear)) + "-" + Commons.normalizeNonZero(dayOfMonth);
                        holder.textTo.setText(date);
                        updateDate(date, model.getLockVersion(), String.valueOf(model.getId()), false);
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(((AppCompatActivity) context).getSupportFragmentManager(), "Datepickerdialog");
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
        TextView textFrom, textTo;
        AppCompatButton buttonDesc, buttonChange, buttonTimeEntry;
        NiceSpinner spinnerStatus;
        TextView textType;

        public ViewHolder(View view) {
            super(view);

            textProgress = view.findViewById(R.id.text_progress);
            progress = view.findViewById(R.id.progress);
            textLastActivity = view.findViewById(R.id.text_last_activity);
            textTitle = view.findViewById(R.id.text_title);
            textProjectName = view.findViewById(R.id.text_project);
            buttonDesc = view.findViewById(R.id.button_desc);
            buttonChange = view.findViewById(R.id.button_change);
            buttonTimeEntry = view.findViewById(R.id.button_time);
            textType = view.findViewById(R.id.text_type);
            spinnerStatus = view.findViewById(R.id.spinner_status);

            progress.setEnabled(false);

            textFrom = view.findViewById(R.id.text_from);
            textTo = view.findViewById(R.id.text_to);
        }
    }


    void updateStatus(StatusModel model, int lockVersion, String wpId) {
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("lockVersion", lockVersion);
            JSONObject _link = new JSONObject();
            JSONObject _status = new JSONObject();
            _status.put("href", "/project/api/v3/statuses/" + model.getId());
            _status.put("title", model.getName());
            _link.put("status", _status);
            object.put("_links", _link);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = context.getString(R.string.baseUrl) + App.PATH.UPDATE_WORK_PACKAGES + wpId;
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

    void updatePercentage(int percentage, int lockVersion, String wpId) {

        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("lockVersion", lockVersion);
            object.put("percentageDone", percentage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = context.getString(R.string.baseUrl) + App.PATH.UPDATE_WORK_PACKAGES + wpId;
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


    void updateDate(String date, int lockVersion, String wpId, boolean isStartdate) {
        progressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("lockVersion", lockVersion);
            if (isStartdate)
                object.put("startDate", date);
            else object.put("dueDate", date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = context.getString(R.string.baseUrl) + App.PATH.UPDATE_WORK_PACKAGES + wpId;
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
