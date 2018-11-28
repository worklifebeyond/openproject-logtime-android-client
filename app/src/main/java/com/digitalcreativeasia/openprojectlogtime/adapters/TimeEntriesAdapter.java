package com.digitalcreativeasia.openprojectlogtime.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitalcreativeasia.openprojectlogtime.R;
import com.digitalcreativeasia.openprojectlogtime.pojos.timeentry.TimeEntry;
import com.digitalcreativeasia.openprojectlogtime.utils.ISO8601;

import org.joda.time.Duration;
import org.joda.time.Period;

import java.text.ParseException;
import java.util.List;


import javax.xml.datatype.DatatypeConfigurationException;

import androidx.recyclerview.widget.RecyclerView;

public class TimeEntriesAdapter extends RecyclerView.Adapter<TimeEntriesAdapter.ViewHolder> {

    private List<TimeEntry> timeEntries;
    private Context context;

    public interface SelectListener{
        void onSelect(TimeEntry model);
    }

    private SelectListener listener;

    public TimeEntriesAdapter(Context context, List<TimeEntry> timeEntries, SelectListener listener) {
        this.context = context;
        this.timeEntries = timeEntries;
        this.listener = listener;
    }



    @Override
    public TimeEntriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_entry, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        TimeEntry timeEntry = timeEntries.get(position);
        try {
            String lastActivity = ISO8601.toReadable(timeEntry.getUpdatedAt());
            holder.textLastActivity.setText(lastActivity);
        } catch (ParseException e) {
            holder.textLastActivity.setText(timeEntry.getUpdatedAt());
            e.printStackTrace();
        }
        try {
            Period period = Period.parse(timeEntry.getHours());
            holder.textSpent.setText("Duration (H:M) "+period.getHours()+":"+period.getMinutes());
        } catch (Exception e) {
            e.printStackTrace();
            holder.textSpent.setText(timeEntry.getHours());
        }
        holder.textTitle.setText(timeEntry.getComment());
        holder.textActivity.setText(timeEntry.getLinks().getActivity().getTitle());

    }


    public void removeItem(int position) {
        timeEntries.remove(position);
        notifyItemRemoved(position);
    }

    public long getDuration(String input) {
        String time = input.substring(2);
        long duration = 0L;
        Object[][] indexs = new Object[][]{{"H", 3600}, {"M", 60}, {"S", 1}};
        for(int i = 0; i < indexs.length; i++) {
            int index = time.indexOf((String) indexs[i][0]);
            if(index != -1) {
                String value = time.substring(0, index);
                duration += Integer.parseInt(value) * (int) indexs[i][1] * 1000;
                time = time.substring(value.length() + 1);
            }
        }
        return duration;
    }

    @Override
    public int getItemCount() {
        return timeEntries.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView textLastActivity, textSpent, textTitle, textActivity;


        public ViewHolder(View view) {
            super(view);

           textLastActivity = view.findViewById(R.id.text_last_activity);
           textSpent = view.findViewById(R.id.text_spent);
           textTitle = view.findViewById(R.id.text_title);
           textActivity = view.findViewById(R.id.text_activity);
        }
    }

}

