package solarstriker.model;

import solarstriker.views.SSPowerupView;
import solarstriker.views.SSShipView;
import solarstriker.views.animation.SpriteLoader;

public class SSShipHandler { // Model
	private SSMainModel model;
	final int baseSpeed = 12;
	final int MAX_FIRE_COUNT = 3;
	final int DEFAULT_LIVES = 3;
	final int ARMOR_RATING = 1;
	private int bulletBuffer = 0;
	private int x;
	private int y;
	private int speed;
	private int freshAliveCount = 0;
	private boolean shipAlive = true;
	private boolean freshAlive = false;
	private boolean approved = true;
	private boolean dying = false;
	private int lives = DEFAULT_LIVES;
	private int hitsRemaining = ARMOR_RATING;
	
	final int baseAngle = 0;
	private int LIMIT_BOTTOM, LIMIT_BOTTOM_2;
	private int SHIP_RESPAWN_HEIGHT;
	private int HALF_SHIP_HEIGHT, HALF_SHIP_WIDTH;
	private int BULLET_SIZE;
	private int angle;
	private int fireCount;
	
	private int nukePowerup = 0;
	private int shieldPowerup = 0;
	
	private SSShipView shipView;
	
	public SSShipHandler(SSMainModel model) {
		this.model = model;
		x = 300;
		y = 300;
		speed = baseSpeed;
		angle = baseAngle;
	}
	
	public void setup() {
		fireCount = 1;
		x = model.getWidth() / 2 - model.getShipSize()[0] / 2;
		y = model.getHeight() + model.getShipSize()[1];
		LIMIT_BOTTOM = model.getHeight() - model.getShipSize()[1] * 2 - 10;
		LIMIT_BOTTOM_2 = model.getHeight() - model.getShipSize()[1] * 2;
		SHIP_RESPAWN_HEIGHT = (int) (LIMIT_BOTTOM - model.getShipSize()[1] * 1.2);
		HALF_SHIP_WIDTH = model.getShipSize()[0] / 2;
		HALF_SHIP_HEIGHT = model.getShipSize()[1] / 2;
		BULLET_SIZE = SpriteLoader.getBulletImage().getWidth();
	}
	
	/* Getters */
	public int getX() { return x; }
	public int getY() { return y; }
	public int getBaseSpeed() { return baseSpeed; }
	public int getSpeed() { return speed; }
	public int getAngle() { return angle; }
	
	/* Setters */
	public void setSpeed(int speed) { this.speed = speed; }
	public void setAngle(int angle) { this.angle = angle; }
	
	/* Actions */
	public void moveUp() {
		if(y > 0 && approved && !dying)
			y -= speed;
	}

	public void approveBounds() {
		if(model.getGameView() != null && !model.isPaused()) {
			if(shipView == null)
				shipView = model.getGameView().getShipView();
			
			if(freshAliveCount >= 60) {
				freshAliveCount = 0;
				freshAlive = false;
				shipView.setVisible(true);
			}
			
			if(freshAlive) {
				if(freshAliveCount % 4 == 0)
					shipView.setVisible(false);
				if(freshAliveCount % 8 == 0)
					shipView.setVisible(true);
				freshAliveCount++;
			}
		}
		
		if(y > LIMIT_BOTTOM_2 && approved) {
			approved = false;
			System.out.println("Ship out of bounds");
		}
		if(!approved) {
			dying = false;
			y -= speed;
			freshAlive = true;
			if(y < SHIP_RESPAWN_HEIGHT) {
				approved = true;
				setShipAlive(true);
			}
		}
	}
	
	public void moveLeft() {
		if(x > 0 && approved && !dying)
			x -= speed;
	}

	public void moveDown() {
		if(y < LIMIT_BOTTOM && approved && !dying)
			y += speed;
	}
	
	public void moveRight() {
		if(x < model.getWidth() - model.getShipSize()[0] && approved && !dying)
			x += speed;
	}
	
