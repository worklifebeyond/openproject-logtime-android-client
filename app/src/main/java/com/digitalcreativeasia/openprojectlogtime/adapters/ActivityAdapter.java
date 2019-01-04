package com.digitalcreativeasia.openprojectlogtime.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitalcreativeasia.openprojectlogtime.R;
import com.digitalcreativeasia.openprojectlogtime.pojos.activity.ActivityModel;
import com.digitalcreativeasia.openprojectlogtime.pojos.activity.RowComment;
import com.digitalcreativeasia.openprojectlogtime.utils.ISO8601;

import java.text.ParseException;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<RowComment> activityModels;
    private Context context;

    public interface SelectListener {
        void onSelect(ActivityModel model);
    }

    private SelectListener listener;

    public ActivityAdapter(Context context, List<RowComment> activityModels, SelectListener listener) {
        this.context = context;
        this.activityModels = activityModels;
        this.listener = listener;
    }


    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        RowComment model = activityModels.get(position);
        if (model.getDate().equals("")) {
            holder.dateText.setText(model.getDate());
            holder.dateText.setVisibility(View.GONE);
        } else {
            try {
                String date = ISO8601.toReadable(model.getDate());
                holder.dateText.setText(date);
                holder.dateText.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                holder.dateText.setText(model.getDate());
                e.printStackTrace();
            }
        }
        String text =
                model.getContent().replaceAll("<strong>", "<b>").replaceAll("</strong>", "</b>")+"  ";
        holder.contentDate.setText(Html.fromHtml(text));


    }


    public void removeItem(int position) {
        activityModels.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return activityModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView dateText, contentDate;


        public ViewHolder(View view) {
            super(view);

            dateText = view.findViewById(R.id.text_date);
            contentDate = view.findViewById(R.id.text_content);
        }
    }

}

