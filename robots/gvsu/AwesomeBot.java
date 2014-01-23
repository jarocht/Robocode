package gvsu;
import robocode.*;
import robocode.AdvancedRobot;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * AwesomeBot - a robot by (your name here)
 */
public class AwesomeBot extends AdvancedRobot
{
	//Radar radar = new Radar();
	public double count = 0; //for testing how often enemy fires
	private double eEnergy = 100;
	private double x,y;
	/**
	 * run: AwesomeBot's default behavior
	 */
	public void run() {
		//detaches radar from robot and gun movement
		detach(true);
		// Initialization of the robot should be put here
		 setColors(Color.GRAY,Color.RED,Color.ORANGE); // body,gun,radar
		
		//move to center of map
		x = getX() - (getBattleFieldWidth() / 2.0);
		turnRight(90.0 - getHeading());
		ahead(-x);
		y = getY() - (getBattleFieldHeight() / 2.0);
		turnRight(180.0 - getHeading());
		ahead(y);
		
		// Robot main loop
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			turnRadarRight(360);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		
		//each bullet fired (with walls) uses 2 energy
		if (eEnergy - e.getEnergy() == 2){
			count++;
			//System.out.println(count);
			System.out.println("shot fired!");
			//Calculate movement here and use setAhead(x);
			//SetTurnRight(x); or SetTurnRight(-x);
			setAhead(100);
			setTurnRight(90.0);
		} eEnergy = e.getEnergy();	
		
		// Calculate exact location of the robot
		double absoluteBearing = getHeading() + e.getBearing(); //System.out.println(absoluteBearing);
		double bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
		turnRadarRight(bearingFromRadar);
				
		//calculate next likely corner here
		System.out.println("Headed to:"+nextCornerHeading(e));		
		//Turn gun to that corner
		double cBearing; //Trig HERE
		
		double bearingFromGun = normalRelativeAngleDegrees(cBearing - getGunHeading());
		// If it's close enough, fire!
		if (Math.abs(bearingFromRadar) <= 20) {
			turnRadarRight(bearingFromRadar);
			// We check gun heat here, because calling fire()
			// uses a turn, which could cause us to lose track
			// of the other robot.
			if (getGunHeat() == 0) {
				//fire(Math.min(3 - Math.abs(bearingFromRadar), getEnergy() - .1));
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

	public void detach(Boolean var){
		//Radar doesn't turn with robot
		setAdjustRadarForRobotTurn(var);
		//Radar doesn't turn with gun
		setAdjustRadarForGunTurn(var);
	}
	public double nextCornerHeading(ScannedRobotEvent e){
		switch((int)e.getHeading()){
			case 0:
			case 360:
				return Math.toDegrees(Math.atan( (getY()-0) / (getX()-0) ) );
			case 90:
				return Math.toDegrees(Math.atan((getY()-0)/(getX()-getBattleFieldWidth()));
			case 180:
				return	Math.toDegrees(Math.atan((getY()-getBattleFieldHeight()) / (getX()-getBattleFieldWidth()));
			case 270:
				return Math.toDegrees(Math.atan((getY()-getBattleFieldHeight())/(getX()-0)));
			default:
				return -1;
		}	

	}
	public String nextCorner(ScannedRobotEvent e){
		switch((int)e.getHeading()){
			case 0:
			case 360:
				return "Top Left";
			case 90:
				return "Top Right";
			case 180:
				return "Bottom Right";
			case 270:
				return "Bottom Left";
			default:
				return "In Transition!";
		}		

	}
}


