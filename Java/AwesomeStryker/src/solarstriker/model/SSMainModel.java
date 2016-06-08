package solarstriker.model;

import java.awt.geom.AffineTransform;
import java.util.HashMap;

import solarstriker.controller.SSBulletController;
import solarstriker.controller.SSPanelController;
import solarstriker.views.SSGameView;

public class SSMainModel { // Model
	
	//example of some property categories required
	private HashMap<Integer, AffineTransform> rotates;
	private SSEnemyHandler enemies;
	private SSShipHandler ship;
	private SSBulletController bullets;
	private SSPanelController panelController;
	private boolean showingHomeFlag;
	
	//properties of the panel
	private int width, height;
	private int shipWidth, shipHeight;
	
	//properties of the game
	private boolean pause;
	private HashMap<Integer, Boolean> keys;
	
	private SSGameView gameView;
	private int scoreCount;
	
	
	public SSMainModel() {
		//will need to add necessary properties to the view as you do your own parts
		//all manipulated properties of all our objects need to be represented here
		showingHomeFlag = true;
		keys = new HashMap<Integer, Boolean>();
		rotates = new HashMap<Integer, AffineTransform>();

		ship = new SSShipHandler(this);
		enemies = new SSEnemyHandler(this);
		setup();
	}
	
	public void setup() {
		enemies.reset();
		ship.reset();
		//if(bullets != null)
		//	bullets.reset();
		
		pause = true;
	}

	public void registerController(SSBulletController bulletController) {
		this.bullets = bulletController;
	}
	
	public void registerPanelController(SSPanelController panelController) {
		this.panelController = panelController;
	}
	
	public void registerGameView(SSGameView gameView) {
		this.gameView = gameView;
	}
	
	//some setters
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		ship.setup();
	}
	
	public SSGameView getGameView() {
		return gameView;
	}
	
	public void pause(boolean pause) {
		this.pause = pause;
	}
	
	public void adjustShowingHome() {
		this.showingHomeFlag = !this.showingHomeFlag;
		pause(this.showingHomeFlag);
	}
	
	public void adjustShowingHome(boolean showingHomeFlag) {
		this.showingHomeFlag = showingHomeFlag;
		pause(showingHomeFlag);
	}
	
	public void setKeys(int key, boolean set) {
		keys.put(key, set);
	}
	
	public int[] getShipSize() {
		return new int[]{ shipWidth, shipHeight };
	}
	
	public void setShipSize(int width, int height) {
		shipWidth = width;
		shipHeight = height;
	}
	 
	//some getters
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public boolean getHomeFlag() { return showingHomeFlag;}
	public SSShipHandler getShip() { return ship; }
	public SSEnemyHandler getEnemyHandler() { return enemies; }
	public SSBulletController getBulletController() { return bullets; }
	public SSPanelController getPanelController() { return panelController; }
	
	public HashMap<Integer, Boolean> getKeys() { return keys; }
	public HashMap<Integer, AffineTransform> getRotates() { return rotates; }
	
	//some conditions
	public boolean isPaused() { return pause; }

	public int getScoreCount() {
		return scoreCount;
	}

	public void setScoreCount(int scoreCount) {
		this.scoreCount = scoreCount;
	}

}