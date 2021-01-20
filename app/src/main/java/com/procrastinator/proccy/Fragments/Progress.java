package com.procrastinator.proccy.Fragments;

import android.content.SharedPreferences;
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

import com.procrastinator.proccy.PreferencesConfig;
import com.procrastinator.proccy.R;

public class Progress extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int scoreDaily;
    private ProgressBar progress;
    private TextView progressText;
    private Button button_resetProgress;

    public Progress() {
        // Required empty public constructor
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
        button_resetProgress = getView().findViewById(R.id.resetButton);
        button_resetProgress.setOnClickListener(v -> resetDailyScore());
        updateProgressBar();

        super.onViewCreated(view, savedInstanceState);
    }


    public void updateProgressBar() {
        scoreDaily = PreferencesConfig.loadDailyScore(requireActivity());
        progressText.setText(scoreDaily + " %");
        progress.setProgress(scoreDaily);
    }

    public void resetDailyScore() {
        PreferencesConfig.removeDailyScoreFromPref(requireActivity());
        updateProgressBar();
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesConfig.PREF_DAILY_SCORE)) {
            updateProgressBar();
        }
    }
}