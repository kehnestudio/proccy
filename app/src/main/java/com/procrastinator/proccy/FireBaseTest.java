package com.procrastinator.proccy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;


public class FireBaseTest extends AppCompatActivity {

    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
    private Button button;
    private FirebaseDatabase mDataBase;

    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebasetest);


        checkBox1 = findViewById(R.id.checkBox);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        button = findViewById(R.id.buttonQuestions);
        AtomicReference<String> q1 = new AtomicReference<>("HELLO");

        language = Locale.getDefault().getLanguage();
        Log.d("TAG", "onCreate: " + language);

        button.setOnClickListener(v -> {
            changeCheckBox();
        });
    }

    public void changeCheckBox() {
        DatabaseReference reference = mDataBase.getInstance().getReference("questions");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Counts number of questions (children) on firebase
                long childrenCount = snapshot.getChildrenCount();
                //sets max number based on children count
                int max = (int) childrenCount;

                //creates a new ArrayList with size of childrenCount
                final List<Integer> l = new ArrayList<>();
                for (int j = 0; j < max; j++) {
                    l.add(j);
                }
                //Shuffles the created Arraylist
                Collections.shuffle(l);

                //Converts the Integers in Array List to Strings
                String count1 = Integer.toString(l.get(0));
                String count2 = Integer.toString(l.get(1));
                String count3 = Integer.toString(l.get(2));
                String count4 = Integer.toString(l.get(3));


                if (snapshot.exists()) {
                    Log.d("TAG", "onDataChange exists");
                    String check1 = snapshot.child(count1).child(language).getValue(String.class);
                    String check2 = snapshot.child(count2).child(language).getValue(String.class);
                    String check3 = snapshot.child(count3).child(language).getValue(String.class);
                    String check4 = snapshot.child(count4).child(language).getValue(String.class);
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
}