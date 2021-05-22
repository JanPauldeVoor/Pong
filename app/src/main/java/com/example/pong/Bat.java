package com.example.pong;

import android.graphics.RectF;

public class Bat {
    // These are all member variables (fields)
    // They all have the m prefix
    // They are all private
    // Direct access is not required
    private RectF mRect;
    private float mLength;
    private float mXCoord;
    private float mBatSpeed;
    private int mScreenX;

    // These variables are public and final
    // Can be directly accessed as they are not part of the game
    final int STOPPED = 0;
    final int LEFT = 1;
    final int RIGHT = 2;

    // Keeps track of if and how the bat is moving
    // Starting with STOPPED condition
    private int mBatMoving = STOPPED;

    public Bat(int sx, int sy){
        mScreenX = sx;

        // Configure size of bat based on 1/8 of screen resolution
        mLength = mScreenX / 8;

        // 1/40 screen height
        float height = sy / 40;

        // Configure starting location of bat roughly in the middle horizontally
        mXCoord = mScreenX / 2;

        // The height of the bat off the bottom of the screen
        float mYCoord = sy - height;

        // Initialize mRect based on the size and position
        mRect = new RectF(mXCoord, mYCoord,
                mXCoord + mLength,
                mYCoord + height);

        // Configure the speed of the bat
        // Can cover the width of the screen in 1 second
        mBatSpeed = mScreenX;
    }

    // Return a reference to the mRect object
    RectF getRect(){
        return mRect;
    }

    // Update the movement state passed by onTouchEvent
    void setMovementState(int state){
        mBatMoving = state;
    }

    // Update bat each frame / loop
    void update(long fps){
        // Move the bat based on mBatMoving and speed of previous frame
        if (mBatMoving == LEFT){
            mXCoord = mXCoord - mBatSpeed / fps;
        }
        if (mBatMoving == RIGHT){
            mXCoord = mXCoord + mBatSpeed / fps;
        }

        // Stop the bat going off screen
        if (mXCoord < 0){
            mXCoord = 0;
        } else if (mXCoord + mLength > mScreenX){
            mXCoord = mScreenX - mLength;
        }

        // Update mRect based on results from the previous code in update
        mRect.left = mXCoord;
        mRect.right = mXCoord + mLength;
    }
}
