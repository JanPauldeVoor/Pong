package com.example.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * This class contains the macro workings of the pong game
 */
public class PongGame extends SurfaceView implements Runnable{

    // Are we Debugging?
    private final boolean DEBUGGING = true;

    // These objects needed to draw
    private SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private Paint mPaint;
    private Paint mBatPaint;
    private Paint mBallPaint;

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
    private Obstacle mObs;

    // The current score and lives remaining
    private int mScore;
    private int mLives;

    // Thread and two control variables
    private Thread mGameThread = null;
    // Volatile variable can be accessed from inside and outside the thread
    private volatile boolean mPlaying;
    private boolean mPaused = true;

    // All of these are for playing sounds
    private SoundPool mSP;
    private int mBeepID = -1;
    private int mBoopID = -1;
    private int mBopID = -1;
    private int mMissID = -1;

    /**
     *  This is the constructor of the game state and initializes
     *  the settings of the game
     *
     * @param context The context in which the game is being played
     * @param x the horizontal size of the screen
     * @param y the vertical size of the screen
     */
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

        // Choose a color to paint with
        mPaint.setColor(Color.argb(255,255,255,255));

        // Initialize random color variables
        mBatPaint = new Paint();
        mBallPaint = new Paint();

        // Initialize the bat and ball
        mBall = new Ball(mScreenX);
        mBat = new Bat(mScreenX, mScreenY);
        mObs = new Obstacle(mScreenX, mScreenY,
                mScreenX / 2, mScreenY / 4, mScreenX / 3);

