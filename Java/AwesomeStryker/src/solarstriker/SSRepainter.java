package solarstriker;

//part of the level rendering that repaints the screen based on an action performed

//part of the level rendering that repaints the screen based on an action performed

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import solarstriker.model.SSMainModel;
import solarstriker.model.SSShipHandler;
import solarstriker.views.SSMainView;

public class SSRepainter implements ActionListener {
	SSMainModel model;
	SSMainView view;
	SSShipHandler ship;
	
	public SSRepainter(SSMainModel model, SSMainView view) {
		this.model = model;
		this.view = view;
		//this.ss = SS;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	  if(e.getSource() == SolarStriker.timer) {
		HashMap<Integer, Boolean> keys = model.getKeys();
		if(keys.containsKey(KeyEvent.VK_P) && keys.get(KeyEvent.VK_P) && !model.getHomeFlag()) {
			model.pause(!model.isPaused());
		}
		
		if(model.isPaused() || !view.isVisible())
			return;
		
		if(ship == null)
			ship = model.getShip();
		
		if((keys.containsKey(KeyEvent.VK_LEFT) && keys.get(KeyEvent.VK_LEFT)) || (keys.containsKey(KeyEvent.VK_A) && keys.get(KeyEvent.VK_A))) {
			ship.moveLeft();
		}
		if((keys.containsKey(KeyEvent.VK_RIGHT) && keys.get(KeyEvent.VK_RIGHT)) || (keys.containsKey(KeyEvent.VK_D) && keys.get(KeyEvent.VK_D))) {
			ship.moveRight();
		}
		if((keys.containsKey(KeyEvent.VK_UP) && keys.get(KeyEvent.VK_UP)) || (keys.containsKey(KeyEvent.VK_W) && keys.get(KeyEvent.VK_W))) {
			ship.moveUp();
		}
		if((keys.containsKey(KeyEvent.VK_DOWN) && keys.get(KeyEvent.VK_DOWN)) || (keys.containsKey(KeyEvent.VK_S) && keys.get(KeyEvent.VK_S))) {
			ship.moveDown();
		}
		if(keys.containsKey(KeyEvent.VK_SPACE) && keys.get(KeyEvent.VK_SPACE)) {
			ship.shipFire();
		}
		if(keys.containsKey(KeyEvent.VK_B) && keys.get(KeyEvent.VK_B)) {
			ship.rotate(1);
		}
		if(keys.containsKey(KeyEvent.VK_V) && keys.get(KeyEvent.VK_V)) {
			ship.rotate(-1);
		}
	  
		view.update();
	  }	
	}
}