package com.negiwang.android.Users;

import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.voicetalk.AudioRecorder.AndroidAudioRecorder;
import com.voicetalk.AudioRecorder.model.AudioChannel;
import com.voicetalk.AudioRecorder.model.AudioSampleRate;
import com.voicetalk.AudioRecorder.model.AudioSource;
import com.negiwang.android.R;

public class SubRecorderActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_recorder);

        recordAudio();
        finish();
    }

    public void recordAudio() {

        String AUDIO_FILE_PATH =
                Environment.getExternalStorageDirectory().getPath() + "/recorded_audio.mp3";

                AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(AUDIO_FILE_PATH)
                .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                .setRequestCode(REQUEST_RECORD_AUDIO)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(false)
                .setKeepDisplayOn(true)

                // Start recording
                .record();
    }
}
