package com.procrastinator.proccy.Fragments;

import android.content.Context;
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

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.procrastinator.proccy.MainActivity;
import com.procrastinator.proccy.PreferencesConfig;
import com.procrastinator.proccy.R;
import com.procrastinator.proccy.SignInActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Home extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView scoreDailyTextView, scoreTotalTextView, displayNameTextView;
    private Button signOutButton;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private int scoreDaily, scoreTotal;

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
        displayNameTextView = getView().findViewById(R.id.textView_displayname);
        displayNameTextView.setText("Willkommen, " + PreferencesConfig.loadUserName(requireActivity()));
        scoreDailyTextView = getView().findViewById(R.id.dailyScoreDisplay);
        scoreTotalTextView = getView().findViewById(R.id.totalScoreDisplay);
        signOutButton = getView().findViewById(R.id.button_sign_out);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        updateDailyAndTotalScore();

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    private void sendToLogin() { //funtion
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
                        Toast.makeText(requireActivity(), "Logged Out", Toast.LENGTH_LONG).show(); //if u want to show some text
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        requireActivity().finish();
                    }
                });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesConfig.PREF_DAILY_SCORE)) {
            updateDailyAndTotalScore();
        }
    }

    public void updateDailyAndTotalScore() {
        String dailyScoreText = getResources().getString(R.string.textview_score_daily);
        String totalScoreText = getResources().getString(R.string.textview_score_total);
        scoreDaily = PreferencesConfig.loadDailyScore(requireActivity());
        scoreTotal = PreferencesConfig.loadTotalScore(requireActivity());

        scoreDailyTextView.setText(dailyScoreText + scoreDaily);
        scoreTotalTextView.setText(totalScoreText + scoreTotal);
    }
}