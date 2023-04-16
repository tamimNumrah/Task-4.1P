package com.tamim.task41p;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView exerciseStateTextView;
    TextView timerDurationTextView;
    ProgressBar progressBar;
    Button startButton;
    EditText exerciseDuration;
    EditText restDuration;
    Button setTimerButton;

    Integer restDurationSeconds = 0;
    Integer exerciseDurationSeconds = 0;

    CountDownTimer exerciseTimer;
    CountDownTimer restTimer;

    Boolean timerRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerDurationTextView = findViewById(R.id.timerDurationTextView);
        exerciseStateTextView = findViewById(R.id.exerciseStateTextView);
        progressBar = findViewById(R.id.progressBar);
        startButton = findViewById(R.id.startButton);
        exerciseDuration = findViewById(R.id.exerciseDuration);
        restDuration = findViewById(R.id.restDuration);
        setTimerButton = findViewById(R.id.setTimerButton);
        resetTexts();
    }

    private void resetTexts() {
        timerDurationTextView.setText("");
        exerciseStateTextView.setText("");
        progressBar.setProgress(0);
        startButton.setText("Start");
    }

    private void startWorkoutTimer() {
        exerciseStateTextView.setText("Workout");
        progressBar.setMax(exerciseDurationSeconds*1000);
        exerciseTimer = new CountDownTimer(exerciseDurationSeconds*1000, 1000) {
            @Override
            public void onTick(long l) {
                progressBar.setProgress((int) l);
            }

            @Override
            public void onFinish() {
                System.out.println("workout timer finished");
                startRestingTimer();
            }
        }.start();
    }

    private void cancelExerciseTimer() {
        if (exerciseTimer != null) {
            exerciseTimer.cancel();
        }
    }

    private void startRestingTimer() {
        exerciseStateTextView.setText("Rest");
        progressBar.setMax(restDurationSeconds*1000);
        restTimer = new CountDownTimer(restDurationSeconds*1000, 1000) {
            @Override
            public void onTick(long l) {
                progressBar.setProgress((int) l);
            }

            @Override
            public void onFinish() {
                System.out.println("resting timer finished");
                startWorkoutTimer();
            }
        }.start();
    }

    private void cancelRestTimer() {
        if (restTimer != null) {
            restTimer.cancel();
        }
    }


    public void onStartButtonPressed(View view) {
        if (timerRunning) {
            System.out.println("Stop Button Pressed!");
            resetTexts();
            timerRunning = false;
            cancelExerciseTimer();
            cancelRestTimer();
            return;
        }
        System.out.println("Start Button Pressed!");
        if (restDurationSeconds == 0 || exerciseDurationSeconds == 0 ) {
            showToast("Please set durations properly");
            return;
        }
        timerRunning = true;
        startButton.setText("Stop");
        startWorkoutTimer();
    }
    public void onSetTimerButtonPressed(View view) {
        System.out.println("onSetTimerButtonPressed Pressed!");
        String exerciseDurationString = exerciseDuration.getText().toString();
        if (exerciseDurationString.matches("")) {
            showToast("You did not enter Exercise duration");
            return;
        }
        String restDurationString = restDuration.getText().toString();
        if (restDurationString.matches("")) {
            showToast("You did not enter rest duration");
            return;
        }
        restDurationSeconds = Integer.valueOf(restDurationString);
        exerciseDurationSeconds = Integer.valueOf(exerciseDurationString);
        showToast("Duration set successfully!");
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}