        // Prepare the SoundPool instance
        // Depending on version of Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder().
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        // Open each of the sound files in turn and load into RAM
        // Try-catch handles when this fails
        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("boop.ogg");
            mBoopID = mSP.load(descriptor, 0);
        } catch (IOException e){
            Log.d("Error", "failed to load sound files");
        }


        // Start game
        startNewGame();

    }

    /**
     * This method sets the runtime behaviour of the game
     */
    @Override
    public void run(){
        // mPlaying gives finer control
        // than just relying on calls to run

        // mPlaying must be true AND thread running for main loop execution
        while (mPlaying){
            // What time is it at start
            long frameStartTime = System.currentTimeMillis();

            // Call update method if game is not paused
            if (!mPaused){
                update();
                // Now bat and ball are in new positions, detect collisions
                detectCollisions();
            }

            // Movement and collisions have been handled so draw scene
            draw();

            // How long did frame / loop take?
            long timeThisFrame = System.currentTimeMillis() - frameStartTime;

            // Make sure timeThisFrame is at least 1ms
            // Accidentally dividing by 0 crashes things

            if (timeThisFrame > 0){
                // Store the current frame rate in mFPS
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    /**
     * This method controls what happens each time the game screen is touched
     * @param motionEvent State of the player touch input
     * @return whether the event has been processed, true if handled, false if not
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        // This switch block replaces if statement from SubHunter
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
            // The player has put their finger on teh screen
            case MotionEvent.ACTION_DOWN:
                mPaused = false;

                // Where did the touch happen
                if (motionEvent.getX() > mScreenX / 2){
                    // On right hand side
                    mBat.setMovementState(mBat.RIGHT);
                } else {
                    // On the left hand side
                    mBat.setMovementState(mBat.LEFT);
                }
                break;

            // The player has lifted their finger off the screen
            // Multiple fingers can cause bugs
            case MotionEvent.ACTION_UP:
                // Stop the bat moving
                mBat.setMovementState(mBat.STOPPED);
                break;
        }

        return true;
    }

    /**
     * This method controls what happens each frame / loop in the game
     */
    private void update(){
        // update bat and ball
        mBall.update(mFPS);
        mBat.update(mFPS);
        mObs.update(mFPS);
    }

    /**
     * This method controls the randomization of colours in the game
     */
    private void changeColor(){
        final int MAX_RGB = 256;
        final int MIN_RGB = 0;

        mBallPaint.setColor(Color.argb(255, (int)(Math.random() * (MAX_RGB - MIN_RGB + 1 ) + MIN_RGB),
                (int)(Math.random() * (MAX_RGB - MIN_RGB + 1 ) + MIN_RGB),
                (int)(Math.random() * (MAX_RGB - MIN_RGB + 1 ) + MIN_RGB) ));

        mBatPaint.setColor(Color.argb(255, (int)(Math.random() * (MAX_RGB - MIN_RGB + 1 ) + MIN_RGB),
                (int)(Math.random() * (MAX_RGB - MIN_RGB + 1 ) + MIN_RGB),
                (int)(Math.random() * (MAX_RGB - MIN_RGB + 1 ) + MIN_RGB) ));

    }

    /**
     * This method controls what happens when the ball collides with an object
     * @param obj Rect object the ball collided with
     */
    private void collisionAction(RectF obj){
        // Realistic-ish bounce
        mBall.batBounce(obj);
        mBall.increaseVelocity();
        mScore++;
        changeColor();
        mSP.play(mBeepID, 1, 1, 0, 0, 1);
    }

    /**
     * This method detects if the ball collided with another object or wall
     */
    private void detectCollisions(){
        // Has bat hit ball?

        if (RectF.intersects(mObs.getRect(), mBall.getRect())){

            collisionAction(mObs.getRect());
        }


        if (RectF.intersects(mBat.getRect(), mBall.getRect())){
            Log.d("Debug", "hit object");
            collisionAction(mBat.getRect());
        }


        // Has ball hit edge of screen?

        // Bottom
        if (mBall.getRect().bottom > mScreenY){
            mBall.reverseYVelocity();
            mLives--;
            changeColor();
            mSP.play(mBoopID, 1, 1, 0, 0, 1);
            if (mLives == 0) {
                mPaused = true;
                startNewGame();
            }
        }

        // Top
        if (mBall.getRect().top < 0){
            mBall.reverseYVelocity();
            mSP.play(mBoopID, 1, 1, 0, 0, 1);
            changeColor();
        }

        // Left
        if (mBall.getRect().left < 0){
            mBall.reverseXVelocity();
            mSP.play(mBoopID, 1, 1, 0, 0, 1);
            changeColor();
        }

        // Right
        if (mBall.getRect().right > mScreenX){
            mBall.reverseXVelocity();
            mSP.play(mBoopID, 1, 1, 0, 0, 1);
            changeColor();
        }
    }

    /**
     * This method controls what happens when the player pauses the game
     */
    public void pause(){
        // Set playing to false,
        // Stopping thread isn't always instant
        mPlaying = false;
        mBallPaint = mPaint;
        mBallPaint = mPaint;
        try{
            // Stop thread
            mGameThread.join();
        } catch (InterruptedException e){
            Log.e("Error: ", "joining thread");
        }
    }

    /**
     * This method controls what happens when the game is started or restarted
     */
    public void resume(){

        mPlaying = true;
        mBallPaint = mPaint;
        mBatPaint = mPaint;
        // Initialize the instance of Thread
        mGameThread = new Thread(this);
        // Start the Thread
        mGameThread.start();
    }

    /**
     * This method resets the game state to the initial state
     */
    private void startNewGame(){
        // Put the ball back to the starting position
        mBall.reset(mScreenX, mScreenY);
        mObs.reset();

        // Reset the score and the player's chances
        mScore = 0;
        mLives = 3;
    }

    /**
     * This method draws all the objects and information to the game screen
     */
    private void draw(){
        if (mOurHolder.getSurface().isValid()){
            // lock canvas ready to draw
            mCanvas = mOurHolder.lockCanvas();

            // Fill screen with solid color
            mCanvas.drawColor(Color.argb(255, 26, 128, 182));

            // Draw the bat and ball
            mCanvas.drawRect(mBall.getRect(), mBallPaint);
            mCanvas.drawRect(mBat.getRect(), mBatPaint);
            mCanvas.drawRect(mObs.getRect(), mPaint);

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

    /**
     * This method prints the debugging text to the screen
     */
    public void printDebuggingText(){
        int debugSize = mFontSize / 2;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);
        mCanvas.drawText("FPS: " + mFPS,
                10, debugStart + debugSize, mPaint);
    }
}
