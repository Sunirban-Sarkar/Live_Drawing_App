package com.sunirban.livedrawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Random;

public class ParticleSystem {
    private final Random random = new Random();
    boolean mIsRunning = false;
    private float mDuration;
    private ArrayList<Particle> mParticles;

    // creates new particles and gives them velocity vectors, called only once
    void init(int numParticles) {
        mParticles = new ArrayList<>();
        // create the particles

        for (int i = 0; i < numParticles; i++) {
            float angle = (random.nextInt(360));
            // the random angle between 0 to 359 in degree to radian
            angle *= 3.14f / 180.f;

            // fast particles
            //float speed = (random.nextInt(10) + 1);
            // slow particles
            float speed = random.nextFloat()/10;

            // created vectors from the random speed and random angle
            PointF direction = new PointF((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);
            mParticles.add(new Particle(direction));
        }
    }

    void update(long fps) {
        // subtract elapsed time of a frame (got from 1/fps) from total duration
        mDuration -= (1f / fps);

        for (Particle p : mParticles) {
            p.update(fps);
        }
        // if particle effect has run it's allotted time, stop
        if (mDuration < 0) {
            mIsRunning = false;
        }
    }

    // sets each Particle instance running, called each time effect needs to be started
    void emitParticles(PointF startPosition) {
        mIsRunning = true;
        // System lasts for 30 seconds
        mDuration = 30f;

        // System lasts for 3 seconds
        //mDuration = 3f;

        for (Particle p : mParticles) {
            p.setPosition(startPosition);
        }
    }

    void draw(Canvas canvas, Paint paint) {
        for (Particle p : mParticles) {
            // colored particles
            //paint.setARGB(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

            // white particles
            paint.setColor(Color.argb(255, 255, 255, 255));

            // particles sizes
            float sizeX = 0;
            float sizeY = 0;

            // Big particles
            //sizeX = 25;
            //sizeY = 25;

            // Medium particles
            //sizeX = 10;
            //sizeY = 10;

            // Tiny particles
            sizeX = 1;
            sizeY = 1;

            // Draw the particles
            // Square particles
            //canvas.drawRect(p.getPosition().x, p.getPosition().y, p.getPosition().x + sizeX, p.getPosition().y + sizeY, paint);

            // Circular particle
            canvas.drawCircle(p.getPosition().x, p.getPosition().y, sizeX, paint);
        }
    }
}
