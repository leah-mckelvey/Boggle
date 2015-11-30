package com.interview.leah.boggle;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.interview.leah.boggle.model.Board;

import org.w3c.dom.Text;

import java.util.concurrent.Callable;


public class BoggleActivity extends Activity implements SensorEventListener {

    private String word = "";
    private TextView wordText;
    private Board board;
    private TextView scoreText;
    private TextView shakeView;
    SensorManager sensorMgr;
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    private long mShakeTimestamp;
    private int mShakeCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boggle);
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor accelerometer = sensorMgr
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        board = new Board();
        board.shake();
        initViews();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    private void initViews() {
        wordText = (TextView) findViewById(R.id.word);
        scoreText = (TextView) findViewById(R.id.score);
        shakeView = (TextView) findViewById(R.id.shake);
        shakeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board.shake();
                Resources r = getResources();
                String name = getPackageName();
                TextView textView;
                for (int i = 0; i < Board.BOARD_SIZE; i++) {
                    for (int j = 0; j < Board.BOARD_SIZE; j++) {
                        int id = r.getIdentifier("box" + i + j, "id", name);
                        textView = (TextView) findViewById(id);


                        if (textView != null) {
                            textView.setText(board.getLetter(i, j));
                        }
                    }
                }
                scoreText.setText("" + board.getScore());
                word = "";
                wordText.setText(word);
            }
        });
        Resources r = getResources();
        String name = getPackageName();
        TextView textView;
        for (int i=0; i < Board.BOARD_SIZE; i++) {
            for (int j=0; j < Board.BOARD_SIZE; j++) {
                int id = r.getIdentifier("box" + i + j, "id", name);
                textView = (TextView) findViewById(id);


                if (textView != null) {
                    textView.setText(board.getLetter(i, j));
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectBox(v);
                        }
                    });
                }
            }
        }
    }

    // Something like this might be appropriate to eliminate the repeated code in this class, but
    // I ran out of time to get this working exactly
    private void applyToBoard(Callable func) {
        Resources r = getResources();
        String name = getPackageName();
        TextView textView;
        for (int i=0; i < Board.BOARD_SIZE; i++) {
            for (int j=0; j < Board.BOARD_SIZE; j++) {
                int id = r.getIdentifier("box" + i + j, "id", name);
                textView = (TextView) findViewById(id);
                try {
                    func.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = FloatMath.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0;
                }

                mShakeTimestamp = now;
                mShakeCount++;

                board.shake();
                Resources r = getResources();
                String name = getPackageName();
                TextView textView;
                for (int i=0; i < Board.BOARD_SIZE; i++) {
                    for (int j=0; j < Board.BOARD_SIZE; j++) {
                        int id = r.getIdentifier("box" + i + j, "id", name);
                        textView = (TextView) findViewById(id);
                        textView.setText(board.getLetter(i, j));
                        textView.invalidate();
                    }
                }
            }
    }

    private void selectBox(View view) {
        TextView textBox = (TextView) view;
        String tag = textBox.getTag().toString();
        Point location = new Point(Integer.parseInt(String.valueOf(tag.charAt(0))), Integer.parseInt(String.valueOf(tag.charAt(1))));
        if (board.tryBox(location)) {
            word += ((TextView) view).getText();
            wordText.setText(word);
            wordText.invalidate();
            scoreText.setText("" + board.getScore());
            scoreText.invalidate();
        }
    }
}
