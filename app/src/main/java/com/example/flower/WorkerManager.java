package com.example.flower;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkerManager extends Worker {
    public static final String TAG = "WorkerManager";

    public WorkerManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String name = inputData.getString("name");
        int number = inputData.getInt("number", -1);
        Log.d(TAG, "doWork: number: " + number );

        for (int i=number; i>0;i--){
            Log.d(TAG, "doWork: i was:" + i);
            try {
                Thread.sleep(1000);
            }   catch (InterruptedException e){
                e.printStackTrace();
                return  Result.failure();
            }
        }
        return Result.success();
    }
}
