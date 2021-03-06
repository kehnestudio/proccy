package com.procrastinator.proccy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private SignInButton btnSignIn;
    private GoogleSignInClient mGoogleSignClient;
    private String TAG = "SignInActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private int totalscore;
    private int RC_SIGN_IN = 1;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnSignIn = findViewById(R.id.sign_in_button);

        mAuth = FirebaseAuth.getInstance();
        createLoginRequest();

        btnSignIn.setOnClickListener(v -> signIn());
    }

    private void createLoginRequest() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(SignInActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "firebaseAuthWithGoogle:" + acc.getId());
            firebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(SignInActivity.this, "Sign in unsuccessful", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Google sign in failed", e);
            firebaseGoogleAuth(null);
        }
    }

    // After a user successfully signs in, get an ID token from the GoogleSignInAccount object,
    // exchange it for a Firebase credential, and authenticate with Firebase using the Firebase credential:
    private void firebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                user = mAuth.getCurrentUser();
                Toast.makeText(SignInActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "signInWithCredential:success");
                checkUserDataOnLogin(user.getUid(), user.getDisplayName());
            } else {
                Toast.makeText(SignInActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "signInWithCredential:failure", task.getException());
            }
        });
    }

    private void updateNameAndScores(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "updateNameAndScores: Iam starting");

        if (user != null) {
            String uid = user.getUid();
            Uri uri = user.getPhotoUrl();
            String displayname = user.getDisplayName();
            DataHolder.getInstance().setDisplayName(displayname);
            DataHolder.getInstance().setUri(uri);

            DocumentReference docRef = db.collection("users").document(uid);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.getLong("totalscore") != null) {
                            DataHolder.getInstance().setTotalScore(document.getLong("totalscore").intValue());
                            Log.d(TAG, "Set totalscore to: " + DataHolder.getInstance().getTotalScore());
                            loadDailyScores();
                        } else {
                            Log.d(TAG, "No such document");
                            ArrayList<CalendarDay> datesArrayList = new ArrayList<>();
                            HashMap<CalendarDay, Integer> history = new HashMap<>();

                            DataHolder.getInstance().setTotalScore(0);
                            datesArrayList.add(CalendarDay.today());
                            history.put(CalendarDay.today(), 0);
                            DataHolder.getInstance().setDailyScoreHashMap(history);
                            DataHolder.getInstance().setCalendarDays(datesArrayList);
                            openMainActivity();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        } else {
            Log.d(TAG, "updateSharedPreferences: No user found");
        }
    }

    private void loadDailyScores() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String uid = user.getUid();

        CollectionReference scoreRef = db.collection("users").document(uid).collection("dailyScoreHistory");
        scoreRef.get().addOnSuccessListener(queryDocumentSnapshots -> {

            ArrayList<CalendarDay> datesArrayList = new ArrayList<>();
            HashMap<CalendarDay, Integer> history = new HashMap<>();

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                DailyScore dailyScore = documentSnapshot.toObject(DailyScore.class);
                Timestamp timestamp = dailyScore.getDate();
                int dailyScoreFromFireStore = dailyScore.getScore();
                Date date = timestamp.toDate();
                CalendarDay calendarDay = CalendarDay.from(date);

                datesArrayList.add(calendarDay);
                history.put(calendarDay, dailyScoreFromFireStore);

            }
            DataHolder.getInstance().setDailyScoreHashMap(history);
            DataHolder.getInstance().setCalendarDays(datesArrayList);
            openMainActivity();
        });
    }

    private void openMainActivity(){
        Intent mainscreenIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainscreenIntent);
    }

    private void checkUserDataOnLogin(String userId, String name) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", name);
        userMap.put("userid", userId);
        db.collection("users").document(userId).set(userMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateNameAndScores();
            }
        });
    }
}