package solarstriker.model;

import java.util.ArrayList;
import java.util.Random;

import solarstriker.views.SSEnemyView;
import solarstriker.views.SSGameView;
import solarstriker.views.SSPowerupView;

/**
 * Contains the movements for AI
 * @author Taylor
 *
 */
public class SSEnemyHandler {
	private ArrayList<SSEnemyView> enemies;
	private ArrayList<SSEnemyView> toRemove;
	private SSMainModel model;
	int speed = 7;
	private int currentBoss = 0;
	boolean left = false;
	boolean right = false;
	
	private SSGameView gameView;
	private SSPowerupView powerupView;
	private SSShipHandler ship;
	
	public SSEnemyHandler(SSMainModel model){
		this.model = model;
		enemies = new ArrayList<SSEnemyView>();
		toRemove = new ArrayList<SSEnemyView>();
		ship = model.getShip();
	}
	
	public ArrayList<SSEnemyView> getEnemies(){
		return enemies;
	}
	
	public void update(){
		for(int i = toRemove.size() - 1; i >= 0; i--) {
			SSEnemyView enemy = toRemove.get(i);
			enemies.remove(enemy);
			gameView.remove(enemy);
			toRemove.remove(enemy);
		}
		
		for(SSEnemyView enemy: enemies) {
			enemy.update(this);
			if(enemy.isDying())
				continue;
			
			int id = enemy.getID();
			switch(id) {
				case 1:
					if(enemy.getX() > 9/10f * model.getWidth() - enemy.getWidth())
						enemy.setDirection(-2);
					if(enemy.getX() < 1/10f * model.getWidth())
						enemy.setDirection(2);
					
					enemy.setLocation(enemy.getX() + enemy.getDirection() * speed, enemy.getY() + speed);
					break;
				case 2:
					int y = 1;
					if(enemy.getX() < ship.getX())
						enemy.setDirection(1);
					if(enemy.getX() > ship.getX())
						enemy.setDirection(-1);
					if(enemy.getY() < ship.getY())
						y = 1;
					if(enemy.getY() > ship.getY())
						y = -1;
					
					enemy.setLocation(enemy.getX() + enemy.getDirection() * (speed - 1), enemy.getY() + y * (speed - 1));
					break;
				case 3:
					if(enemy.getY() == 200 && enemy.getX() > 7/8f * model.getWidth() - enemy.getWidth() / 2){
						enemy.setDirection(-2);
						enemy.setLocation(enemy.getX() + enemy.getDirection() * speed, enemy.getY());
						break;
					}
					if(enemy.getY() == 200 && enemy.getX() < 1/8f * model.getWidth() + enemy.getWidth() / 2){
						enemy.setDirection(2);
						enemy.setLocation(enemy.getX() + enemy.getDirection() * speed, enemy.getY());
						break;
					}
					if(enemy.getX() % 100 == 0) {
						enemy.setLocation(enemy.getX(), enemy.getY() + speed);
						break;
					}
					
					enemy.setLocation(enemy.getX(), enemy.getY() + speed);
					break;
				case 5:
					if(enemy.getX() < ship.getX())
						enemy.setDirection(1);
					if(enemy.getX() > ship.getX())
						enemy.setDirection(-1);
					
					enemy.setLocation(enemy.getX(), enemy.getY() + (speed + 5));
					break;
				case 7:
					if(enemy.getX() < ship.getX())
						enemy.setDirection(1);
					if(enemy.getX() > ship.getX())
						enemy.setDirection(-1);
					
					enemy.setLocation(enemy.getX() + enemy.getDirection() * (speed + 4), enemy.getY() + (speed + 2));
					break;
				case 8:
					if(enemy.getY() + 100 > ship.getY() && enemy.getX() < ship.getX())
						enemy.setDirection(1);
					if(enemy.getY() + 100 > ship.getY() && enemy.getX() > ship.getX())
						enemy.setDirection(-1);
					if(enemy.getX() > 9/10f * model.getWidth() - enemy.getWidth())
						enemy.setDirection(-1);
					if(enemy.getX() < 1/10f * model.getWidth())
						enemy.setDirection(1);
					
					enemy.setLocation(enemy.getX() + enemy.getDirection() * speed, enemy.getY() + speed);
					break;
				case 9:
					if(enemy.getX() > 9/10f * model.getWidth() - enemy.getWidth())
						enemy.setDirection(-1);
					if(enemy.getX() < 1/10f * model.getWidth())
						enemy.setDirection(1);
					
					enemy.setLocation(enemy.getX() + enemy.getDirection() * speed, enemy.getY() + speed);
					break;
				case 10:
					if(enemy.getY() >= 350 && enemy.getY() < 400 && enemy.getX() < ship.getX()){
						enemy.setDirection(1);
						left = true;
						right = false;
						enemy.setLocation(enemy.getX() + enemy.getDirection() * speed, enemy.getY() + speed);
						break;
					}
					if(enemy.getY() >= 350 && enemy.getY() < 400 && enemy.getX() > ship.getX()){
						enemy.setDirection(-1);
						right = true;
						left = false;
						enemy.setLocation(enemy.getX() + enemy.getDirection() * speed, enemy.getY() + speed);
						break;
					}
					if(enemy.getY() >= 400 && left){
						enemy.setDirection(1);
						enemy.setLocation(enemy.getX() + enemy.getDirection() * speed, enemy.getY());
						break;
					}
					if(enemy.getY() >= 400 && right){
						enemy.setDirection(-1);
						enemy.setLocation(enemy.getX() + enemy.getDirection() * speed, enemy.getY());
						break;
					}
					
					enemy.setLocation(enemy.getX(), enemy.getY() + speed);
					break;
				case 11:
					if(enemy.getY() == ship.getY())
						enemy.setDirection(2);
					
					enemy.setLocation(enemy.getX() + enemy.getDirection() * speed + 2, enemy.getY());
					break;
				case 12:
					/*if(enemy.getX() > 7/8f * model.getWidth() - enemy.getWidth() / 2)
						enemy.setDirection(-1);
					if(enemy.getX() < 1/8f * model.getWidth() + enemy.getWidth() / 2)
						enemy.setDirection(1);*/
					
					enemy.setLocation(enemy.getX(), enemy.getY() + 2);
					break;
				case 16:
					
					enemy.setLocation(enemy.getX(), enemy.getY() + (speed - 2));
					break;
				default:
					enemy.setLocation(enemy.getX(), enemy.getY() + speed);
					break;
			}
		}
	}
	
	public int weaken(SSEnemyView enemy) {
		enemy.setHealth(enemy.getHealth() - 1);
		return enemy.getHealth();
	}
	
	public void kill(SSEnemyView enemy, SSGameView view) {
		enemy.awardPoints();
		gameView = view;
	}
	
	public void checkPowerup(SSEnemyView enemy, SSGameView view) {
		if(powerupView == null)
			powerupView = gameView.getPowerupView();
		
		if(!powerupView.isVisible()) {
			Random rm = new Random();
			int pup = rm.nextInt(10);
			if(pup == 0)
				powerupView.spawn(enemy.getX() + enemy.getWidth() / 2, enemy.getY() + enemy.getHeight() / 2);
		}
	}
	
	public void removeMe(SSEnemyView enemy, SSGameView view) {
		toRemove.add(enemy);
		gameView = view;
	}
	
	public int getCurrentBoss() {
		return currentBoss;
	}

	public void reset() {
		for(SSEnemyView enemy : enemies)
 			removeMe(enemy, gameView);
	}
}
