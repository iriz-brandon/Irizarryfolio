package solarstriker.views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;

import solarstriker.controller.SSBulletController;
import solarstriker.model.SSEnemyHandler;
import solarstriker.model.SSMainModel;
import solarstriker.model.SSShipHandler;
import solarstriker.views.animation.AnimationEngine;
import solarstriker.views.animation.SpriteLoader;

public class SSEnemyView extends JPanel {
	private static final long serialVersionUID = -7757934736703718800L;
	private HashMap<Integer, AffineTransform> rotates;
	private BufferedImage refImage;
	private SSMainModel model;
	private SSMainView view;
	private Polygon bounds;
	private int enemyID;
	private int angle = 0;
	private int fireBuffer = 50;
	private int direction = 0;
	private int health = 0;
	private boolean dying = false, hasChecked = false;
	private boolean isBoss = false;

	private int BULLET_SIZE;
	private AnimationEngine death;
	private SSShipHandler ship;
	private SSBulletController bulletController;
	
	public SSEnemyView(SSMainModel model, SSMainView view, boolean isBoss){
		this.model = model;
		this.view = view;
		this.isBoss = isBoss;
		rotates = model.getRotates();
		
		setup();
	}
		
	public void setup() {
		List<Integer> next = new ArrayList<Integer>();

		Random random = new Random();
		if(isBoss) {
			enemyID = model.getEnemyHandler().getCurrentBoss();
			
			refImage = SpriteLoader.getBossImage(enemyID);
		} else {
			for(int j = 0; j < Mob.values().length; j++) {
				Mob mob = Mob.values()[j];
				if(model.getScoreCount() >= mob.getPointReq() && view.getGlobalTicks() >= mob.getTimeReq())
					for(int i = 0; i < mob.getSpawnRate(); i++)
						next.add(mob.getValue());
			}
			int select = random.nextInt(next.size());
			int pick = next.get(select);
			
			Mob mob = Mob.values()[pick];
			enemyID = pick;
			health = mob.getBaseHealth();
			
			refImage = SpriteLoader.getEnemyImage(enemyID);
		}
		setSize(refImage.getWidth(null), refImage.getHeight(null));

		dying = false;
		direction = (random.nextInt(2) == 0)? -1 : 1;
		setLocation(random.nextInt(view.getWidth() - getWidth() * 2) + getWidth(), -getHeight());
		BULLET_SIZE = SpriteLoader.getBulletImage().getWidth();
		ship = model.getShip();
	}
	
	public Rectangle getPhysicsBody() {
		Rectangle rect = getBounds();
		rect.setLocation(getX(), getY());
		return rect;
	}
	
