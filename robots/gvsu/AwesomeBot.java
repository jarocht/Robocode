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
		ahead(y - 65);
		
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
		} 
		eEnergy = e.getEnergy();	
		double absoluteBearing = getHeading() + e.getBearing();
		if (eEnergy < 3){
				double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
				turnGunRight(bearingFromGun);
				fire(2);
		}

		// Calculate exact location of the robot
		double bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
		turnRadarRight(bearingFromRadar);
				
		//Turn gun to that corner
		double cornerHeading = nextCornerHeading(e); //Trig HERE
		double bearingFromGun = cornerHeading - getGunHeading();
		if (bearingFromGun != 0)
			turnGunRight(bearingFromGun);
		else
			fire(2);

		if (bearingFromRadar == 0) {
			scan();
		}	
		
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		setAhead(-100);
		setTurnLeft(90);
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
		//Gun doesn't turn with Robot
		setAdjustGunForRobotTurn(var);
	}
	public double nextCornerHeading(ScannedRobotEvent e){
		switch((int)e.getHeading()){
			case 0:
			case 360:
				System.out.println("Top Left");
				return Math.toDegrees(Math.atan( (getBattleFieldHeight()-getY()) / (getX()-0.0))) + 270.0;
			case 90:
				System.out.println("Top Right");
				return 90 - Math.toDegrees(Math.atan( (getBattleFieldHeight()-getY()) / (getBattleFieldWidth()-getX())));
			case 180:
				System.out.println("Bottom Right");
				return Math.toDegrees(Math.atan( (getY()-0.0) / (getBattleFieldWidth()-getX()))) + 90.0;
			case 270:
				System.out.println("Bottom Left");
				return 270 - Math.toDegrees(Math.atan( (getY()-0.0) / (getX()-0.0)));
			default:
				System.out.println("In Transition!");
				return getGunHeading();
		}	

	}
}


