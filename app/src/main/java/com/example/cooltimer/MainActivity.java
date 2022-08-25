package com.example.cooltimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBar seekBar;
    private TextView textView;
    private int maxTime;
    private int tick;
    private CountDownTimer timer;
    private Button button;
    private MediaPlayer player;
    private boolean isCheck = false;
    private int defaultInterval;
    private SharedPreferences preferences;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }else if(id == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = findViewById(R.id.seekBarTime);
        textView = findViewById(R.id.textViewTimer);
        button = findViewById(R.id.buttonStart);
        maxTime = 600000;
        tick = 1000;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        seekBar.setMax(maxTime);
        setIntervalTimer(preferences);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    maxTime = progress;
                    textView.setText(String.valueOf(progress / 1000));
                    timer = new CountDownTimer(maxTime, tick) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            textView.setText(String.valueOf(millisUntilFinished / 1000));
                        }

                        @Override
                        public void onFinish() {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            if(preferences.getBoolean("enable_sound", true)){
                                String melody = preferences.getString("timer_melody", "bell");
                                if(melody.equals("bell")){
                                    player = MediaPlayer.create(getApplicationContext(), R.raw.bell_sound);
                                    player.start();
                                }else if(melody.equals("alarm")){
                                    player = MediaPlayer.create(getApplicationContext(), R.raw.alarm_siren_sound);
                                    player.start();
                                }else if(melody.equals("bip")){
                                    player = MediaPlayer.create(getApplicationContext(), R.raw.bip_sound);
                                    player.start();
                                }

                            }
                        }
                    };
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(!isCheck){
                                timer.start();
                                button.setText(R.string.pause);
                                isCheck = true;
                                seekBar.setEnabled(false);
                            }else{
                                timer.cancel();
                                button.setText(R.string.start);
                                isCheck = false;
                                seekBar.setEnabled(true);
                            }
                        }
                    });
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private void setIntervalTimer(SharedPreferences preference){
        defaultInterval = Integer.parseInt(preferences.getString("timer_interval", "60"));
        seekBar.setProgress(defaultInterval);
        textView.setText(String.valueOf(defaultInterval));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("timer_interval")){
            setIntervalTimer(preferences);
        }
    }
}