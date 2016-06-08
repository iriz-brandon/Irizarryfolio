package solarstriker.controller;

import java.util.ArrayList;

import solarstriker.model.SSMainModel;
import solarstriker.views.SSBulletView;
import solarstriker.views.SSMainView;

public class SSBulletController { // Controller
    private SSMainModel model;
    private SSMainView view;
	private int bulletsFired = 0;
	private ArrayList<SSBulletView> bullets; 
	
    public SSBulletController(SSMainModel model, SSMainView view) {
    	this.model = model;
    	this.view = view;
    	
    	bullets = new ArrayList<SSBulletView>();
    	
    	model.registerController(this);
    }
    
	public ArrayList<SSBulletView> getBullets() {
		return bullets;
	}
	
	public void addEnemyBullet(int x, int y, int angle) {
		int useableAngle = angle % 360;
		if(useableAngle < 0)
			useableAngle += 360;

		if(useableAngle >= 90 && useableAngle <= 270) {
			SSBulletView bullet = new SSBulletView(model, view);
			bullet.setLocation(x, y);
			bullet.setTheta(angle);
			bullet.setOpaque(false);
			bullet.setOwn(false);
			
			bullets.add(bullet);
			view.addBullet(bullet);
		}
	}

	public void addBullet(int x, int y, int angle) {
		SSBulletView bullet = new SSBulletView(model, view);
		bullet.setLocation(x, y);
		bullet.setTheta(angle);
		bullet.setOpaque(false);
		bullet.setOwn(true);
		
		bullets.add(bullet);
		view.addBullet(bullet);
		
		++bulletsFired;
	}
	
	public int getBulletsFired() {
		return bulletsFired;
	}
	
	public void destroyBullet(SSBulletView bullet) {
		synchronized(bullet) {
			if(bullet != null) {
				bullets.remove(bullet);
				view.getGameView().remove(bullet);
				
				bullet = null;
			}
		}
	}
}  
 
