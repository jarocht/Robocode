package gvsu;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * AwesomeBot - a robot by (your name here)
 */
public class AwesomeBot extends Robot
{
	Radar radar = new Radar();
	public double count =0; //for testing how often enemy fires
	private double eEnergy;
	private boolean move;
	/**
	 * run: AwesomeBot's default behavior
	 */
	public void run() {
		//detaches radar from robot and gun movement
		radar.detach();

		// Initialization of the robot should be put here
		eEnergy = 0;
		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		 setColors(Color.pink,Color.pink,Color.pink); // body,gun,radar

		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			if (move){
				move = false;
				ahead(100);
			} else {			
				back(100);
			}
			turnRadarRight(360);
			//turnGunRight(355);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		if (eEnergy == 0){
			eEnergy = e.getEnergy();
			//each bullet fired (with walls) uses 2 energy
		} else if (eEnergy - e.getEnergy() == 2){
			count++;
			System.out.println(count);
			System.out.println("shot fired!");
			move = true;
		} eEnergy = e.getEnergy();	
		
		// Calculate exact location of the robot
		double absoluteBearing = getHeading() + e.getBearing();
		System.out.println(absoluteBearing);
		//calculate next likely corner here
		double bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());

		// If it's close enough, fire!
		if (Math.abs(bearingFromRadar) <= 20) {
			turnRadarRight(bearingFromRadar);
			// We check gun heat here, because calling fire()
			// uses a turn, which could cause us to lose track
			// of the other robot.
			if (getGunHeat() == 0) {
				fire(Math.min(3 - Math.abs(bearingFromRadar), getEnergy() - .1));
			}
		} // otherwise just set the gun to turn.
		// Note:  This will have no effect until we call scan()
		else {
			turnRadarRight(bearingFromRadar);
		}
		// Generates another scan event if we see a robot.
		// We only need to call this if the gun (and therefore radar)
		// are not turning.  Otherwise, scan is called automatically.
		if (bearingFromRadar == 0) {
			scan();
		}	

	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		//back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		//back(20);
	}	


	class Radar{
		public void detach(){
			//Radar doesn't turn with robot
			setAdjustRadarForRobotTurn(true);
			//Radar doesn't turn with gun
			setAdjustRadarForGunTurn(true);
		}
		
	
	}
}


