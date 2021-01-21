package com.procrastinator.proccy.Fragments;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.procrastinator.proccy.PreferencesConfig;
import com.procrastinator.proccy.R;
import com.procrastinator.proccy.Receiver;
import com.procrastinator.proccy.TimerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.procrastinator.proccy.Receiver.SEND_ON_FINISH;
import static com.procrastinator.proccy.Receiver.UPDATE_BUTTONS;
import static com.procrastinator.proccy.Receiver.UPDATE_COUNTDOWN_TEXT;
import static com.procrastinator.proccy.TimerService.TIMER_RUNNING;
import static com.procrastinator.proccy.TimerService.TIME_LEFT_IN_MILLIS;

public class Goals extends Fragment{

    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    //TIMER VARIABLEN
    //private long START_TIME_IN_MILLIS = 300000;
    private long START_TIME_IN_MILLIS = 10000; //TEST VALUE
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    //TIMER TEXTVIEW UND BUTTONS
    private TextView mTextViewCountDown;
    private Button mButtonStartPause, mButtonReset, mButtonRefresh;
    private SeekBar seekbar_timer;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
    private int scoreDaily;
    private int scoreTotal;
    private int scoreTemp;
    int score1 = 5;
    int score2 = 5;
    int score3 = 5;
    int score4 = 5;
    public Intent serviceIntent;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Goals() {
        // Required empty public constructor
    }