	public void shipFire() {
		if(nukePowerup > 0) {
			shipNuke();
			return;
		}
		
		if(bulletBuffer == 0 && approved && !dying) {
			int c = 6;
			int defaultX = x + model.getShipSize()[0] / 2 - c;
			switch(fireCount) {
				case 2: 
					model.getBulletController().addBullet(defaultX + (BULLET_SIZE + 4), y, angle);
					model.getBulletController().addBullet(defaultX - (BULLET_SIZE + 4), y, angle);
					break;
				case 3:
					model.getBulletController().addBullet(defaultX + (BULLET_SIZE + 4), y, angle);
					model.getBulletController().addBullet(defaultX, y, angle);
					model.getBulletController().addBullet(defaultX - (BULLET_SIZE + 4), y, angle);
					break;
				default:
					model.getBulletController().addBullet(defaultX, y, angle);
					break;
			}
			bulletBuffer = 10;
		}
	}
	
	public void shipNuke() {
		if(bulletBuffer == 0 && approved && !dying) {
			nukePowerup--;
			int c = 6;
			model.getBulletController().addBullet(x - c, y, -45);
			model.getBulletController().addBullet(x + HALF_SHIP_WIDTH - c, y, 0);
			model.getBulletController().addBullet(x + HALF_SHIP_WIDTH * 2 - c, y, 45);
			model.getBulletController().addBullet(x + HALF_SHIP_WIDTH * 2 - c, y + HALF_SHIP_HEIGHT, 90);
			model.getBulletController().addBullet(x + HALF_SHIP_WIDTH * 2 - c, y + HALF_SHIP_HEIGHT * 2, 135);
			model.getBulletController().addBullet(x + HALF_SHIP_WIDTH - c, y + HALF_SHIP_HEIGHT * 2, 180);
			model.getBulletController().addBullet(x - c, y + HALF_SHIP_HEIGHT * 2, 225);
			model.getBulletController().addBullet(x - c, y + HALF_SHIP_HEIGHT, 270);

			bulletBuffer = 14;
		}
	}
	
	public void addPowerup(int powerupID) {
		int k = SSPowerupView.getInfo(powerupID).getQuantity();
		switch(powerupID) {
			case 0:
				if(fireCount < MAX_FIRE_COUNT)
					fireCount += k;
				break;
			case 1:
				shieldPowerup = k;
				break;
			case 2:
				nukePowerup = k;
				/*while(k > 0) {
					shipNuke();
					k--;
				}*/
				break;
			case 3:
				setLives(getLives() + 1);
				model.getPanelController().update(false);
				break;
			default:
				break;
		}
	}

	public void rotate(int direction) {
		//setAngle(angle + direction * 8);
	}

	public int getBulletBuffer() {
		return bulletBuffer;
	}

	public void decreaseBuffer() {
		if(bulletBuffer > 0)
			bulletBuffer--;
	}
	
	public int getShield() {
		return shieldPowerup;
	}
	
	public void setShield(int shieldPowerup) {
		this.shieldPowerup = shieldPowerup;
	}
	
	public void killShip(SSShipView ship) {
		if((shieldPowerup + hitsRemaining) <= 0) {
			hitsRemaining = ARMOR_RATING;
			setLives(getLives() - 1);
			model.getPanelController().update(true);
			dying = true;
			setShipAlive(false);
			ship.animatedDeath();
		} else {
			if(shieldPowerup > 0)
				shieldPowerup--;
			else
				hitsRemaining--;
		}
	}
	
	public void respawn() {
		setup();
		//shieldPowerup = 3;
	}

	public boolean isShipAlive() {
		return shipAlive;
	}
	
	public boolean isFreshAlive() {
		return freshAlive;
	}
	
	public void setFreshAlive(boolean freshAlive) {
		this.freshAlive = freshAlive;
	}
	
	public boolean isDying() {
		return dying;
	}
	
	public void setDying(boolean dying) {
		this.dying = dying;
	}

	public void setShipAlive(boolean shipAlive) {
		this.shipAlive = shipAlive;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		if(lives <= 8 && lives >= 0)
			this.lives = lives;
	}

	public int getHitsRemaining() {
		return hitsRemaining;
	}

	public void setHitsRemaining(int hitsRemaining) {
		this.hitsRemaining = hitsRemaining;
	}

	public void reset() {
		lives = DEFAULT_LIVES;
		hitsRemaining = ARMOR_RATING;
	}
}
