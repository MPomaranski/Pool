package com.company;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFullScreen extends JFrame implements KeyListener {
   // Current display device's width & height
   private int displayWidth;  
   private int displayHeight;
   // Current window's width and height
   private int windowedModeWidth;
   private int windowedModeHeight;

   private GraphicsDevice device;  // For entering full screen mode
   private boolean fullScreenMode; // full-screen or windowed mode

   /** Constructor to initialize UI */
   public MainFullScreen() {
      // Find out the current display width and height
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      displayWidth = dim.width;
      displayHeight = dim.height;
      windowedModeWidth = displayWidth;
      windowedModeHeight = displayHeight - 40; // minus task bar 
      
      // Get the default graphic device and try full screen mode
      device = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
      if (device.isFullScreenSupported()) { // Go for full-screen mode
         this.setUndecorated(true);         // Don't show title and border
         this.setResizable(false);
         //this.setIgnoreRepaint(true);     // Ignore OS re-paint request
         device.setFullScreenWindow(this);
         fullScreenMode = true;
      } else {                              // Run in windowed mode
         this.setSize(windowedModeWidth, windowedModeHeight);
         this.setResizable(true);
         fullScreenMode = false;
      }
      
      // Allocate and add the game panel
      BallWorld ballWorld = new BallWorld(this.getWidth(), this.getHeight());
      this.setContentPane(ballWorld); // BallWorld is a JPanel

      // To handle key events
      this.addKeyListener(this);
      this.setFocusable(true);

      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setTitle("A World of Balls");
      this.pack();            // Pack to preferred size
      this.setVisible(true);  // Show it
   }
   
   @Override
   public void keyPressed(KeyEvent e) {
      int keyCode = e.getKeyCode();
      switch (keyCode) {
         case KeyEvent.VK_F1: // F1 to toggle between full-screen and windowed modes
            fullScreenMode = !fullScreenMode;
            this.setVisible(false);                    // Hide the display
            if (this.isDisplayable()) this.dispose();  // For changing the decoration
            if (fullScreenMode) {  // Go for full screen mode if supported
               // Save windowed mode width and height for restoration if needed.
               windowedModeWidth = this.getWidth();
               windowedModeHeight = this.getHeight();
               if (device.isFullScreenSupported()) {
                  this.setUndecorated(true);
                  this.setResizable(false);
                  device.setFullScreenWindow(this);
               }
            } else {               // Windowed mode
               this.setUndecorated(false); // Put the title and border back
               device.setFullScreenWindow(null); // Windowed mode
               this.setSize(windowedModeWidth, windowedModeHeight);
               this.setResizable(true);
            }
            this.setVisible(true);  // Show it
            break;
         case KeyEvent.VK_ESCAPE:   // ESC to quit
            System.exit(0);
            break;
      }
   }

   @Override
   public void keyReleased(KeyEvent e) {}

   @Override
   public void keyTyped(KeyEvent e) {}


   public static void main(String[] args) {
      // Run UI in the Event Dispatcher Thread (EDT), instead of Main thread
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            new MainFullScreen();
         }
      });
   }
}
