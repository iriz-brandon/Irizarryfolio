package solarstriker.model;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import solarstriker.views.SSMainView;

public class SSKeyHandler implements KeyListener {
	SSMainModel model;
	SSMainView view;
	
	public SSKeyHandler(SSMainModel model, SSMainView view) {
		this.model = model;
		this.view = view;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	    model.setKeys(e.getKeyCode(), true);
	}

	@Override
	public void keyPressed(KeyEvent e) {
	    model.setKeys(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	    model.setKeys(e.getKeyCode(), false);
	}
}
