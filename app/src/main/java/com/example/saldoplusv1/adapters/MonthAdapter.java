package com.example.saldoplusv1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saldoplusv1.R;

import java.text.DateFormatSymbols;
import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {

    public interface OnMonthClick {
        void onClick(int year, int month);
    }

    private final List<YearMonth> months;
    private final OnMonthClick listener;

    public MonthAdapter(List<YearMonth> months, OnMonthClick listener) {
        this.months = months;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        public ViewHolder(View v) {
            super(v);
            label = v.findViewById(R.id.tvMonthLabel);
        }
    }

    @NonNull
    @Override
    public MonthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_month, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthAdapter.ViewHolder holder, int position) {
        YearMonth ym = months.get(position);
        String text = new DateFormatSymbols()
                .getMonths()[ym.month - 1] + " " + ym.year;
        holder.label.setText(text);
        holder.itemView.setOnClickListener(v ->
                listener.onClick(ym.year, ym.month)
        );
    }

    @Override
    public int getItemCount() {
        return months.size();
    }

    public static class YearMonth {
        public final int year, month;
        public YearMonth(int y, int m) { year = y; month = m; }
    }
}
