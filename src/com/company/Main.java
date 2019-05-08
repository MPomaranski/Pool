package com.company;

import javax.swing.JFrame;


public class Main {
   // Entry main program
   public static void main(String[] args) {
      // Run UI in the Event Dispatcher Thread (EDT), instead of Main thread
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            JFrame frame = new JFrame("A World of Balls");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new BallWorld(480, 640)); // BallWorld is a JPanel
            frame.pack();            // Preferred size of BallWorld
            frame.setVisible(true);  // Show it
         }
      });
   }
}
