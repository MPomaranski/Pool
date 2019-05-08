package com.company;

import javax.swing.JApplet;

public class MainApplet extends JApplet {
   @Override
   public void init() {
      // Run UI in the Event Dispatcher Thread (EDT) instead of the main thread.
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            setContentPane(new BallWorld(480, 640)); // BallWorld is a JPanel
         }
      });
   }
}
