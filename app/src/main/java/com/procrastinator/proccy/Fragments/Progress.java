package com.procrastinator.proccy.Fragments;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.procrastinator.proccy.CurrentDayDecorator;
import com.procrastinator.proccy.DataHolder;
import com.procrastinator.proccy.EventDecorator;
import com.procrastinator.proccy.R;
import com.procrastinator.proccy.Utilities;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.DateFormat;
import java.util.List;

public class Progress extends Fragment {

    private static final String ARG_PARAM1 = "daily score";
    private static final String ARG_PARAM2 = "total score";
    private static final String TAG = "ProgressFragment";
    private int scoreDaily, scoreTotal;
    private ProgressBar progress;
    private TextView progressTextView, scoreDailyTextView, scoreTotalTextView, displayDayTextView;
    private MaterialCalendarView calendarView;

    public Progress() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        super.onViewCreated(view, savedInstanceState);

        progress = getView().findViewById(R.id.progress_bar);
        progressTextView = getView().findViewById(R.id.text_view_progress);
        displayDayTextView = getView().findViewById(R.id.textview_displayday);
        scoreDailyTextView = getView().findViewById(R.id.dailyScoreDisplay_progress);
        scoreTotalTextView = getView().findViewById(R.id.totalScoreDisplay_progress);

        calendarView = getView().findViewById(R.id.calendarView);
        calendarView.addDecorators(new CurrentDayDecorator(requireActivity()));

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            setSelectedDailyScore(date);
        });

        if(DataHolder.getInstance().getCalendarDays()!=null){
            addDecorator(DataHolder.getInstance().getCalendarDays());
        }

        setCurrentDailyAndTotalScore();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void setCurrentDailyAndTotalScore(){
        scoreTotal =  DataHolder.getInstance().getTotalScore();
        scoreDaily = Utilities.getCurrentDayDailyScore();
        scoreTotalTextView.setText(getResources().getString(R.string.textview_score_total,scoreTotal));
        scoreDailyTextView.setText(getResources().getString(R.string.textview_score_daily, scoreDaily));
        progressTextView.setText(getResources().getString(R.string.progress_fragment_progresscircle_text,scoreDaily));
        progress.setProgress(scoreDaily, true);
    }

    private void setSelectedDailyScore(CalendarDay date){
        int score = Utilities.getSelectedDayDailyScore(date);
        progressTextView.setText(getResources().getString(R.string.progress_fragment_progresscircle_text, score));
        progress.setProgress(score, true);
        if (date.equals(CalendarDay.today())){
            displayDayTextView.setText(R.string.progress_fragment_display_date);
        }else {
            //Formats CalendarDay into Date then Formats into Locale DateFormat.
            displayDayTextView.setText(DateFormat.getDateInstance().format(date.getDate()));
        }
    }

    private void addDecorator(List<CalendarDay> calendarDays) {
        EventDecorator dailyScoreDecorator = new EventDecorator(Color.RED, calendarDays);
        calendarView.addDecorator(dailyScoreDecorator);
        calendarView.invalidateDecorators();
    }

}