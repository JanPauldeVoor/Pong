package com.example.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PongGame extends SurfaceView {
    // Are we Debugging?
    private final boolean DEBUGGING = true;

    // These objects needed to draw
    private SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    // How many frames per second did we get?
    private long mFPS;

    // number of milliseconds in a second
    private final int MILLIS_IN_SECOND = 1000;

    // Holds screen resolution
    private int mScreenX;
    private int mScreenY;

    // How big will the text be?
    private int mFontSize;
    private int mFontMargin;

    // The game objects
    private Bat mBat;
    private Ball mBall;

    // The current score and lives remaining
    private int mScore;
    private int mLives;

    // PongGame Constructor
    // Called when mPongGame is created
    public PongGame(Context context, int x, int y){
        // Super.. calls parent class
        // constructor of SurfaceView provided by Android
        super(context);

        // Initialize these two members/fields with passed values
        mScreenX = x;
        mScreenY = y;

        // Font is 5% of screen width
        mFontSize = mScreenX / 20;
        // Margin is 1.5% of screen width
        mFontMargin = mScreenX / 75;

        // Initialize the objects ready for drawing with
        // getHolder: method of SurfaceView
        mOurHolder = getHolder();
        mPaint = new Paint();

        // Initialize the bat and ball

        // Start game
        startNewGame();

    }

    // Player has lost or is starting first game
    private void startNewGame(){
        // Put the ball back to the starting position

        // Reset the score and the player's chances
        mScore = 0;
        mLives = 3;
    }

    // Draw the game objects and the HUD
    private void draw(){
        if (mOurHolder.getSurface().isValid()){
            // lock canvas ready to draw
            mCanvas = mOurHolder.lockCanvas();

            // Fill screen with solid color
            mCanvas.drawColor(Color.argb(255, 26, 128, 182));

            // Choose a color to paint with
            mPaint.setColor(Color.argb(255,255,255,255));

            // Draw the bat and ball

            // Choose the font size
            mPaint.setTextSize(mFontSize);

            // Draw the HUD
            mCanvas.drawText("Score: " + mScore + "    Lives: " + mLives,
                    mFontMargin, mFontSize, mPaint);

            if (DEBUGGING){
                printDebuggingText();
            }

            // Display the drawing on screen

            // unlockCanvasAndPost is a method of SurfaceView
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    public void printDebuggingText(){
        int debugSize = mFontSize / 2;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);
        mCanvas.drawText("FPS: " + mFPS,
                10, debugStart + debugSize, mPaint);
    }
}
