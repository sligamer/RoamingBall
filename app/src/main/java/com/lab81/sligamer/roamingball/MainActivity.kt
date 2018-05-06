package com.lab81.sligamer.roamingball

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.nfc.cardemulation.HostNfcFService
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.constraint.ConstraintLayout
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Justin Freres on 4/10/2018.
 * Roaming Ball Lab 8.1 *
 * Plugin Support with kotlin_version = '1.2.41'
 */
class MainActivity : AppCompatActivity(), SensorEventListener {

    // DECLARE VARIABLES
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorAccelerometer: Sensor
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var ballImage: ImageView
    private lateinit var mBall: Ball

    private lateinit var movementThread: Thread

    companion object {
        var TOP: Int = 0
        var BOTTOM: Int = 0
        var LEFT: Int = 0
        var RIGHT: Int = 0
    }

    private lateinit var x_axis: TextView
    private lateinit var y_axis: TextView
    private lateinit var z_axis: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TASK 1: SET REFS TO LAYOUT UI
        mainLayout = findViewById(R.id.constraintLayout)

        x_axis = findViewById(R.id.zeroxaxistxtView)
        y_axis = findViewById(R.id.zeroyaxistxtView)
        z_axis = findViewById(R.id.zerozaxistxtView)

        // TASK 2: ADD THE BALL AND INIT MOVEMENT OF BALL SETTINGS
        mBall = Ball()
        initializeBall()
        ballImage = layoutInflater.inflate(R.layout.ball_item, null) as ImageView
        ballImage.x = 50.0F
        ballImage.y = 50.0F
        mainLayout.addView(ballImage, 0)

        // TASK 3: REGISTER THE SENOR MANAGER
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // TASK 4: IMPLEMENT THE MOVEMENT THREAD
        movementThread = Thread(BallMovement)
    }

    private fun initializeBall() {
        // COMPUTE WIDTH AND HEIGHT OF DEVICE
        var metrics: DisplayMetrics = this.resources.displayMetrics
        var screenWidth: Int = metrics.widthPixels
        var screenHeight: Int = metrics.heightPixels

        // CONFIG ROAMING BALL
        mBall.setX(50.0F)
        mBall.setY(50.0F)
        mBall.setWidth(225)

        mBall.setVelocityX(0.0F)
        mBall.setVelocityY(0.0F)

        TOP = 0
        BOTTOM = screenHeight - mBall.getWidth()
        LEFT = 0
        RIGHT = screenWidth - mBall.getWidth()


    }

    // REGISTER TEH SENSOR LISTENER
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        // START THE THREAD
        movementThread.start()
    }

    // UNREGISTER TEH SENOR LISTENER
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, sensorAccelerometer)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onDestroy() {
        finish()
        super.onDestroy()
    }

    private val BallMovement: Runnable = Runnable {
        var DELAY: Long = 20

        try {
            while (true)
            {
                mBall.setX(mBall.getX() - mBall.getVelocityX())
                mBall.setY(mBall.getY() - mBall.getVelocityY())

                // CHECK FOR COLISIONS
                if(mBall.getY() < TOP){
                    mBall.setY(TOP.toFloat())
                }
                else if(mBall.getY() > BOTTOM){
                    mBall.setY(BOTTOM.toFloat())
                }

                if(mBall.getX() < LEFT)
                {
                    mBall.setX(LEFT.toFloat())
                }
                else if (mBall.getX() > RIGHT)
                {
                    mBall.setX(RIGHT.toFloat())
                }

                // DELAY ANIMATIONS
                Thread.sleep(DELAY)

                // HANDLE TEH RELOCATION OF THE VIEW
                threadHandler.sendEmptyMessage(0)
            }
        }catch (e: InterruptedException)
        {

        }
    }

    private val threadHandler: Handler = object: Handler() {
        override fun handleMessage(msg: Message?) {
           // HANDLE THE RELOCATION OF THE IMAGE
            ballImage.x = mBall.getX()
            ballImage.y = mBall.getY()
        }
    }


    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     *
     *
     * See the SENSOR_STATUS_* constants in
     * [SensorManager][android.hardware.SensorManager] for details.
     *
     * @param accuracy The new accuracy of this sensor, one of
     * `SensorManager.SENSOR_STATUS_*`
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {


    }

    /**
     * Called when there is a new sensor event.  Note that "on changed"
     * is somewhat of a misnomer, as this will also be called if we have a
     * new reading from a sensor with the exact same sensor values (but a
     * newer timestamp).
     *
     *
     * See [SensorManager][android.hardware.SensorManager]
     * for details on possible sensor types.
     *
     * See also [SensorEvent][android.hardware.SensorEvent].
     *
     *
     * **NOTE:** The application doesn't own the
     * [event][android.hardware.SensorEvent]
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the [SensorEvent][android.hardware.SensorEvent].
     */
    override fun onSensorChanged(event: SensorEvent?) {
        when {
            event!!.sensor.type == Sensor.TYPE_ACCELEROMETER -> {
                mBall.setVelocityX(event.values[0])
                mBall.setVelocityY(event.values[1])

                x_axis.text = " ${event.values[0]}"
                y_axis.text = " ${event.values[1]}"
                z_axis.text = " ${event.values[2]}"
            }
        }
    }

    // DO NOT ALLOW CHANGING OF ORIENTATION
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item!!.itemId
        if(id == R.string.action_settings){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
