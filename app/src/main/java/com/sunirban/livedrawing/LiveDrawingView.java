package com.sunirban.livedrawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class LiveDrawingView extends SurfaceView implements Runnable {
    private final boolean DEBUGGING = true;
    private final int MILLIS_IN_SECOND = 1000;
    // Objects needed to draw
    private final SurfaceHolder mOurHolder;
    private final Paint mPaint;
    // Holds the resolution of the screen
    private final int mScreenX;
    private final int mScreenY;
    // Text properties
    private final int mFontSize;
    private final int mFontMargin;
    // will be used for simple buttons
    private final RectF mResetButton;
    private final RectF mTogglePauseButton;
    // upto 1000 systems with 100 particles each
    private final int MAX_SYSTEMS = 1000;
    // Particles Systems
    private final ArrayList<ParticleSystem> mParticleSystems = new ArrayList<>();
    private final int mParticlesPerSystem = 100;
    private boolean mPaused = true;
    private Canvas mCanvas;
    // how many FPS aare we getting
    private long mFps;
    // here is thread and two control variables
    private Thread mThread = null;
    // this volatile variable can be accessed from both inside and outside of the thread
    private volatile boolean mDrawing;
    private int mNextSystem = 0;


    public LiveDrawingView(Context context, int x, int y) {
        super(context);
        mScreenX = x;
        mScreenY = y;

        // font is 5%(1/20) of screen width
        mFontSize = mScreenX / 20;

        // margin is 1.3%(1/75) of screen width
        mFontMargin = mScreenX / 75;

        mOurHolder = getHolder();
        mPaint = new Paint();

        // initialize 2 buttons
        mResetButton = new RectF(0, 0, 100, 100);
        mTogglePauseButton = new RectF(0, 150, 100, 250);
        // initialize the particles and their systems
        for (int i = 0; i < MAX_SYSTEMS; i++) {
            mParticleSystems.add(new ParticleSystem());
            mParticleSystems.get(i).init(mParticlesPerSystem);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // user moved a finger while touching screen
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            mParticleSystems.get(mNextSystem).emitParticles(new PointF(motionEvent.getX(), motionEvent.getY()));
            mNextSystem++;
            if (mNextSystem == MAX_SYSTEMS) {
                mNextSystem = 0;
            }
        }
        // button touches
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            // user pressed reset button rect
            if (mResetButton.contains(motionEvent.getX(), motionEvent.getY())) {
                // clear the screen of all particles
                mNextSystem = 0;
            }
            // user pressed the toggle pause rect
            if (mTogglePauseButton.contains(motionEvent.getX(), motionEvent.getY())) {
                mPaused = !mPaused;
            }
        }
        return true;
    }

    // This method is called by LiveDrawingActivity when user quits app
    public void pause() {
        // set mdDrawing to false as stopping the thread isn't instant
        mDrawing = false;
        try {
            // stop the thread
            mThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "Joining thread");
        }
    }

    // This method is called by LiveDrawingActivity when user starts app
    public void resume() {
        mDrawing = true;
        // initialise the instance of thread
        mThread = new Thread(this);
        // start the thread
        mThread.start();
    }

    // when we star thread with mThread.start() the run() method is continuously being called by the android
    // because we implemented the Runnable interface
    // calling mThread.join() will stop the thread
    @Override
    public void run() {
        // mDrawing gives us finer control rather than just relying on the calls to run
        // mDrawing must be true AND the thread running for the main loop to execute
        while (mDrawing) {
            // time at the start of the loop
            long frameStartTime = System.currentTimeMillis();

            // provided app not paused call update
            if (!mPaused) {
                update();
                // particles are in their new position

            }
            // movement have been handled now call draw
            draw();

            // how long this frame/loop take
            long timeCurrentFrame = System.currentTimeMillis() - frameStartTime;

            // timeCurrentFrame should be at least 1 as dividing by zero will crash
            if (timeCurrentFrame > 0) {
                // store the current frame rate in mFPS ready to pass to the update method
                // in the next frame/loop
                mFps = MILLIS_IN_SECOND / timeCurrentFrame;
            }
        }
    }

    // draw the particles system and the HUD
    private void draw() {
        if (mOurHolder.getSurface().isValid()) {
            // Lock the canvas (graphics memory) ready to draw
            mCanvas = mOurHolder.lockCanvas();

            // fill screen with a solid color
            mCanvas.drawColor(Color.argb(255, 0, 0, 0));

            // choose color to paint
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // choose font size
            mPaint.setTextSize(mFontSize);

            //draw the particle systems
            for (int i = 0; i < mNextSystem; i++) {
                mParticleSystems.get(i).draw(mCanvas, mPaint);
            }

            // draw the buttons
            mCanvas.drawRect(mResetButton, mPaint);
            mCanvas.drawRect(mTogglePauseButton, mPaint);
            // draw the HUD
            if (DEBUGGING) {
                printDebuggingText();
            }

            // Display the Drawing on screen

            mOurHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    // update particles
    private void update() {
        for (int i = 0; i < mParticleSystems.size(); i++) {
            if (mParticleSystems.get(i).mIsRunning) {
                mParticleSystems.get(i).update(mFps);
            }
        }
    }

    private void printDebuggingText() {
        int debugSize = mFontSize / 2;
        int debugStart = 50;
        mPaint.setTextSize(debugSize);
        mPaint.setColor(Color.argb(255, 255, 75, 31));
        mCanvas.drawText("FPS: " + mFps, mScreenX - 300, debugStart + debugSize, mPaint);
        mCanvas.drawText("Systems: " + mNextSystem, 10, mFontMargin + debugStart + debugSize * 5, mPaint);
        mCanvas.drawText("Particles: " + mNextSystem * mParticlesPerSystem, 10, mFontMargin + debugStart + debugSize * 6, mPaint);
    }

}
