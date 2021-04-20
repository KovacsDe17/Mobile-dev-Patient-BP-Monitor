package com.example.bloodpressuremonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MeasureItemAdapter extends RecyclerView.Adapter<MeasureItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<MeasureItem> mMeasureItemsData;
    private ArrayList<MeasureItem> mMeasureItemsDataAll;
    private Context mContext;
    private int lastPosition = -1;

    public MeasureItemAdapter(Context context, ArrayList<MeasureItem> itemsData) {
        this.mMeasureItemsData = itemsData;
        this.mMeasureItemsDataAll = itemsData;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MeasureItemAdapter.ViewHolder holder, int position) {
        MeasureItem currentItem = mMeasureItemsData.get(position);

        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_anim);
            holder.itemView.startAnimation(animation);

            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mMeasureItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return measureFilter;
    }

    private Filter measureFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<MeasureItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mMeasureItemsDataAll.size();
                results.values = mMeasureItemsDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(MeasureItem item : mMeasureItemsDataAll) {
                    if(item.getDate().toString().concat(" " + item.getTimeOfDay()).toLowerCase().contains(filterPattern))
                        filteredList.add(item);
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            mMeasureItemsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView timeOfDay;
        private TextView systolic;
        private TextView diastolic;
        private TextView pulse;

        public ViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.itemDateText);
            timeOfDay = itemView.findViewById(R.id.timeOfDayText);
            systolic = itemView.findViewById(R.id.sysText);
            diastolic = itemView.findViewById(R.id.diaText);
            pulse = itemView.findViewById(R.id.pulseText);

            itemView.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
        }

        public void bindTo(MeasureItem currentItem) {
            date.setText(currentItem.getDate().toString());
            timeOfDay.setText(currentItem.getTimeOfDay());
            systolic.setText(Integer.toString(currentItem.getSystolic()));
            diastolic.setText(Integer.toString(currentItem.getDiastolic()));
            pulse.setText(Integer.toString(currentItem.getPulse()));
        }
    }
}
