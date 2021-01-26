package com.procrastinator.proccy.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.procrastinator.proccy.CurrentDayDecorator;
import com.procrastinator.proccy.DataHolder;
import com.procrastinator.proccy.EventDecorator;
import com.procrastinator.proccy.PreferencesConfig;
import com.procrastinator.proccy.R;
import com.procrastinator.proccy.Utilities;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.List;

public class Progress extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String ARG_PARAM1 = "daily score";
    private static final String ARG_PARAM2 = "total score";
    private static final String TAG = "ProgressFragment";
    private int scoreDaily, scoreTotal;
    private ProgressBar progress;
    private TextView progressText, scoreDailyTextView, scoreTotalTextView;
    private Button button_resetProgress, button_fireStore;
    private MaterialCalendarView calendarView;

    public Progress() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        //    mParam1 = getArguments().getInt(ARG_PARAM1);
        //    mParam2 = getArguments().getString(ARG_PARAM2);
        //}
    }

    public static Progress newInstance(int param1, String param2) {
        Progress fragment = new Progress();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        progress = getView().findViewById(R.id.progress_bar);
        progressText = getView().findViewById(R.id.text_view_progress);
        scoreDailyTextView = getView().findViewById(R.id.dailyScoreDisplay_progress);
        scoreTotalTextView = getView().findViewById(R.id.totalScoreDisplay_progress);
        button_resetProgress = getView().findViewById(R.id.resetButton);
        button_resetProgress.setOnClickListener(v -> resetDailyScore());
        button_fireStore = getView().findViewById(R.id.setDotSpans);
        button_fireStore.setOnClickListener(v -> addDecorator(DataHolder.getInstance().getCalendarDays()));
        calendarView = getView().findViewById(R.id.calendarView);
        calendarView.addDecorators(new CurrentDayDecorator(requireActivity()));

        updateProgressBar();
        super.onViewCreated(view, savedInstanceState);
    }

    public void updateProgressBar() {
        scoreDaily = Utilities.getCurrentDayDailyScore();
        scoreTotal =  DataHolder.getInstance().getTotalScore();

        scoreDailyTextView.setText(getResources().getString(R.string.textview_score_daily, scoreDaily));
        scoreTotalTextView.setText(getResources().getString(R.string.textview_score_total,scoreTotal));

        progressText.setText(getResources().getString(R.string.progress_fragment_progresscircle_text,scoreDaily));
        progress.setProgress(scoreDaily);
    }

    public void resetDailyScore() {
        scoreDaily = Utilities.getCurrentDayDailyScore();
        updateProgressBar();
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesConfig.PREF_DAILY_SCORE)) {
            updateProgressBar();
        }
    }

    private void addDecorator(List<CalendarDay> calendarDays) {
        EventDecorator dailyScoreDecorator = new EventDecorator(Color.RED, calendarDays);
        calendarView.addDecorator(dailyScoreDecorator);
        calendarView.invalidateDecorators();
    }

}