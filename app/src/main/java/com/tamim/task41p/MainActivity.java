package com.tamim.task41p;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;


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
    public static Boolean stop = false;

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
        progressBar.setMax(exerciseDurationSeconds);
        exerciseTimer = new CountDownTimer(exerciseDurationSeconds * 1000, 1000) {
            @Override
            public void onTick(long l) {
                Integer duration = exerciseDurationSeconds - (int) (l / 1000);
                progressBar.setProgress(duration);
                timerDurationTextView.setText("" + duration + " seconds");
                if (stop) {
                    stop = false;
                    stop();
                }
            }

            @Override
            public void onFinish() {
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
                System.out.println("workout timer finished");
                startRestingTimer();
                sendNotification("Workout finished. Rest started.");
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
        progressBar.setMax(restDurationSeconds);
        restTimer = new CountDownTimer(restDurationSeconds * 1000, 1000) {
            @Override
            public void onTick(long l) {
                Integer duration = restDurationSeconds - (int) (l / 1000);
                progressBar.setProgress(duration);
                timerDurationTextView.setText("" + duration + " seconds");
                if (stop) {
                    stop = false;
                    stop();
                }
            }

            @Override
            public void onFinish() {
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
                System.out.println("resting timer finished");
                startWorkoutTimer();
                sendNotification("Rest finished. Workout started.");
            }
        }.start();
    }

    private void cancelRestTimer() {
        if (restTimer != null) {
            restTimer.cancel();
        }
    }


    private  void stop() {
        System.out.println("Stop Button Pressed!");
        resetTexts();
        timerRunning = false;
        cancelExerciseTimer();
        cancelRestTimer();
    }

    public void onStartButtonPressed(View view) {
        if (timerRunning) {
            stop();
            return;
        }
        System.out.println("Start Button Pressed!");
        if (restDurationSeconds == 0 || exerciseDurationSeconds == 0) {
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

    public void sendNotification(String message) {
// Create an explicit intent for an Activity in your app
        System.out.println("send notification");
        createNotificationChannel();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        Intent intentAction = new Intent(this,ActionReceiver.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this,1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Timer ended")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_background, "Stop", pendingIntentStop)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(000, builder.build());
    }
    private void createNotificationChannel() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("channel", "TIMER", importance);
        channel.setDescription("Timer");
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}

