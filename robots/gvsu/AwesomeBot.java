package gvsu;
import robocode.*;
import robocode.AdvancedRobot;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * AwesomeBot - a robot by (Tim Jaroch and Tyler Hutek)
 */
public class AwesomeBot extends AdvancedRobot {
	//The energy of the enemy
	private double eEnergy = 100;
	
	//The x and y coordinates used to locate the map center
	private double x,y;
	
	//true if AwesomeBot is at the center 
	private boolean center;
	
	//determines how far Awesomebot moves when the enemy fires 
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
			//rotates the radar 360 degrees to locate the enemy
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
			//if the enemy's energy drops by exactly 2 a shot was fired.
			System.out.println("shot fired!");
			//Move to avoid being hit.
			setAhead(move);
			//used to alternate the direction AwesomeBotMoves.
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
		//the coordinates for the center of the Battle Field
		int xCenter, yCenter;
		
		//Determine the center of the field.
		xCenter = (int)(getBattleFieldWidth() / 2.0);
		yCenter = (int)(getBattleFieldHeight() / 2.0);
		
		//Prints out the center of the field
		System.out.println(xCenter + "," + yCenter);	
		
		//While AwesomeBot isn't at the center...
		while (!(Math.ceil(getX()) == xCenter && Math.ceil(getY()) == yCenter)){
			center = false;
			//determine how far left or right Awesomebot needs to move
			x = getX() - xCenter;
			//Move that direction
			turnRight(90.0 - getHeading());
			ahead(-x);
			
			//determine how far up or down the center is
			y = getY() - yCenter;
			
			//move in that direction
			turnRight(180.0 - getHeading());
			ahead(y);
		}
		//AwesomeBot is now at the center
		center = true;
		return center;			
	}
	
	//Get the X and Y coordinates of the enemy
	public void getCoordinates(String side, ScannedRobotEvent e){
		//Get the hypotenuse of the triangle made with the enemy, wall, and Awesomebot
		double hypot = e.getDistance();
		
		//the X coordinate
		double X = 0;
		
		//the Y coordinate
		double Y = 0;
		
		//The length of the adjacent side of the triangle.
		double adjacent;
		
		//The length of the opposite side of the triangle.
		double opposite;
		
		//The velocity of the enemy.
		double enemyVelocity = e.getVelocity();
		
		//The time the enemy needs to reach a corner.
		double enemyTime;
		
		//The distance the enemy is from a corner.
		double cornerDistance;
		
		//The time it would take for a bullet to hit a corner.
		double fireTime;
		
		//The velocity of your bullet
		double bulletVelocity;
		
		//The power of your bullet
		double bulletPower = 3;
		
		//should give the angle of the triangle in degrees
		double angle = (getRadarHeading()); 
		
		//if the enemy is at the top edge
		if(side.equals("top")){
			//get the Y coordinate
			Y =getBattleFieldHeight()-18;
			adjacent = Y-getY(); //getting the adjacent side length by measuring distance from top wall
			opposite = Math.sqrt(Math.pow(hypot,2)-Math.pow(adjacent,2));
			if(angle>270){ //for when the enemy is to the left of you.
				opposite*=-1;
			}
			X = opposite + getX()+16; // 16 added for radar lag 
			enemyTime = (800-X)/enemyVelocity;
			cornerDistance = Math.sqrt(Math.pow((getX()-800),2)+Math.pow((getY()-600),2)); //corner is 0,800
		}

		//if the enemy is on the bottom edge
		else if(side.equals("bottom")){
			//Getting the Y coorinated
			Y =18;
			adjacent = Y-getY(); //getting the adjacent side length by measuring distance from top wall
			opposite = Math.sqrt(Math.pow(hypot,2)-Math.pow(adjacent,2));
			if(angle>180){ //for when the enemy is to the left of you.
				opposite*=-1;
			}
			//Getting X coordinate
			X = opposite + getX()-16; // 16 subtracted for radar lag 
			enemyTime = X/enemyVelocity;
			cornerDistance = Math.sqrt(Math.pow(getX(),2)+Math.pow(getY(),2)); //corner is 0,0
		}
		
		//If the enemy is on the left edge
		else if(side.equals("left")){
			//Getting the X coordinate 
			X =18;
			adjacent = X-getX(); //getting the adjacent side length by measuring distance from top wall

			opposite = Math.sqrt(Math.pow(hypot,2)-Math.pow(adjacent,2));
			if(angle<270){ //for when the enemy is to the left of you.
				opposite*=-1;
			}
			//Getting the Y coordinate
			Y = opposite + getY()+16; // 16 added for radar lag 
			enemyTime = (600-Y)/enemyVelocity;
			cornerDistance = Math.sqrt(Math.pow(getX(),2)+Math.pow((getY()-600),2)); //corner is 0,600
		}
		
		//If the enemy is on the right side.
	 	else if(side.equals("right")){
			//Getting the Y coordinate
			X =getBattleFieldWidth()-18;
			adjacent = X-getX(); //getting the adjacent side length by measuring distance from top wall

			opposite = Math.sqrt(Math.pow(hypot,2)-Math.pow(adjacent,2));
			if(angle>90){ //for when the enemy is to the left of you.
				opposite*=-1;
			}
			//Getting the Y coordinate
			Y = opposite + getY()-16; // 16 subtracted for radar lag 
			enemyTime = Y/enemyVelocity;
			cornerDistance = Math.sqrt(Math.pow((getX()-800),2)+Math.pow(getY(),2)); //corner is 800,0
		}
		//If the corner can't be determined, set the coordinates so AwesomeBot won't fire
	else{
		cornerDistance = 900;
		enemyTime = 999999;
	}

		//coordinates are calculated at this point
		

		//Determine bulletVelocity
		bulletVelocity = 20-3*bulletPower;
		
		//Determine the time for the bullet to hit a coner
		fireTime = cornerDistance/bulletVelocity;
		System.out.println("Bullet Time: "+fireTime + "enemy Time: " + enemyTime);
		//getting ratio for how long a bullet will take to reach a corner vs
		// how long the enemy will take to reach a corner.
		double fireRatio = fireTime/enemyTime;
		
		//setting a tolerance for the fire ratio.
		double tolerance = .03;
		
		//if the ratio falls within the tollerance, fire.
		if(fireRatio >(1-tolerance) && fireRatio<(1+tolerance)){
		System.out.println("ratio: " + fireRatio);
		System.out.println("FIRE!");
		fire(bulletPower);
		}
		 
	

	}
}


