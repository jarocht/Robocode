package gvsu;
import robocode.*;
import robocode.AdvancedRobot;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * AwesomeBot - a robot by (your name here)
 */
public class AwesomeBot extends AdvancedRobot {
	private double eEnergy = 100;
	private double x,y;
	private boolean center;
	private int move = 100;
	/**
	 * run: AwesomeBot's default behavior
	 */
	public void run() {
		//detaches radar from robot and gun movement
		detach(true);
		// Initialization of the robot should be put here
		setColors(Color.GRAY,Color.RED,Color.ORANGE); // body,gun,radar
		//move to center of map
		moveToCenter();
		
		while(true) {
			turnRadarRight(360);
		}
	}

	/**
	 * 
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		if (!center) return;		
		//Always be perpendicular to enemy travel
		setTurnRight(90 + e.getBearing()); 	
		//Walls always fires shots that use 2 energy
		if (eEnergy - e.getEnergy() == 2){ 
			System.out.println("shot fired!");
			setAhead(move);
			move *= -1;
		} 
		eEnergy = e.getEnergy(); //Update enemy energy to prevent fire mis-detection
		
		//Get enemy bearing from our heading
		double absoluteBearing = getHeading() + e.getBearing();
		// Follow Target with Radar
		turnRadarRight(normalRelativeAngleDegrees(absoluteBearing - getRadarHeading()));
		if (eEnergy  == 0.0){//Enemy is disabled, turn gun and fire
			double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
			turnGunRight(bearingFromGun);
			fire(1.0);
		} else {//Enemy moving toward corner, target predicted corner
			turnGunRight(nextCornerHeading(e) - getGunHeading());
		}		
		
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		moveToCenter();
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
				//System.out.println("Top Left");

				getCoordinates("left", e);
				return Math.toDegrees(Math.atan( (getBattleFieldHeight()-getY()) / (getX()-0.0))) + 270.0;

			case 90:
				//System.out.println("Top Right");

				getCoordinates("top", e);
				return 90 - Math.toDegrees(Math.atan( (getBattleFieldHeight()-getY()) / (getBattleFieldWidth()-getX())));

			case 180:
				//System.out.println("Bottom Right");

				getCoordinates("right", e);
				return Math.toDegrees(Math.atan( (getY()-0.0) / (getBattleFieldWidth()-getX()))) + 90.0;

			case 270:
				//System.out.println("Bottom Left");

				getCoordinates("bottom", e);
				return 270 - Math.toDegrees(Math.atan( (getY()-0.0) / (getX()-0.0)));

			default:
				//System.out.println("In Transition!");
				return getGunHeading();
		}	

	}
	
	public boolean moveToCenter(){
		int xCenter, yCenter;
		xCenter = (int)(getBattleFieldWidth() / 2.0);
		yCenter = (int)(getBattleFieldHeight() / 2.0);
		System.out.println(xCenter + "," + yCenter);	
		while (!(Math.ceil(getX()) == xCenter && Math.ceil(getY()) == yCenter)){
			center = false;
			x = getX() - xCenter;
			turnRight(90.0 - getHeading());
			ahead(-x);
			y = getY() - yCenter;
			turnRight(180.0 - getHeading());
			ahead(y);
		}
		center = true;
		return center;			
	}
	
	public void getCoordinates(String side, ScannedRobotEvent e){
		double hypot = e.getDistance();

		double X = 0;
		double Y = 0;
		double adjacent;
		double opposite;
		double enemyVelocity = e.getVelocity();
		double enemyTime;
		double cornerDistance;
		double fireTime;
		double bulletVelocity;
		double bulletPower = 3;
		//System.out.println(angle);
		double angle = (getRadarHeading()); //should give the angle of the triangle in degrees
		if(side.equals("top")){
			Y =getBattleFieldHeight()-18;
			adjacent = Y-getY(); //getting the adjacent side length by measuring distance from top wall
			opposite = Math.sqrt(Math.pow(hypot,2)-Math.pow(adjacent,2));
			if(angle>270){ //for when the enemy is to the left of you.
				opposite*=-1;
			}
			X = opposite + getX()+16;
			//System.out.println(X + "," + Y);
			enemyTime = (800-X)/enemyVelocity;
			cornerDistance = Math.sqrt(Math.pow((getX()-800),2)+Math.pow((getY()-600),2)); //corner is 0,0
		}

		else if(side.equals("bottom")){
			Y =18;
			adjacent = Y-getY(); //getting the adjacent side length by measuring distance from top wall
			opposite = Math.sqrt(Math.pow(hypot,2)-Math.pow(adjacent,2));
			if(angle>180){ //for when the enemy is to the left of you.
				opposite*=-1;
			}
			X = opposite + getX()-16;
			//System.out.println(X + "," + Y);
			enemyTime = X/enemyVelocity;
			cornerDistance = Math.sqrt(Math.pow(getX(),2)+Math.pow(getY(),2)); //corner is 0,0
		}
		
		else if(side.equals("left")){
			X =18;
			adjacent = X-getX(); //getting the adjacent side length by measuring distance from top wall

			opposite = Math.sqrt(Math.pow(hypot,2)-Math.pow(adjacent,2));
			if(angle<270){ //for when the enemy is to the left of you.
				opposite*=-1;
			}
			Y = opposite + getY()+16;
			//System.out.println(X + "," + Y);
			enemyTime = (600-Y)/enemyVelocity;
			cornerDistance = Math.sqrt(Math.pow(getX(),2)+Math.pow((getY()-600),2)); //corner is 0,600
		}
		
	 else if(side.equals("right")){
			X =getBattleFieldWidth()-18;
			adjacent = X-getX(); //getting the adjacent side length by measuring distance from top wall

			opposite = Math.sqrt(Math.pow(hypot,2)-Math.pow(adjacent,2));
			if(angle>90){ //for when the enemy is to the left of you.
				opposite*=-1;
			}
			Y = opposite + getY()-16; // 16 subtracted for gun 
			//System.out.println(X + "," + Y);
			enemyTime = Y/enemyVelocity;
			cornerDistance = Math.sqrt(Math.pow((getX()-800),2)+Math.pow(getY(),2)); //corner is 800,0
		}
	else{
		cornerDistance = 900;
		enemyTime = 999999;
	}

		//coordinates are calculated at this point
		bulletVelocity = 20-3*bulletPower;
		fireTime = cornerDistance/bulletVelocity;
		System.out.println("Bullet Time: "+fireTime + "enemy Time: " + enemyTime);
		double fireRatio = fireTime/enemyTime;
		
		double tolerance = .02;
		if(fireRatio >(1-tolerance) && fireRatio<(1+tolerance)){
		System.out.println("ratio: " + fireRatio);
		System.out.println("FIRE!");
		fire(bulletPower);
		}
		 
	

	}
}


