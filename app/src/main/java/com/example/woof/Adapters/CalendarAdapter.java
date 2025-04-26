package com.example.woof.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.woof.Model.DayEvent;
import com.example.woof.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private OnItemListener onItemListener;
    private final Map<LocalDate, DayEvent> dayEvents;
    private LocalDate selectedMonth;

    public interface OnItemListener {
        void onItemClick(int position, String dayText, LocalDate cellDate);
    }

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener, Map<LocalDate, DayEvent> dayEvents, LocalDate selectedMonth) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.dayEvents = dayEvents;
        this.selectedMonth = selectedMonth;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String dayText = daysOfMonth.get(position);
        holder.cellDayText.setText(daysOfMonth.get(position));

        if(!dayText.equals("")){
            int day = Integer.parseInt(dayText);
            LocalDate cellDate = selectedMonth.withDayOfMonth(day);
            DayEvent event = dayEvents.get(cellDate);
            holder.itemView.setOnClickListener(v -> onItemListener.onItemClick(position, dayText,cellDate));
            if(event != null){
                holder.CC_SIV_Medicine.setVisibility(event.hasMedicine() ? View.VISIBLE : View.GONE);
                holder.CC_SIV_Vaccine.setVisibility(event.hasVaccine() ? View.VISIBLE : View.GONE);
            } else{
                holder.CC_SIV_Medicine.setVisibility(View.GONE);
                holder.CC_SIV_Vaccine.setVisibility(View.GONE);
            }
        } else{
            holder.CC_SIV_Medicine.setVisibility(View.GONE);
            holder.CC_SIV_Vaccine.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        private TextView cellDayText;
        private ShapeableImageView CC_SIV_Medicine;
        private ShapeableImageView CC_SIV_Vaccine;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            cellDayText = itemView.findViewById(R.id.cellDayText);
            CC_SIV_Medicine = itemView.findViewById(R.id.CC_SIV_Medicine);
            CC_SIV_Vaccine = itemView.findViewById(R.id.CC_SIV_Vaccine);


        }
    }
}
