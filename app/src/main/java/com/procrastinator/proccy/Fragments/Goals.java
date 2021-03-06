package com.procrastinator.proccy.Fragments;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.slider.Slider;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.procrastinator.proccy.DataHolder;
import com.procrastinator.proccy.PreferencesConfig;
import com.procrastinator.proccy.R;
import com.procrastinator.proccy.Receiver;
import com.procrastinator.proccy.TimerService;
import com.procrastinator.proccy.Utilities;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.procrastinator.proccy.Receiver.SEND_ON_FINISH;
import static com.procrastinator.proccy.Receiver.UPDATE_BUTTONS;
import static com.procrastinator.proccy.Receiver.UPDATE_COUNTDOWN_TEXT;
import static com.procrastinator.proccy.TimerService.TIMER_RUNNING;
import static com.procrastinator.proccy.TimerService.TIME_LEFT_IN_MILLIS;

public class Goals extends Fragment {

    public static final String TAG = "GoalsFragment";
    private static final String ARG_PARAM1 = "daily score";
    private static final String ARG_PARAM2 = "total score";
    //TIMER VARIABLEN
    //private long START_TIME_IN_MILLIS = 300000;
    private long START_TIME_IN_MILLIS = 10000; //TEST VALUE
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean hasFinished;
    //TIMER TEXTVIEW UND BUTTONS
    private TextView mTextViewCountDown;
    private Button mButtonStartPause, mButtonReset, mButtonRefresh;
    private Slider mSlider;
    private MaterialCheckBox mCheckbox1, mCheckbox2, mCheckbox3, mCheckbox4;
    private int mScoreDaily;
    private int mScoreTotal;
    private int mScoreTemporary;
    public Intent mServiceIntent;
    public static LottieAnimationView animationView;

    public Goals() {
        // Required empty public constructor
    }

    public static Goals newInstance(int param1, String param2) {
        Goals fragment = new Goals();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        //if (getArguments() != null) {
        //    mScoreTotal = getArguments().getInt(ARG_PARAM1);
        //    displayName = getArguments().getString(ARG_PARAM2);
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mServiceIntent = new Intent(requireActivity(), TimerService.class);

        mTextViewCountDown = getView().findViewById(R.id.text_view_timer);
        mButtonStartPause = getView().findViewById(R.id.button_start);
        mButtonReset = getView().findViewById(R.id.button_reset);
        mButtonRefresh = getView().findViewById(R.id.btn_refresh);
        mCheckbox1 = getView().findViewById(R.id.checkBox);
        mCheckbox2 = getView().findViewById(R.id.checkBox2);
        mCheckbox3 = getView().findViewById(R.id.checkBox3);
        mCheckbox4 = getView().findViewById(R.id.checkBox4);
        mSlider = getView().findViewById(R.id.seekBar_timer);

        mButtonRefresh.setOnClickListener(v -> changeCheckBox());
        mButtonStartPause.setOnClickListener(v -> startTimer());
        mButtonReset.setOnClickListener(v -> resetTimer());
        mSlider.addOnChangeListener((slider, value, fromUser) -> {
            START_TIME_IN_MILLIS = (int) value * 60000;
            mTimeLeftInMillis = START_TIME_IN_MILLIS;
            updateCountDownText();
        });

    }

