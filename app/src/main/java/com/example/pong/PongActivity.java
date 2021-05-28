package com.example.pong;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.os.Bundle;
import android.graphics.Point;
import android.view.Display;

/**
 * This class sets up the activity for the android game as a whole
 */
public class PongActivity extends Activity {

    private PongGame mPongGame;

    /**
     * This method creates the initial state of the game
     * @param savedInstanceState current state of game should orientation change.
     *                           null in initialization and is called when orientation changes
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mPongGame = new PongGame(this, size.x, size.y);
        setContentView(mPongGame);
    }

    /**
     * This method is called when the game is resumed from a pause
     */
    @Override
    protected void onResume(){
        super.onResume();
        mPongGame.resume();
    }

    /**
     * This method controlls what happens when the game is paused
     */
    @Override
    protected void onPause(){
        super.onPause();
        mPongGame.pause();
    }

}