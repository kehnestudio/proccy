package com.procrastinator.proccy.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.procrastinator.proccy.DataHolder;
import com.procrastinator.proccy.PreferencesConfig;
import com.procrastinator.proccy.R;
import com.procrastinator.proccy.SignInActivity;
import com.procrastinator.proccy.TimerService;
import com.procrastinator.proccy.Utilities;

public class Home extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String ARG_PARAM1 = "daily score";
    private static final String ARG_PARAM2 = "total score";
    private TextView scoreDailyTextView, scoreTotalTextView, displayNameTextView;
    private Button signOutButton;
    private int scoreDaily, scoreTotal;
    private String displayName;

    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance(int param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        //    scoreTotal = getArguments().getInt(ARG_PARAM1);
        //    displayName = getArguments().getString(ARG_PARAM2);
        //}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        displayNameTextView = getView().findViewById(R.id.textView_displayname);
        scoreDailyTextView = getView().findViewById(R.id.dailyScoreDisplay);
        scoreTotalTextView = getView().findViewById(R.id.totalScoreDisplay);
        signOutButton = getView().findViewById(R.id.button_sign_out);

        updateUI();

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    private void sendToLogin() {
        GoogleSignInClient mGoogleSignInClient ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(),
                new OnCompleteListener<Void>() {  //signout Google
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut(); //signout firebase
                        Intent setupIntent = new Intent(requireActivity(), SignInActivity.class);
                        PreferencesConfig.clearAllPreferences(getActivity());
                        requireActivity().stopService(new Intent(getActivity(), TimerService.class));
                        Toast.makeText(requireActivity(), "Logged Out", Toast.LENGTH_LONG).show();
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        requireActivity().finish();
                    }
                });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesConfig.PREF_DAILY_SCORE)) {
            updateUI();
        }
    }

    public void updateUI() {
        String dailyScoreText = getResources().getString(R.string.textview_score_daily);
        String totalScoreText = getResources().getString(R.string.textview_score_total);
        scoreDaily = Utilities.getCurrentDayDailyScore();
        scoreTotal = DataHolder.getInstance().getTotalScore();
        displayName = DataHolder.getInstance().getDisplayName();

        scoreTotalTextView.setText(totalScoreText + scoreTotal);
        scoreDailyTextView.setText(dailyScoreText + scoreDaily);
        displayNameTextView.setText("Willkommen, " + displayName);
    }
}