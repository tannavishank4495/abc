package com.example.parrot.pong1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // -----------------------------------
    // ## ANDROID DEBUG VARIABLES
    // -----------------------------------

    // Android debug variables
    final static String TAG="PONG-GAME";

    // -----------------------------------
    // ## SCREEN & DRAWING SETUP VARIABLES
    // -----------------------------------

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;


    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;



    // -----------------------------------
    // ## GAME SPECIFIC VARIABLES
    // -----------------------------------

    // ----------------------------
    // ## SPRITES
    // ----------------------------
    int ballXPosition;      // keep track of ball -x
    int ballYPosition;      // keep track of ball -y

    int racketXPosition;  // top left corner of the racket
    int racketYPosition;  // top left corner of the racket

    // ----------------------------
    // ## GAME STATS - number of lives, score, etc
    // ----------------------------
    int score = 0;

    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;


        this.printScreenInfo();

        // @TODO: Add your sprites to this section
        // This is optional. Use it to:
        //  - setup or configure your sprites
        //  - set the initial position of your sprites
        this.ballXPosition = this.screenWidth / 2;
        this.ballYPosition = this.screenHeight / 2;


        // Setup the initial position of the racket
        this.racketXPosition = 350;
        this.racketYPosition = 1200;

        // @TODO: Any other game setup stuff goes here


    }

    // ------------------------------
    // HELPER FUNCTIONS
    // ------------------------------

    // This funciton prints the screen height & width to the screen.
    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }


    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------


    String directionBallIsMoving = "down";
    String personTapped="";

    // 1. Tell Android the (x,y) positions of your sprites
    public void updatePositions() {
        // @TODO: Update the position of the sprites

        if (directionBallIsMoving == "down") {
            this.ballYPosition = this.ballYPosition + 10;

            // if ball hits the floor, then change its direciton
            if (this.ballYPosition >= this.screenHeight) {
                //Log.d(TAG, "BALL HIT THE FLOOR / OUT OF BOUNDS");
                directionBallIsMoving = "up";
            }
        }
        if (directionBallIsMoving == "up") {
            this.ballYPosition = this.ballYPosition - 10;

            // if ball hits ceiling, then change directions
            if (this.ballYPosition <= 0 ) {
                // hit upper wall
                //Log.d(TAG,"BALL HIT CEILING / OUT OF BOUNDS ");
                directionBallIsMoving = "down";
            }
        }


        // calculate the racket's new position
        if (personTapped.contentEquals("right")){
            this.racketXPosition = this.racketXPosition + 10;
        }
        else if (personTapped.contentEquals("left")){
            this.racketXPosition = this.racketXPosition - 10;
        }


        // @TODO: Collision detection code


        // detect when ball hits the racket
        // ---------------------------------

        // 1. if ball hits racket, bounce off racket
        if (ballYPosition >= this.racketYPosition) {
            // ball is touching racket
            Log.d(TAG, "Ball IS TOUCHING RACKET!");
            directionBallIsMoving = "up";

            // increase the game score!
            this.score = this.score + 50;
        }


        // 2. if ball misses racket, then keep going down

        // 3. if ball falls off bottom of screen, restart the ball in middle
    }

    // 2. Tell Android to DRAW the sprites at their positions
    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------
            // Put all your drawing code in this section

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,0,255));
            paintbrush.setColor(Color.WHITE);

            //@TODO: Draw the sprites (rectangle, circle, etc)

            // 1. Draw the ball
            this.canvas.drawRect(
                    ballXPosition,
                    ballYPosition,
                    ballXPosition + 50,
                    ballYPosition + 50,
                    paintbrush);

            // 2. Draw the racket

            paintbrush.setColor(Color.YELLOW);
            this.canvas.drawRect(this.racketXPosition,
                    this.racketYPosition,
                    this.racketXPosition + 400,     // 400 is width of racket
                    this.racketYPosition + 50,    // 50 is height of racket
                    paintbrush);
            paintbrush.setColor(Color.WHITE);


            // this.canvas.drawRect(left, top, right, bottom, paintbrush);



            //@TODO: Draw game statistics (lives, score, etc)
            paintbrush.setTextSize(60);
            canvas.drawText("Score: " + this.score, 20, 100, paintbrush);

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    // Sets the frame rate of the game
    public void setFPS() {
        try {
            gameThread.sleep(50);
        }
        catch (Exception e) {

        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        // ---------------------------------------------------------
        // Get position of the tap
        // Compare position of tap to the middle of the screen
        // If tap is on left, move racket Position to left
        // If tap is on right, move racket position to right

        if (userAction == MotionEvent.ACTION_DOWN) {
            // user pushed down on screen

            // 1. Get position of tap
            float fingerXPosition = event.getX();
            float fingerYPosition = event.getY();
            Log.d(TAG, "Person's pressed: "
                    + fingerXPosition + ","
                    + fingerYPosition);


            // 2. Compare position of tap to middle of screen
            int middleOfScreen = this.screenWidth / 2;
            if (fingerXPosition <= middleOfScreen) {
                // 3. If tap is on left, racket should go left
                personTapped = "left";
            }
            else if (fingerXPosition > middleOfScreen) {
                // 4. If tap is on right, racket should go right
                personTapped = "right";
            }
        }
        else if (userAction == MotionEvent.ACTION_UP) {
            // user lifted their finger
        }
        return true;
    }
}