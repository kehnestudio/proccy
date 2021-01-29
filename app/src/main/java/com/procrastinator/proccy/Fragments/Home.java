package com.procrastinator.proccy.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.procrastinator.proccy.DataHolder;
import com.procrastinator.proccy.PreferencesConfig;
import com.procrastinator.proccy.R;
import com.procrastinator.proccy.SignInActivity;
import com.procrastinator.proccy.TimerService;
import com.procrastinator.proccy.Utilities;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String ARG_PARAM1 = "daily score";
    private static final String ARG_PARAM2 = "total score";
    private TextView scoreDailyTextView, scoreTotalTextView, displayNameTextView;
    private Button signOutButton;
    private int scoreDaily, scoreTotal;
    private String displayName;
    private CircleImageView profilePictureImageView;

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
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
        profilePictureImageView = getView().findViewById(R.id.imageView_profile_picture);
        updateUI();

        signOutButton.setOnClickListener(v -> sendToLogin());


        super.onViewCreated(view, savedInstanceState);
    }

    private void sendToLogin() {
        GoogleSignInClient mGoogleSignInClient ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        //signout Google
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(),
                task -> {
                    FirebaseAuth.getInstance().signOut(); //signout firebase
                    Intent setupIntent = new Intent(requireActivity(), SignInActivity.class);
                    PreferencesConfig.clearAllPreferences(requireActivity());
                    requireActivity().stopService(new Intent(requireActivity(), TimerService.class));
                    Toast.makeText(requireActivity(), "Logged Out", Toast.LENGTH_LONG).show();
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                    requireActivity().finish();
                });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesConfig.PREF_DAILY_SCORE)) {
            updateUI();
        }
    }

    public void updateUI() {
        scoreDaily = Utilities.getCurrentDayDailyScore();
        scoreTotal = DataHolder.getInstance().getTotalScore();
        displayName = DataHolder.getInstance().getDisplayName();
        Picasso.get().load(DataHolder.getInstance().getUri()).placeholder(R.drawable.ic_sloth_svg).into(profilePictureImageView);

        scoreDailyTextView.setText(getResources().getString(R.string.textview_score_daily, scoreDaily));
        scoreTotalTextView.setText(getResources().getString(R.string.textview_score_total,scoreTotal));
        displayNameTextView.setText(getString(R.string.home_fragment_displayname, displayName));
    }
}