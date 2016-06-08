package solarstriker.views;

/* We will use this class to paint objects on the screen
 * in the area that is playable for the user.
 * This will be done using their x,y coordinates
 * combined with the size an object takes up
 * within the playable area. 
 */

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JPanel;

import solarstriker.controller.SSBulletController;
import solarstriker.model.SSEnemyHandler;
import solarstriker.model.SSMainModel;
import solarstriker.model.SSShipHandler;
import solarstriker.views.animation.SpriteLoader;

@SuppressWarnings("serial")
public class SSGameView extends JPanel { // View
	protected SSMainModel model;
	protected SSMainView view;
	private SSShipView shipView;
	private SSPowerupView powerupView;
	//private ArrayList<SSBulletView> bullets;
	private Image bg1 = (Image)SpriteLoader.loadSprite("Space.jpg"),
				  bg2 = (Image)SpriteLoader.loadSprite("Space.jpg");
	final int scrollSpeed = 2;
	int y = 0;
	
	private SSBulletController bulletController;
	private SSShipHandler shipHandler;
	private SSEnemyHandler enemyHandler;
	
	public SSGameView(SSMainModel model, SSMainView view) {
		this.model = model;
		this.view = view;
		setLayout(null);
		
		//bullets = new ArrayList<SSBulletView>();
		
		shipView = new SSShipView(model, view);
        shipView.setOpaque(false);
        add(shipView);
        
        powerupView = new SSPowerupView(model, view);
        powerupView.setOpaque(false);
        add(powerupView);
        
        model.registerGameView(this);
	}	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(bg1, 0, y - bg2.getHeight(null), null);
		g.drawImage(bg2, 0, y, null);
		y += scrollSpeed;
		if(y > bg2.getHeight(null) - scrollSpeed)
			y = 0;
	}
	
	public SSPowerupView getPowerupView() {
		return powerupView;
	}
	
	public void update() {
		ArrayList<SSBulletView> bullets = model.getBulletController().getBullets();
		for(SSBulletView each : bullets){
			int newX = (int)(each.getX() + Math.cos(Math.toRadians(each.getTheta()) - Math.PI / 2) * each.getSpeed());
			int newY = (int)(each.getY() + Math.sin(Math.toRadians(each.getTheta()) - Math.PI / 2) * each.getSpeed());
			each.setLocation(newX, newY);
		}
		if(powerupView.isVisible() && shipHandler.isShipAlive())
			powerupView.pushLimit();
		
		shipView.update();
		checkCollisions();
	}
	
	public SSShipView getShipView() {
		return shipView;
	}
	
	private void checkCollisions() {
		if(shipHandler == null)
			shipHandler = model.getShip();
		if(enemyHandler == null)
			enemyHandler = model.getEnemyHandler();
		if(bulletController == null)
			bulletController = model.getBulletController();
		
		ArrayList<SSEnemyView> enemies = enemyHandler.getEnemies();
		ArrayList<SSBulletView> bullets = bulletController.getBullets();
		SSShipView ship = shipView;
		
		//-----Need to add the X, Y location to the Bounds/PhysicsBody of the ship/bullet/enemies so their bounding bodies are accurate 
		
		ArrayList<JPanel> toRemove = new ArrayList<JPanel>();
		if(shipHandler.isShipAlive()) {
			if(!shipHandler.isFreshAlive()) {
				for(SSBulletView bullet : bullets) {
					if(ship.getPhysicsBody().intersects(bullet.getPhysicsBody())) {
						if(!bullet.isOwn()) {
							System.out.println("Ship hit bullet");
							shipHandler.killShip(shipView);
							if(!toRemove.contains(bullet))
								toRemove.add(bullet);
							break;
						}
					}
				}
				
				for(SSEnemyView enemy : enemies) {
					if(enemy.isDying())
						continue;
					
					if(ship.getPhysicsBody().intersects(enemy.getPhysicsBody())) {
						System.out.println("Ship hit enemy");
						shipHandler.setHitsRemaining(0);
						shipHandler.killShip(shipView);
						if(!toRemove.contains(enemy))
							toRemove.add(enemy);
						break;
					}
				}
			}
			
			for(SSBulletView bullet1 : bullets) {
				for(SSBulletView bullet2 : bullets) {
					if(bullet1 == bullet2)
						continue;
					
					if(bullet1.getPhysicsBody().intersects(bullet2.getPhysicsBody())) {
						if((bullet1.isOwn() && !bullet2.isOwn()) || (!bullet1.isOwn() && bullet2.isOwn())) {
							System.out.println("Bullet hit bullet");
							
							if(!toRemove.contains(bullet1))
								toRemove.add(bullet1);
							if(!toRemove.contains(bullet2))
								toRemove.add(bullet2);
						}
					}
				}
			}
			
			if(powerupView.isVisible() && ship.getPhysicsBody().intersects(powerupView.getPhysicsBody())) {
				System.out.println("Ship contact with powerup " + powerupView.getID());
				shipHandler.addPowerup(powerupView.getID());
				powerupView.despawn();
			}
		}
		
		for(SSEnemyView enemy : enemies) {
			if(enemy.isDying())
				continue;
			
			for(SSBulletView bullet : bullets) {
				if(enemy.getPhysicsBody().intersects(bullet.getPhysicsBody())) {
					if(bullet.isOwn()) {
						System.out.println("Enemy hit bullet");
						
						if(!toRemove.contains(bullet))
							toRemove.add(bullet);
						//if(!toRemove.contains(enemy))
						toRemove.add(enemy);
					}
				}
			}
		}
		
		for(int i = toRemove.size() - 1; i >= 0; i--) {
			JPanel o = toRemove.get(i);
			if(o instanceof SSBulletView) {
				SSBulletView bullet = (SSBulletView)o;
				synchronized(bullet) {
					bulletController.destroyBullet(bullet);
				}
			}
			if(o instanceof SSEnemyView) {
				if(model.getShip().isShipAlive()) {
					if(enemyHandler.weaken((SSEnemyView) o) == 0)
						enemyHandler.kill((SSEnemyView)o, this);
				} else
					enemyHandler.kill((SSEnemyView)o, this);
			}
		}
	}
}
