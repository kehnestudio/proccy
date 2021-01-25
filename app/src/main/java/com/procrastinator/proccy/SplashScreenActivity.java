package com.procrastinator.proccy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser user;

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "updateNameAndScores: Iam starting");

        if (user != null) {
            String uid = user.getUid();
            String displayname = user.getDisplayName();
            DataHolder.getInstance().setDisplayName(displayname);
            DocumentReference docRef = db.collection("users").document(uid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.getLong("totalscore") != null) {
                                DataHolder.getInstance().setTotalScore(document.getLong("totalscore").intValue());
                                Log.d(TAG, "Set totalscore to: " + DataHolder.getInstance().getTotalScore());
                                Intent mainscreenIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(mainscreenIntent);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                }
            });

        } else {
            Log.d(TAG, "updateSharedPreferences: No user found");
        }
    }
}