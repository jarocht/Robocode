package gvsu;
import robocode.*;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * AwesomeBot - a robot by (your name here)
 */
public class AwesomeBot extends Robot
{
	private double eEnergy;
	private boolean move;
	/**
	 * run: AwesomeBot's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here
		eEnergy = 0;
		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			if (move){
				move = false;
				ahead(100);
			}			
			turnGunRight(360);
			back(100);
			turnGunRight(355);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		if (eEnergy == 0){
			eEnergy = e.getEnergy();
		} else if (eEnergy - e.getEnergy() < 3){
			System.out.println("shot fired!");
			move = true;
		}
		eEnergy = e.getEnergy();
		
		fire(1.1);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
