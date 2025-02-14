package com.example.fithit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fithit.Models.Metric;
import com.example.fithit.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MetricsAdapter extends RecyclerView.Adapter<MetricsAdapter.MetricViewHolder> {

    private List<Metric> metricsList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public MetricsAdapter(List<Metric> metricsList) {
        this.metricsList = metricsList;
    }

    @NonNull
    @Override
    public MetricViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_metric, parent, false);
        return new MetricViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MetricViewHolder holder, int position) {
        Metric metric = metricsList.get(position);
        holder.bind(metric);
    }

    @Override
    public int getItemCount() {
        return metricsList.size();
    }

    public void updateMetrics(List<Metric> newMetrics) {
        this.metricsList = newMetrics;
        notifyDataSetChanged();
    }

    static class MetricViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMetricType;
        private final TextView tvMetricValue;
        private final TextView tvMetricDate;
        private final TextView tvMetricNotes;

        public MetricViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMetricType = itemView.findViewById(R.id.tv_metric_type);
            tvMetricValue = itemView.findViewById(R.id.tv_metric_value);
            tvMetricDate = itemView.findViewById(R.id.tv_metric_date);
            tvMetricNotes = itemView.findViewById(R.id.tv_metric_notes);
        }

        public void bind(Metric metric) {
            tvMetricType.setText(metric.getType().toString());
            tvMetricValue.setText(String.valueOf(metric.getValue()));
            tvMetricDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(metric.getMeasurementDate()));
            tvMetricNotes.setText(metric.getNotes());
        }
    }
}