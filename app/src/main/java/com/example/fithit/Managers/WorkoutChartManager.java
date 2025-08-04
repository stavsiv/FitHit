package com.example.fithit.Managers;

import android.content.Context;
import com.example.fithit.Models.WorkoutRecord;
import com.example.fithit.R;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WorkoutChartManager {

    private final LineChart lineChart;
    private final CombinedChart combinedChart;
    private final Context context;
    private OnChartAnnouncementListener announcementListener;
    private final boolean isLineChart;

    public interface OnChartAnnouncementListener {
        void onAnnouncement(String announcement);
    }

    public WorkoutChartManager(LineChart chart, Context context) {
        this.lineChart = chart;
        this.combinedChart = null;
        this.context = context;
        this.isLineChart = true;
        setupLineChart();
    }

    public WorkoutChartManager(CombinedChart chart, Context context) {
        this.lineChart = null;
        this.combinedChart = chart;
        this.context = context;
        this.isLineChart = false;
        setupCombinedChart();
    }

    public void setOnChartAnnouncementListener(OnChartAnnouncementListener listener) {
        this.announcementListener = listener;
    }
    private void setupLineChart() {
        if (lineChart == null) return;

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
        lineChart.setNoDataText(context.getString(R.string.no_workouts_history));
        lineChart.setNoDataTextColor(context.getResources().getColor(R.color.colorSecondary));

        lineChart.setContentDescription(context.getString(R.string.workout_progress_chart_showing_history_of_completed_workouts));

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (announcementListener != null) {
                    int workoutNumber = (int)e.getX() + 1;
                    int totalCompleted = (int)e.getY();
                    String announcement = context.getString(R.string.workout) + " " + workoutNumber + ": " +
                            context.getString(R.string.total) + " " + totalCompleted + " " +
                            context.getString(R.string.workouts_completed);
                    announcementListener.onAnnouncement(announcement);
                }
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    private void setupCombinedChart() {
        if (combinedChart == null) return;

        combinedChart.setTouchEnabled(true);
        combinedChart.setDragEnabled(true);
        combinedChart.setScaleEnabled(true);
        combinedChart.setPinchZoom(true);
        combinedChart.getAxisLeft().setDrawGridLines(false);
        combinedChart.getAxisRight().setEnabled(true);
        combinedChart.getAxisRight().setDrawGridLines(false);
        combinedChart.getLegend().setEnabled(true);
        combinedChart.setNoDataText(context.getString(R.string.no_workouts_history));
        combinedChart.setNoDataTextColor(context.getResources().getColor(R.color.colorSecondary));
        combinedChart.setDrawBorders(false);
        combinedChart.setExtraBottomOffset(50f);
        combinedChart.setExtraTopOffset(20f);
        combinedChart.setExtraLeftOffset(15f);
        combinedChart.setExtraRightOffset(15f);

        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,
                CombinedChart.DrawOrder.LINE
        });

        combinedChart.setContentDescription(context.getString(R.string.workout_progress_chart_showing_history_of_completed_workouts));
    }
    public void updateChart(List<WorkoutRecord> history) {
        if (isLineChart) {
            updateLineChart(history);
        } else {
            updateCombinedChart(history);
        }
    }

    private void updateLineChart(List<WorkoutRecord> history) {
        if (lineChart == null || history == null || history.isEmpty()) {
            if (lineChart != null) {
                lineChart.setNoDataText(context.getString(R.string.no_workouts_history));
                lineChart.invalidate();
            }
            return;
        }

        Collections.sort(history, (w1, w2) -> Long.compare(w1.getDate(), w2.getDate()));

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            entries.add(new Entry(i, i+1));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Workouts Progress");
        dataSet.setDrawFilled(true);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(context.getResources().getColor(R.color.colorPrimary));
        dataSet.setFillColor(context.getResources().getColor(R.color.colorPrimaryLight));
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(true);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(context.getResources().getColor(R.color.colorAccent));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setEnabled(true);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }
    private void updateCombinedChart(List<WorkoutRecord> history) {
        if (combinedChart == null || history == null || history.isEmpty()) {
            if (combinedChart != null) {
                combinedChart.setNoDataText(context.getString(R.string.no_workouts_history));
                combinedChart.invalidate();
            }
            return;
        }

        Collections.sort(history, (w1, w2) -> Long.compare(w1.getDate(), w2.getDate()));

        Map<String, Integer> workoutsByMonth = new HashMap<>();
        Map<String, Integer> cumulativeWorkoutsByMonth = new HashMap<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

        int totalWorkouts = 0;
        for (WorkoutRecord record : history) {
            Date date = new Date(record.getDate());
            String monthYear = monthFormat.format(date);
            workoutsByMonth.put(monthYear, workoutsByMonth.getOrDefault(monthYear, 0) + 1);
            totalWorkouts++;
            cumulativeWorkoutsByMonth.put(monthYear, totalWorkouts);
        }
        List<String> sortedMonths = new ArrayList<>(workoutsByMonth.keySet());
        Collections.sort(sortedMonths, (m1, m2) -> {
            try {
                Date date1 = monthFormat.parse(m1);
                Date date2 = monthFormat.parse(m2);
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });

        ArrayList<BarEntry> monthlyEntries = new ArrayList<>();
        ArrayList<Entry> cumulativeEntries = new ArrayList<>();

        for (int i = 0; i < sortedMonths.size(); i++) {
            String month = sortedMonths.get(i);
            int monthlyCount = workoutsByMonth.get(month);
            int cumulativeCount = cumulativeWorkoutsByMonth.get(month);

            monthlyEntries.add(new BarEntry(i, monthlyCount));
            cumulativeEntries.add(new Entry(i, cumulativeCount));
        }

        BarDataSet barDataSet = new BarDataSet(monthlyEntries, context.getString(R.string.workouts_per_month));
// שינוי צבע העמודות לשחור/כהה יותר
        barDataSet.setColor(context.getResources().getColor(android.R.color.black)); // שינוי לשחור
        barDataSet.setValueTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        barDataSet.setValueTextSize(12f);
        barDataSet.setHighLightAlpha(255);
        barDataSet.setDrawValues(true);
// הוספת מסגרת לעמודות
        barDataSet.setBarBorderWidth(1.0f); // מסגרת עבה יותר
        barDataSet.setBarBorderColor(context.getResources().getColor(R.color.colorPrimaryDark));
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value > 0 ? String.valueOf((int) value) : "";
            }
        });

        LineDataSet lineDataSet = new LineDataSet(cumulativeEntries, context.getString(R.string.total_workouts));
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet.setColor(context.getResources().getColor(R.color.colorPrimary)); // שינוי הצבע לניגודיות עם העמודות
        lineDataSet.setCircleColor(context.getResources().getColor(R.color.colorPrimaryDark));
        lineDataSet.setLineWidth(2.5f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawValues(true);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setValueTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        CombinedData combinedData = new CombinedData();
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f);

        LineData lineData = new LineData(lineDataSet);

        combinedData.setData(barData);
        combinedData.setData(lineData);

        combinedChart.setData(combinedData);

        final List<String> finalSortedMonths = sortedMonths;
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < finalSortedMonths.size()) {
                    return finalSortedMonths.get(index);
                }
                return "";
            }
        });
        xAxis.setLabelRotationAngle(90);
        xAxis.setLabelCount(sortedMonths.size());
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextSize(10f);
        xAxis.setYOffset(15f);

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        leftAxis.setTextSize(10f);
        leftAxis.setDrawLabels(false);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setEnabled(true);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setGranularity(1f);
        rightAxis.setTextColor(context.getResources().getColor(R.color.colorAccent));
        rightAxis.setTextSize(10f);

        Legend legend = combinedChart.getLegend();

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        legend.setFormSize(10f);
        legend.setTextSize(12f);
        legend.setXEntrySpace(20f);
        legend.setYOffset(5f);
        legend.setFormToTextSpace(8f);
        legend.setDrawInside(false);
        legend.setTextColor(context.getResources().getColor(R.color.colorAccent));
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setWordWrapEnabled(false);

        barDataSet.setLabel("Workouts Per Month");
        lineDataSet.setLabel("Total Workouts");

