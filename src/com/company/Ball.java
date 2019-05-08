package com.company;

import java.awt.*;
import java.util.Formatter;

public class Ball {
   float x, y;           // Ball's center x and y (package access)
   float speedX, speedY; // Ball's speed per step in x and y (package access)
   float radius;         // Ball's radius (package access)
   private Color color;  // Ball's color
   private static final Color DEFAULT_COLOR = Color.BLUE;
   
   // For collision detection and response
   // Maintain the response of the earliest collision detected 
   //  by this ball instance. Only the first collision matters! (package access)
   CollisionResponse earliestCollisionResponse = new CollisionResponse();

   public Ball(float x, float y, float radius, float speed, float angleInDegree,
         Color color) {
      this.x = x;
      this.y = y;
      // Convert (speed, angle) to (x, y), with y-axis inverted
      this.speedX = (float)(speed * Math.cos(Math.toRadians(angleInDegree)));
      this.speedY = (float)(-speed * (float)Math.sin(Math.toRadians(angleInDegree)));
      this.radius = radius;
      this.color = color;
   }
   public Ball(float x, float y, float radius, float speed, float angleInDegree) {
      this(x, y, radius, speed, angleInDegree, DEFAULT_COLOR);
   }

   // Working copy for computing response in intersect(), 
   // to avoid repeatedly allocating objects.
   private CollisionResponse tempResponse = new CollisionResponse(); 

   public void intersect(ContainerBox box, float timeLimit) {
      // Call movingPointIntersectsRectangleOuter, which returns the 
      // earliest collision to one of the 4 borders, if collision detected.
      CollisionPhysics.pointIntersectsRectangleOuter(x, y, speedX, speedY, radius,
            box.minX, box.minY, box.maxX, box.maxY, timeLimit, tempResponse);
      if (tempResponse.t < earliestCollisionResponse.t) {
         earliestCollisionResponse.copy(tempResponse);
      }
   }
   
   // Working copy for computing response in intersect(Ball, timeLimit), 
   // to avoid repeatedly allocating objects.
   private CollisionResponse thisResponse = new CollisionResponse(); 
   private CollisionResponse anotherResponse = new CollisionResponse(); 

   public void intersect(Ball another, float timeLimit) {
      CollisionPhysics.pointIntersectsMovingPoint(
            this.x, this.y, this.speedX, this.speedY, this.radius,
            another.x, another.y, another.speedX, another.speedY, another.radius,
            timeLimit, thisResponse, anotherResponse);
      
      if (anotherResponse.t < another.earliestCollisionResponse.t) {
            another.earliestCollisionResponse.copy(anotherResponse);
      }
      if (thisResponse.t < this.earliestCollisionResponse.t) {
            this.earliestCollisionResponse.copy(thisResponse);
      }
   }
   public void update(float time) {
      // Check if this ball is responsible for the first collision?
      if (earliestCollisionResponse.t <= time) { // FIXME: threshold?
         // This ball collided, get the new position and speed
         this.x = earliestCollisionResponse.getNewX(this.x, this.speedX);
         this.y = earliestCollisionResponse.getNewY(this.y, this.speedY);
         this.speedX = earliestCollisionResponse.newSpeedX;
         this.speedY = earliestCollisionResponse.newSpeedY;
      } else {
         // This ball does not involve in a collision. Move straight.
         this.x += this.speedX * time;
         this.y += this.speedY * time;
         if (this.speedY < 0 && this.speedX < 0) {
            this.speedX += 0.01f;
            this.speedY += 0.01f;
         } else if (this.speedY < 0 && this.speedX > 0){
            this.speedX -= 0.01f;
            this.speedY += 0.01f;
         } else if (this.speedY > 0 && this.speedX > 0){
            this.speedX -= 0.01f;
            this.speedY -= 0.01f;
         } else if (this.speedY > 0 && this.speedX < 0){
            this.speedX += 0.01f;
            this.speedY -= 0.01f;
         }
      }
      // Clear for the next collision detection
      earliestCollisionResponse.reset();
   }

   public void draw(Graphics g) {
      g.setColor(color);
      g.fillOval((int)(x - radius), (int)(y - radius), (int)(2 * radius),
            (int)(2 * radius));
   }

   public float getSpeed() {
      return (float)Math.sqrt(speedX * speedX + speedY * speedY);
   }

   public float getMoveAngle() {
      return (float)Math.toDegrees(Math.atan2(-speedY, speedX));
   }

   public float getMass() {
      return radius * radius * radius / 1000f;
   }

   public float getKineticEnergy() {
      return 0.5f * getMass() * (speedX * speedX + speedY * speedY);
   }

   public String toString() {
      sb.delete(0, sb.length());
      formatter.format("@(%3.0f,%3.0f) r=%3.0f V=(%3.0f,%3.0f) " +
            "S=%4.1f \u0398=%4.0f KE=%3.0f", 
            x, y, radius, speedX, speedY, getSpeed(), getMoveAngle(),
            getKineticEnergy());  // \u0398 is theta
      return sb.toString();
   }
   private StringBuilder sb = new StringBuilder();
   private Formatter formatter = new Formatter(sb);

}
