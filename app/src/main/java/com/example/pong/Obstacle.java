package com.example.pong;

import android.graphics.RectF;

/**
 * This class contains the control variables for a bat like object
 */
public class Obstacle {

    private RectF mRect;
    private float mLength;
    private float mXCoord;
    private int mScreenX;
    private float mXVelocity;
    private float mStartX;

    /**
     * This method initializes the data for an instance of the obstacle object
     * @param sx Width of the game screen
     * @param sy Height of the game screen
     * @param px position of the obstacle in x plane
     * @param py position of the obstacle in y plane
     * @param vel velocity of the obstacle
     */
    public Obstacle(int sx, int sy, float px, float py, int vel){
        mScreenX = sx;
        mXCoord = mStartX = px;
        mLength = mScreenX / 6;

        float height = sy / 40;

        float mYCoord = py;
        mRect = new RectF(mXCoord, mYCoord,
                mXCoord + mLength,
                mYCoord + height);
        mXVelocity = vel;

    }

    /**
     * This method resets the object to its initial state
     */
    void reset(){
        mRect.left = mStartX;
        mRect.right = (mScreenX / 2) + mLength;
    }

    /**
     * THis method returns a reference to the Rect object in the class
     * @return reference to the Rect object variable
     */
    RectF getRect() { return mRect; }

    /**
     * This method reverses the travel velocity of the obstacle
     */
    void reverseVelocity() { mXVelocity = -mXVelocity; }

    /**
     * This method controlls what the behaviour of the obstacle each frame / loop
     * @param fps how many frames per second are being processed in game
     */
    void update(long fps){
        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.right = mRect.left + mLength;

        if (mRect.right >  mScreenX || mRect.left < 0) {
            reverseVelocity();
        }
    }


}
