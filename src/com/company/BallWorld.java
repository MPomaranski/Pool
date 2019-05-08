package com.company;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class BallWorld extends JPanel {
   private static final int UPDATE_RATE = 30;    // Frames per second (fps)
   private static final float EPSILON_TIME = 1e-2f;  // Threshold for zero time
   
   // Balls
   private static final int MAX_BALLS = 25; // Max number allowed 
   private int currentNumBalls;             // Number currently active
   private Ball[] balls = new Ball[MAX_BALLS];

   private ContainerBox box;     // The container rectangular box
   private DrawCanvas canvas;    // The Custom canvas for drawing the box/ball
   private int canvasWidth;
   private int canvasHeight;

   private ControlPanel control; // The control panel of buttons and sliders.
   private boolean paused = false;  // Flag for pause/resume control

   public BallWorld(int width, int height) {
      final int controlHeight = 30;    
      canvasWidth = width;
      canvasHeight = height - controlHeight;
      currentNumBalls = 15;
      balls[0] = new Ball(350, 150, 14.5f, 0, 34, Color.YELLOW);
      balls[1] = new Ball(320, 150, 14.5f, 0, -114, Color.YELLOW);
      balls[2] = new Ball(290, 150, 14.5f, 0, 14, Color.GREEN);
      balls[3] = new Ball(260, 150, 14.5f, 0, 14, Color.GREEN);
      balls[4] = new Ball(230, 150, 14.5f, 0, -47, Color.PINK);
      balls[5] = new Ball(335, 175, 14.5f, 0, 47, Color.PINK);
      balls[6] = new Ball(305, 175, 14.5f, 0, -114, Color.ORANGE);
      balls[7] = new Ball(275, 175, 14.5f, 0, 60, Color.ORANGE);
      balls[8] = new Ball(245, 175, 14.5f, 0, -42, Color.BLUE);
      balls[9] = new Ball(320, 200, 14.5f, 0, -84, Color.CYAN);
      balls[10] = new Ball(290, 200, 14.5f, 0, -42, Color.YELLOW);
      balls[11] = new Ball(260, 200, 14.5f, 0, -42, Color.ORANGE);
      balls[12] = new Ball(305, 225, 14.5f, 0, -42, Color.MAGENTA);
      balls[13] = new Ball(275, 225, 14.5f, 0, -42, Color.PINK);
      balls[14] = new Ball(290, 250, 14.5f, 0, -42, Color.CYAN);

      // The rest of the balls, that can be launched using the launch button
      for (int i = currentNumBalls; i < MAX_BALLS; i++) {
         // Allocate the balls, but position later before the launch
         balls[i] = new Ball(20, canvasHeight - 20, 15, 50, 45, Color.RED);
      }

      // Init the Container Box to fill the screen
      box = new ContainerBox(0, 0, canvasWidth, canvasHeight, Color.BLACK, Color.WHITE);

      // Init the custom drawing panel for drawing the game
      canvas = new DrawCanvas();

      // Init the control panel
      control = new ControlPanel();
   
      // Layout the drawing panel and control panel
      this.setLayout(new BorderLayout());
      this.add(canvas, BorderLayout.CENTER);
      this.add(control, BorderLayout.SOUTH);
      
      // Handling window resize. Adjust container box to fill the screen.
      this.addComponentListener(new ComponentAdapter() {
         // Called back for first display and subsequent window resize.
         @Override
         public void componentResized(ComponentEvent e) {
            Component c = (Component)e.getSource();
            Dimension dim = c.getSize();
            canvasWidth = dim.width;
            canvasHeight = dim.height - controlHeight; // Leave space for control panel
            // Need to resize all components that is sensitive to the screen size.
            box.set(0, 0, canvasWidth, canvasHeight);
         }
      });
       
      // Start the ball bouncing
      gameStart();
   }

   public void gameStart() {
      // Run the game logic in its own thread.
      Thread gameThread = new Thread() {
         public void run() {
            while (true) {
               long beginTimeMillis, timeTakenMillis, timeLeftMillis;
               beginTimeMillis = System.currentTimeMillis();
               
               if (!paused) {
                  // Execute one game step
                  gameUpdate();
                  // Refresh the display
                  repaint();
               }
               
               // Provide the necessary delay to meet the target rate
               timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
               timeLeftMillis = 1000L / UPDATE_RATE - timeTakenMillis;
               if (timeLeftMillis < 5) timeLeftMillis = 5; // Set a minimum
               
               // Delay and give other thread a chance
               try {
                  Thread.sleep(timeLeftMillis);
               } catch (InterruptedException ex) {}
            }
         }
      };
      gameThread.start();  // Invoke GaemThread.run()
   }
   public void gameUpdate() {
      float timeLeft = 1.0f;  // One time-step to begin with
      
      // Repeat until the one time-step is up 
      do {
         // Find the earliest collision up to timeLeft among all objects
         float tMin = timeLeft;
         
         // Check collision between two balls
         for (int i = 0; i < currentNumBalls; i++) {
            for (int j = 0; j < currentNumBalls; j++) {
               if (i < j) {
                  balls[i].intersect(balls[j], tMin);
                  if (balls[i].earliestCollisionResponse.t < tMin) {
                     tMin = balls[i].earliestCollisionResponse.t;
                  }
               }
            }
         }
         // Check collision between the balls and the box
         for (int i = 0; i < currentNumBalls; i++) {
            balls[i].intersect(box, tMin);
            if (balls[i].earliestCollisionResponse.t < tMin) {
               tMin = balls[i].earliestCollisionResponse.t;
            }
         }
   
         // Update all the balls up to the detected earliest collision time tMin,
         // or timeLeft if there is no collision.
         for (int i = 0; i < currentNumBalls; i++) {
            balls[i].update(tMin);
         }
   
         timeLeft -= tMin;                // Subtract the time consumed and repeat
      } while (timeLeft > EPSILON_TIME);  // Ignore remaining time less than threshold
   }

   class DrawCanvas extends JPanel {

      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g);    // Paint background
         // Draw the balls and box
         box.draw(g);
         for (int i = 0; i < currentNumBalls; i++) {
            balls[i].draw(g);
         }
         // Display balls' information
         g.setColor(Color.WHITE);
         g.setFont(new Font("Courier New", Font.PLAIN, 12));
         for (int i = 0; i < currentNumBalls; i++) {
            //g.drawString("Ball " + (i+1) + " " + balls[i].toString(), 20, 30 + i*20);
         }
      }
      @Override
      public Dimension getPreferredSize() {
         return (new Dimension(canvasWidth, canvasHeight));
      }
   }
   class ControlPanel extends JPanel {

      public ControlPanel() {
         // A checkbox to toggle pause/resume movement
         JCheckBox pauseControl = new JCheckBox();
         this.add(new JLabel("Pause"));
         this.add(pauseControl);
         pauseControl.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
               paused = !paused;  // Toggle pause/resume flag
               transferFocusUpCycle();  // To handle key events
            }
         });

         final float[] ballSavedSpeedXs = new float[MAX_BALLS];
         final float[] ballSavedSpeedYs = new float[MAX_BALLS];
         for (int i = 0; i < currentNumBalls; i++) {
            ballSavedSpeedXs[i] = balls[i].speedX;
            ballSavedSpeedYs[i] = balls[i].speedY;
         }
         int minFactor = 5;    // percent
         int maxFactor = 200;  // percent
         JSlider speedControl = new JSlider(JSlider.HORIZONTAL, minFactor, maxFactor, 100);
         this.add(new JLabel("Speed"));
         this.add(speedControl);
         speedControl.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
               JSlider source = (JSlider)e.getSource();
               if (!source.getValueIsAdjusting()) {
                  int percentage = (int)source.getValue();
                  for (int i = 0; i < currentNumBalls; i++) {
                     balls[i].speedX = ballSavedSpeedXs[i] * percentage / 100.0f;
                     balls[i].speedY = ballSavedSpeedYs[i] * percentage / 100.0f;
                  }
               }
               transferFocusUpCycle();  // To handle key events
            }
         });
         // A button for launching the remaining balls
         final JButton launchControl = new JButton("Launch New Ball");
         this.add(launchControl);
         launchControl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if (currentNumBalls < MAX_BALLS) {
                  balls[currentNumBalls].x = 20;
                  balls[currentNumBalls].y = canvasHeight - 20;
                  currentNumBalls++;
                  if (currentNumBalls == MAX_BALLS) {
                     // Disable the button, as there is no more ball
                     launchControl.setEnabled(false);
                  }
               }
               transferFocusUpCycle();  // To handle key events
            }
         });
      }
   }
}
