package com.procrastinator.proccy.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.procrastinator.proccy.MainActivity;
import com.procrastinator.proccy.PreferencesConfig;
import com.procrastinator.proccy.R;
import com.procrastinator.proccy.SignInActivity;

public class Home extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView scoreDailyTextView, scoreTotalTextView, displayNameTextView;
    private Button signOutButton;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

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
        displayNameTextView = getView().findViewById(R.id.textView_displayname);
        signOutButton = getView().findViewById(R.id.button_sign_out);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user !=  null){
            displayNameTextView.setText("Willkommen, " + user.getDisplayName());
        }

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });

        updateDailyAndTotalScore();
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
        String dailyscoreText = getResources().getString(R.string.textview_score_daily);
        String totalscoreText = getResources().getString(R.string.textview_score_total);

        scoreDailyTextView.setText(dailyscoreText + PreferencesConfig.loadDailyScore(requireActivity()));
        scoreTotalTextView.setText(totalscoreText + PreferencesConfig.loadTotalScore(requireActivity()));
    }

}