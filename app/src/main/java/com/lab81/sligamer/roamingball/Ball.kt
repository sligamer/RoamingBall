package com.lab81.sligamer.roamingball

/**
 * Created by Justin Freres on 4/10/2018.
 * Roaming Ball Lab 8.1 *
 * Plugin Support with kotlin_version = '1.2.41'
 */
class Ball {
    // DECLARE VARIABLES
    private var mX: Float = 0F
    private var mY: Float = 0F
    private var mWidth: Int = 0
    private var mVelocityX: Float = 0F
    private var mVelocityY: Float = 0F

    fun setX(x: Float)
    {
        mX = x
    }

    fun getX():Float {
        return mX
    }

    fun setY(y: Float)
    {
        mY = y
    }

    fun getY():Float {
        return mY
    }


    fun setWidth(w: Int)
    {
        mWidth = w
    }

    fun getWidth():Int {
        return mWidth
    }

    fun setVelocityX(Vx: Float)
    {
        mVelocityX = Vx
    }

    fun getVelocityX():Float {
        return mVelocityX
    }

    fun setVelocityY(Yx: Float)
    {
        mVelocityY = Yx
    }

    fun getVelocityY():Float {
        return mVelocityY
    }

}