	public void setPhysicsBody() {
		Rectangle rect = getBounds();
		bounds = new Polygon();
		bounds.addPoint(rect.x, rect.y);
		bounds.addPoint(rect.x + rect.width, rect.y);
		bounds.addPoint(rect.x + rect.width, rect.y + rect.height);
		bounds.addPoint(rect.x, rect.y + rect.height);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if((death == null || !death.isAnimating()) && !dying) {
			if(enemyID == 9)
				angle += 50;
			
			if(angle == 0 || enemyID != 9) {
				//paints the ship image on the game.
				g.drawImage(refImage, 0, 0, null);
			} else {
				Graphics2D g2d = (Graphics2D)g;
				
				if(!rotates.containsKey(angle)) {
					double rotationRequired = Math.toRadians(angle);
					AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, getWidth() / 2,  getHeight() / 2);
					rotates.put(angle, tx);
				}
				// Drawing the rotated image at the required drawing locations
				g2d.drawImage(refImage, rotates.get(angle), null);
			}
			g.setColor(Color.GREEN);
			/*double barX = 1/8f * getWidth();
			double barWidth = 6/8f * getWidth() * getHealth() / (double)getBaseHealth();
			g.fillRect((int)barX, getHeight() - 6, (int)barWidth, 6);*/

			double barY = 0; //1/8f * getWidth();
			double barHeight = getHeight() * getHealth() / (double)getBaseHealth();
			g.fillRect(getWidth() - 6,(int)barY, 6, (int)barHeight);
		} else if(death != null) {
			g.drawImage(death.getSprite(), 0, 0, null);
		}
	}
	
	public void update(SSEnemyHandler handler) {
		switch(enemyID) {
			case 9:
			case 16:
				break;
			case 12:
				attemptFire(10, 1, true);
				break;
			default:
				attemptFire(40, 1, false);
				break;
		}
		fireBuffer--;
		
		if(death != null) {
			if(!hasChecked) {
				handler.checkPowerup(this, view.getGameView());
				hasChecked = true;
			}
			if(!death.isAnimating()) {
				handler.removeMe(this, view.getGameView());
			}
			death.update();
		}
	}
	
	public void animatedDeath() {
		if(death == null)
			death = new AnimationEngine(SpriteLoader.getDeathSprites(), 1);
		
		setSize(SpriteLoader.TILE_SIZE, SpriteLoader.TILE_SIZE);
		dying = true;
		
		death.setRepeatable(false);
		death.start();
	}
	
	public void attemptFire(int fireRate, int fireCount, boolean targeted) {
		if(bulletController == null)
			bulletController = model.getBulletController();
		
		if(fireBuffer <= 0 && !dying && !model.isPaused()) {
			if(!ship.isShipAlive()) {
				fireBuffer = fireRate;
				return;
			}
		
			int c = 6;
			int defaultX = getX() + getWidth() / 2 - c;
			int fireAngle = (targeted)? getFireAngle() : 180;
			switch(fireCount) {
				case 2: 
					bulletController.addEnemyBullet(defaultX + (BULLET_SIZE + 4), getY() + getHeight(), fireAngle);
					bulletController.addEnemyBullet(defaultX - (BULLET_SIZE + 4), getY() + getHeight(), fireAngle);
					break;
				case 3:
					bulletController.addEnemyBullet(defaultX + (BULLET_SIZE + 4), getY() + getHeight(), fireAngle);
					bulletController.addEnemyBullet(defaultX						, getY() + getHeight(), fireAngle);
					bulletController.addEnemyBullet(defaultX - (BULLET_SIZE + 4), getY() + getHeight(), fireAngle);
					break;
				default:
					bulletController.addEnemyBullet(defaultX, getY() + getHeight(), fireAngle);
					break;
			}
			fireBuffer = fireRate;
		}
	}
	
	public int getFireAngle() {
		double angle = Math.toDegrees(Math.atan2(ship.getY() - getY(), ship.getX() - getX())) + 90;
	    return (int)angle;
	}

	public int getID() {
		return enemyID;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public void awardPoints() {
		if(isBoss) {
			Boss boss = Boss.values()[enemyID];
			model.setScoreCount(model.getScoreCount() + boss.getPoints());
			model.getPanelController().update(false);
			animatedDeath();
		} else {
			Mob mob = Mob.values()[enemyID];
			model.setScoreCount(model.getScoreCount() + mob.getPoints());
			model.getPanelController().update(false);
			animatedDeath();
		}
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getBaseHealth() {
		if(isBoss) {
			Boss boss = Boss.values()[enemyID];
			return boss.getBaseHealth();
		} else {
			Mob mob = Mob.values()[enemyID];
			return mob.getBaseHealth();
		}
	}
	
	public boolean isDying() {
		return dying;
	}
	
	public void setDying(boolean dying) {
		this.dying = dying;
	}

	public boolean isBoss() {
		return dying;
	}
	
	public void setBoss(boolean isBoss) {
		this.isBoss = isBoss;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		
		if(x < 0)
			model.getEnemyHandler().removeMe(this, view.getGameView());
		else if(x + getWidth() > view.getWidth())
			model.getEnemyHandler().removeMe(this, view.getGameView());
		else if(y + getHeight() > view.getHeight())
			model.getEnemyHandler().removeMe(this, view.getGameView());
	}
	
	public enum Mob {
	    NO_MOB		(0, 0, 0, 0, 0, 1),      //Null mob
	    BULB_MOB	(1, 200, 6, 0, 0, 2),    //mob_1
	    AXE_MOB		(2, 250, 4, 0, 0, 2),    //mob_2
	    PINSIR_MOB	(3, 200, 3, 0, 0, 5),    //mob_3
	    USER_MOB	(4, 0, 0, 0, 0, 1),	     //mob_4
	    JET_MOB		(5, 350, 2, 0, 0, 3),    //mob_5
	    FALCON_MOB	(6, 300, 2, 0, 0, 4),    //mob_6
	    HAWK_MOB	(7, 300, 3, 0, 0, 3),    //mob_7
	    JUMP_MOB	(8, 200, 2, 0, 0, 3),    //mob_8
	    PEANUT_MOB	(9, 100, 4, 0, 0, 1),    //mob_9
	    EYE_MOB		(10, 150, 8, 0, 0, 1),   //mob_10
	    FLY_MOB		(11, 200, 2, 0, 0, 3),   //mob_11
	    BEETLE_MOB	(12, 500, 1, 0, 0, 10),  //mob_12
	    TENTACLE_MOB(13, 500, 0, 0, 0, 1),   //mob_13
	    ART_MOB		(14, 500, 0, 0, 0, 1),   //mob_14
	    PINECONE_MOB(15, 500, 0, 0, 0, 1),   //mob_15
	    TRUCK_MOB	(16, 1000, 1, 0, 0, 20), //mob_16
	    CAR_MOB		(17, 500, 0, 0, 0, 1),   //mob_17
	    F_MOB		(18, 500, 0, 0, 0, 1),   //mob_18
	    G_MOB		(19, 500, 0, 0, 0, 1);   //mob_19

	    private final int value;
	    private final int points;
	    private final int spawnRate;
	    private final int timeReq;
	    private final int pointReq;
	    private final int baseHealth;

	    private Mob(int value, int points, int spawnRate, int timeReq, int pointReq, int baseHealth) {
	        this.value = value;
	        this.points = points;
	        this.spawnRate = spawnRate;
	        this.timeReq = timeReq;
	        this.pointReq = pointReq;
	        this.baseHealth = baseHealth;
	    }

	    public int getValue() {
	        return value;
	    }
	    
	    public int getPoints() {
	        return points;
	    }
	    
	    public int getSpawnRate() {
	        return spawnRate;
	    }
	    
	    public int getTimeReq() {
	        return timeReq;
	    }
	    
	    public int getPointReq() {
	        return pointReq;
	    }
	    
	    public int getBaseHealth() {
	        return baseHealth;
	    }
	}

	public enum Boss {
	    NO_BOSS			(0, 0, 0, 1),
	    TENTACLE_BOSS	(1, 3000, 5000, 1),
	    MEGA_BOSS		(2, 6000, 15000, 1);
	    
	    private final int value;
	    private final int points;
	    private final int threshold;
	    private final int baseHealth;

	    private Boss(int value, int points, int threshold, int baseHealth) {
	        this.value = value;
	        this.points = points;
	        this.threshold = threshold;
	        this.baseHealth = baseHealth;
	    }

	    public int getValue() {
	        return value;
	    }
	    
	    public int getPoints() {
	        return points;
	    }
	    
	    public int getThreshold() {
	        return threshold;
	    }
	    
	    public int getBaseHealth() {
	        return baseHealth;
	    }
	}
	
}
