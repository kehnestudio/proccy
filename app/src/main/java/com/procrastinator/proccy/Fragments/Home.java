package com.procrastinator.proccy.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.procrastinator.proccy.PreferencesConfig;
import com.procrastinator.proccy.R;

public class Home extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView scoreDailyTextView, scoreTotalTextView;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        scoreDailyTextView = getView().findViewById(R.id.dailyScoreDisplay);
        scoreTotalTextView = getView().findViewById(R.id.totalScoreDisplay);
        updateDailyAndTotalScore();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesConfig.PREF_DAILY_SCORE)) {
            updateDailyAndTotalScore();
        }
    }

    public void updateDailyAndTotalScore() {
            scoreDailyTextView.setText(getResources().getString(R.string.textview_score_daily) + PreferencesConfig.loadDailyScore(requireActivity()));
            scoreTotalTextView.setText(getResources().getString(R.string.textview_score_total) + PreferencesConfig.loadTotalScore(requireActivity()));
    }

}