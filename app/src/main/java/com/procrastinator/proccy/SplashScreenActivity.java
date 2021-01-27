package com.procrastinator.proccy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            updateNameAndScores();
        } else {
            Intent signInIntent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(signInIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateNameAndScores(){
        db = FirebaseFirestore.getInstance();
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
                            DataHolder.getInstance().setTotalScore(0);
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

}