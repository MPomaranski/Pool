package com.company;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFullScreenOnly extends JFrame {

   public MainFullScreenOnly() {
      // Find out the current display width and height
      
      // Get the default graphic device and try full screen mode
      GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
      if (device.isFullScreenSupported()) { // Go for full-screen mode
         this.setUndecorated(true);         // Don't show title and border
         this.setResizable(false);
         //this.setIgnoreRepaint(true);     // Ignore OS re-paint request
         device.setFullScreenWindow(this);
      } else {    // Run in windowed mode if full screen is not supported
         Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
         this.setSize(dim.width, dim.height - 40); // minus task bar
         this.setResizable(true);
      }
      
      // Allocate the game panel to fill the current screen 
      BallWorld ballWorld = new BallWorld(this.getWidth(), this.getHeight());
      this.setContentPane(ballWorld); // Set as content pane for this JFrame

      // To handle key events
      this.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
            case KeyEvent.VK_ESCAPE:   // ESC to quit
               System.exit(0);
               break;
            }
         }
      });
      this.setFocusable(true);  // To receive key event

      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setTitle("A World of Balls");
      this.pack();            // Pack to preferred size
      this.setVisible(true);  // Show it
   }

   public static void main(String[] args) {
      // Run UI in the Event Dispatcher Thread (EDT), instead of Main thread
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            new MainFullScreenOnly();
         }
      });
   }
}
