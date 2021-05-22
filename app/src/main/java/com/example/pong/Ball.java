package com.example.pong;

import android.graphics.Rect;
import android.graphics.RectF;

public class Ball {
    // These are all member variables (fields)
    // They all have the m prefix
    // They are all private
    // Direct access is not required
    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;

    // This is the constructor method
    // Called by PongGame class
    public Ball(int screenX){
        // Make the ball square and 1% of screen width
        mBallWidth = screenX / 100;
        mBallHeight = screenX / 100;

        // Initialize the RectF with 0, 0, 0, 0
        // We do it because we only want to do it once

        // We will initialize the detail at the start of each game
        mRect = new RectF();
    }

    // Return a reference to mRect to PongGame
    RectF getRect(){
        return mRect;
    }

    // Update the ball position on each frame / loop
    void update(long fps){
        // Move the ball based on teh horizontal and vertical speed
        // and the current fps

        // Move the top left corner
        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);

        // Match up the bottom right corner based on ball size
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top + mBallHeight;
    }

    // Reverse the vertical direction of travel
    void reverseYVelocity(){
        mYVelocity = -mYVelocity;
    }

    // Reverse the horizontal direction of travel
    void reverseXVelocity(){
        mXVelocity = -mXVelocity;
    }

    void reset(int x, int y){
        // Initialize the four points of the rectangle that defines the ball
        mRect.left = x / 2;
        mRect.top = 0;
        mRect.right = x / 2 + mBallWidth;
        mRect.bottom = mBallHeight;

        // How fast will the ball travel
        // Can be changed for difficulty
        mYVelocity = -(y / 3);
        mXVelocity  = (x / 2);
    }

    void increaseVelocity(){
        // Increase the speed by 10%
        mXVelocity = mXVelocity * 1.1f;
        mYVelocity = mYVelocity * 1.1f;
    }

    // Bounce the ball back based on
    // whether it hits the left or right-hand side
    void batBounce(RectF batPosition){
        // Detect the center of the bat
        float batCenter = batPosition.left + (batPosition.width() / 2);

        // Detect the center of the ball
        float ballCenter = mRect.left + (mBallWidth / 2);

        // Where did the ball hit?
        float relativeIntersect = (batCenter - ballCenter);

        // Pick a bounce direction
        if (relativeIntersect < 0){
            // Go right
            mXVelocity = Math.abs(mXVelocity);
        } else {
            // Go left
            mXVelocity = -Math.abs(mXVelocity);
        }

        // reverse Y velocity to send ball back upwards
        reverseYVelocity();
    }

}