    public static Goals newInstance(String param1, String param2) {
        Goals fragment = new Goals();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        serviceIntent = new Intent(requireActivity(), TimerService.class);

        mTextViewCountDown = getView().findViewById(R.id.text_view_timer);
        mButtonStartPause = getView().findViewById(R.id.button_start);
        mButtonReset = getView().findViewById(R.id.button_reset);
        mButtonRefresh = getView().findViewById(R.id.btn_refresh);

        checkBox1 = getView().findViewById(R.id.checkBox);
        checkBox2 = getView().findViewById(R.id.checkBox2);
        checkBox3 = getView().findViewById(R.id.checkBox3);
        checkBox4 = getView().findViewById(R.id.checkBox4);

        seekbar_timer = getView().findViewById(R.id.seekBar_timer);

        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCheckBox();
            }
        });

        mButtonStartPause.setOnClickListener(v -> {
            if (START_TIME_IN_MILLIS >= 3000) {
                startService();
            }
        });

        mButtonReset.setOnClickListener(v -> resetTimer());

        seekbar_timer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                START_TIME_IN_MILLIS = progress * 60000;
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                updateCountDownText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
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
                Log.d("TAG", "onReceive: stopping service");
                boolean hasFinished = PreferencesConfig.loadTimerHasFinished(requireActivity());
                updateScore();
                if (hasFinished){
                    playAnimation();
                    PreferencesConfig.saveTimerHasFinished(requireActivity(), false);
                }
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(updateReceiver);
        PreferencesConfig.saveTimerRunning(requireActivity(), mTimerRunning);
        PreferencesConfig.saveMilliesLeft(requireActivity(), mTimeLeftInMillis);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_BUTTONS); // Action1 to filter
        intentFilter.addAction(UPDATE_COUNTDOWN_TEXT);
        intentFilter.addAction(SEND_ON_FINISH);// Action2 to filter
        requireActivity().registerReceiver(updateReceiver, intentFilter);
        mTimerRunning = PreferencesConfig.loadTimerRunning(requireActivity());
        mTimeLeftInMillis = PreferencesConfig.loadMilliesLeft(requireActivity());
        boolean hasFinished = PreferencesConfig.loadTimerHasFinished(requireActivity());
        if (hasFinished){
            playAnimation();
            PreferencesConfig.saveTimerHasFinished(requireActivity(), false);
        }
        if(mTimerRunning){
            startService();
        }
        updateCountDownText();
        updateButtons();


        if (mTimeLeftInMillis < 0) {
            mTimeLeftInMillis = 0;
            mTimerRunning = false;
            PreferencesConfig.saveTimerRunning(requireActivity(), false);
            updateCountDownText();
            updateButtons();
        }
    }


    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void playAnimation() {

        LottieAnimationView animationView = getView().findViewById(R.id.animationView_confetti);
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

    //RESETTET DEN TIMER
    private void resetTimer() {
        mTimerRunning = false;
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        PreferencesConfig.saveTimerRunning(requireActivity(), mTimerRunning);
        PreferencesConfig.saveTimerHasFinished(requireActivity(), false);
        updateCountDownText();
        updateButtons();
        stopService();
    }

    private void updateScore() {

        scoreDaily = PreferencesConfig.loadDailyScore(requireActivity());
        scoreTotal = PreferencesConfig.loadTotalScore(requireActivity());

        scoreTemp = 10;
        if (checkBox1.isChecked()) {
            scoreTemp += score1;
        }
        if (checkBox2.isChecked()) {
            scoreTemp += score2;
        }
        if (checkBox3.isChecked()) {
            scoreTemp += score3;
        }
        if (checkBox4.isChecked()) {
            scoreTemp += score4;
        }
        scoreDaily += scoreTemp;
        scoreTotal += scoreTemp;

        PreferencesConfig.saveDailyScore(requireActivity(), scoreDaily);
        PreferencesConfig.saveTotalScore(requireActivity(), scoreTotal);
        writeUserData(scoreTotal);
    }

    private void updateButtons() {
        if (mTimerRunning) {
            checkBox1.setEnabled(false);
            checkBox2.setEnabled(false);
            checkBox3.setEnabled(false);
            checkBox4.setEnabled(false);
            mButtonRefresh.setVisibility(View.INVISIBLE);
            seekbar_timer.setVisibility(View.INVISIBLE);
            mButtonStartPause.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.VISIBLE);

        } else {
            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                checkBox1.setEnabled(true);
                checkBox2.setEnabled(true);
                checkBox3.setEnabled(true);
                checkBox4.setEnabled(true);
                seekbar_timer.setVisibility(View.VISIBLE);
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

    private void writeUserData(int totalscore){
            mAuth = FirebaseAuth.getInstance();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("totalscore", totalscore);
            db.collection("users").document(mAuth.getUid()).set(userMap, SetOptions.merge());
    }

    public void changeCheckBox() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions");
        mTimerRunning = PreferencesConfig.loadTimerRunning(requireActivity());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String languageCode = Locale.getDefault().getLanguage();
                long childrenCount = snapshot.getChildrenCount();
                int max = (int) childrenCount;

                final List<Integer> l = new ArrayList<>();
                for (int j = 0; j < max; j++) {
                    l.add(j);
                }

                Collections.shuffle(l);

                String count1 = Integer.toString(l.get(0));
                String count2 = Integer.toString(l.get(1));
                String count3 = Integer.toString(l.get(2));
                String count4 = Integer.toString(l.get(3));

                if (!mTimerRunning && snapshot.exists()) {

                    String check1 = snapshot.child(count1).child(languageCode).getValue(String.class);
                    String check2 = snapshot.child(count2).child(languageCode).getValue(String.class);
                    String check3 = snapshot.child(count3).child(languageCode).getValue(String.class);
                    String check4 = snapshot.child(count4).child(languageCode).getValue(String.class);

                    score1 = Integer.parseInt(String.valueOf(snapshot.child(count1).child("score").getValue()));
                    score2 = Integer.parseInt(String.valueOf(snapshot.child(count2).child("score").getValue()));
                    score3 = Integer.parseInt(String.valueOf(snapshot.child(count3).child("score").getValue()));
                    score4 = Integer.parseInt(String.valueOf(snapshot.child(count4).child("score").getValue()));

                    checkBox1.setText(check1);
                    checkBox2.setText(check2);
                    checkBox3.setText(check3);
                    checkBox4.setText(check4);

                } else {
                    Log.d("TAG", "onDataChange: doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void startService() {
        Intent serviceIntent = new Intent(requireActivity(), TimerService.class);
        serviceIntent.putExtra("inputExtra", mTimeLeftInMillis);
        requireActivity().startService(serviceIntent);
    }

    public void stopService() {
        requireActivity().stopService(new Intent(getActivity(),TimerService.class));
    }


}