// Update colors
        barDataSet.setColor(context.getResources().getColor(android.R.color.black));
        lineDataSet.setColor(context.getResources().getColor(R.color.colorAccent));

        combinedChart.setDrawBorders(true);
        combinedChart.setBorderColor(context.getResources().getColor(android.R.color.white));
        combinedChart.setBorderWidth(2f);

        final List<String> monthsList = sortedMonths;
        combinedChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (announcementListener == null) return;

                int index = (int) e.getX();
                if (index >= 0 && index < monthsList.size()) {
                    String month = monthsList.get(index);

                    if (h.getDataSetIndex() == 0) {
                        int monthlyCount = (int) e.getY();
                        String announcement = month + ": " + monthlyCount + " " +
                                context.getString(R.string.workouts_in_month);
                        announcementListener.onAnnouncement(announcement);
                    } else if (h.getDataSetIndex() == 1) {
                        int totalCount = (int) e.getY();
                        String announcement = month + ": " +
                                context.getString(R.string.total) + " " + totalCount + " " +
                                context.getString(R.string.workouts_completed);
                        announcementListener.onAnnouncement(announcement);
                    }
                }
            }

            @Override
            public void onNothingSelected() {
            }
        });

        combinedChart.animateXY(1500, 1500);
        combinedChart.invalidate();
    }
}