    private Receiver updateReceiver = new Receiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (UPDATE_COUNTDOWN_TEXT.equals(intent.getAction())) {
                mTimeLeftInMillis = intent.getLongExtra(TIME_LEFT_IN_MILLIS, 60000);
                updateCountDownText();
                updateButtons();
            } else if (UPDATE_BUTTONS.equals(intent.getAction())) {
                mTimerRunning = intent.getBooleanExtra(TIMER_RUNNING, false);
            } else if (SEND_ON_FINISH.equals(intent.getAction())) {
                hasFinished = PreferencesConfig.loadTimerHasFinished(requireActivity());
                if (hasFinished) {
                    playAnimation();
                    updateScore();
                    PreferencesConfig.saveTimerRunning(requireActivity(), false);
                    PreferencesConfig.saveTimerHasFinished(requireActivity(), false);
                }
            }
        }
    };

    private void startTimer() {

        int mScoreQuestionEnvironment1 = 5;
        int mScoreQuestionEnvironment2 = 5;
        int mScoreQuestionTask1 = 10;
        int mScoreQuestionTask2 = 10;

        if (START_TIME_IN_MILLIS >= 3000) {

            mScoreTemporary = 10;
            if (mCheckbox1.isChecked()) {
                mScoreTemporary += mScoreQuestionEnvironment1;
            }
            if (mCheckbox2.isChecked()) {
                mScoreTemporary += mScoreQuestionEnvironment2;
            }
            if (mCheckbox3.isChecked()) {
                mScoreTemporary += mScoreQuestionTask1;
            }
            if (mCheckbox4.isChecked()) {
                mScoreTemporary += mScoreQuestionTask2;
            }
            PreferencesConfig.saveTempScore(requireActivity(), mScoreTemporary);
            startService();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(updateReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_BUTTONS);
        intentFilter.addAction(UPDATE_COUNTDOWN_TEXT);
        intentFilter.addAction(SEND_ON_FINISH);
        requireActivity().registerReceiver(updateReceiver, intentFilter);

        mTimerRunning = PreferencesConfig.loadTimerRunning(requireActivity());
        hasFinished = PreferencesConfig.loadTimerHasFinished(requireActivity());

        if(mTimerRunning){
            mTimeLeftInMillis = PreferencesConfig.loadMilliesLeft(requireActivity());
        }

        if (hasFinished) {
            playAnimation();
            updateScore();
            PreferencesConfig.saveTimerRunning(requireActivity(), false);
            PreferencesConfig.saveTimerHasFinished(requireActivity(), false);
        }
        updateCountDownText();
        updateButtons();
    }


    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void playAnimation() {
        animationView = getView().findViewById(R.id.animationView_confetti);
        animationView.setVisibility(View.VISIBLE);
        animationView.setRepeatCount(0);
        animationView.playAnimation();
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationView.setVisibility(View.INVISIBLE);
                animationView.setProgress(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public static void stopAnimation() {
        animationView.setVisibility(View.INVISIBLE);
        animationView.setProgress(0);
    }

    private void updateScore() {
        mScoreTotal = DataHolder.getInstance().getTotalScore();
        mScoreDaily = Utilities.getCurrentDayDailyScore();
        mScoreTemporary = PreferencesConfig.loadTempScore(requireActivity());

        mScoreDaily += mScoreTemporary;
        mScoreTotal += mScoreTemporary;

        Log.d(TAG, "updateScore: mScoreTemporary = " + mScoreTemporary);
        Log.d(TAG, "updateScore: mScoreTotal =  " + mScoreTotal);
        Log.d(TAG, "updateScore: mScoreDaily = " + mScoreDaily);

        PreferencesConfig.removeTempScore(requireActivity());
        DataHolder.getInstance().setTotalScore(mScoreTotal);
        //DataHolder.getInstance().setDailyScore(mScoreDaily);
        writeUserData(mScoreTotal);
    }

    //RESETTET DEN TIMER
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        PreferencesConfig.saveMilliesLeft(requireActivity(), mTimeLeftInMillis);
        PreferencesConfig.saveTimerRunning(requireActivity(), false);
        PreferencesConfig.saveTimerHasFinished(requireActivity(), false);
        updateCountDownText();
        updateButtons();
        stopService();
    }

    private void updateButtons() {
        mTimerRunning = PreferencesConfig.loadTimerRunning(getActivity());

        if (mTimerRunning) {
            mCheckbox1.setEnabled(false);
            mCheckbox2.setEnabled(false);
            mCheckbox3.setEnabled(false);
            mCheckbox4.setEnabled(false);
            mButtonRefresh.setVisibility(View.INVISIBLE);
            mSlider.setVisibility(View.INVISIBLE);
            mButtonStartPause.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.VISIBLE);

        } else {
            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mCheckbox1.setEnabled(true);
                mCheckbox2.setEnabled(true);
                mCheckbox3.setEnabled(true);
                mCheckbox4.setEnabled(true);
                mSlider.setVisibility(View.VISIBLE);
                mButtonStartPause.setVisibility(View.VISIBLE);
                mButtonRefresh.setVisibility(View.VISIBLE);

            }
            if (mTimeLeftInMillis < START_TIME_IN_MILLIS) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void writeUserData(int totalscore) {
        FirebaseAuth mAuth;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("totalscore", totalscore);
        db.collection("users").document(uid).set(userMap, SetOptions.merge());

        Date currentDate = Utilities.getDateWithoutTimeUsingCalendar();
        String currentDayWithoutTime = getLocalDate().toString();

        Map<String, Object> newDailyScore = new HashMap<>();
        newDailyScore.put("score", mScoreDaily);
        newDailyScore.put("date", new Timestamp(currentDate));

        DocumentReference addedDocRef = db.collection("users").document(uid);
        addedDocRef.collection("dailyScoreHistory").document(currentDayWithoutTime).set(newDailyScore, SetOptions.merge());

        //Write into DataHolder class
        HashMap<CalendarDay, Integer> temporaryHashmap = DataHolder.getInstance().getDailyScoreHashMap();
        temporaryHashmap.put(Utilities.getCurrentCalendarDay(), mScoreDaily);
        DataHolder.getInstance().setDailyScoreHashMap(temporaryHashmap);
    }

    public static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    public void changeCheckBox() {

        String[] questionsEnvironment = getResources().getStringArray(R.array.questions_environment);
        String[] questionsTasks = getResources().getStringArray(R.array.questions_tasks);
        List<String> questionsEnvironmentList = Arrays.asList(questionsEnvironment);
        List<String> questionsTaskList = Arrays.asList(questionsTasks);
        Collections.shuffle(questionsEnvironmentList);
        Collections.shuffle(questionsTaskList);

        mCheckbox1.setText(questionsEnvironmentList.get(0));
        mCheckbox2.setText(questionsEnvironmentList.get(1));
        mCheckbox3.setText(questionsTaskList.get(0));
        mCheckbox4.setText(questionsTaskList.get(1));

    }

    public void startService() {
        Intent serviceIntent = new Intent(requireActivity(), TimerService.class);
        serviceIntent.putExtra("inputExtra", mTimeLeftInMillis);
        requireActivity().startService(serviceIntent);
    }

    public void stopService() {
        requireActivity().stopService(new Intent(getActivity(), TimerService.class));
    }
}