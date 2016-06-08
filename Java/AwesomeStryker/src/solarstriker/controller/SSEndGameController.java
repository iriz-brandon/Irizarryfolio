package solarstriker.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import solarstriker.SolarStriker;
import solarstriker.model.SSMainModel;
import solarstriker.views.SSMainView;

public class SSEndGameController extends WindowAdapter implements ActionListener {
	SSMainModel model;
	SSMainView view;
	
	public SSEndGameController(SSMainModel model, SSMainView view) {
		this.model = model;
		this.view = view;
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
		reset();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == view.endGameButton) {
			view.endGameDialog.setVisible(false);
			view.endGameDialog.dispose();
		}
	}
	
	public void reset() {
		view.dispose();
		SolarStriker.main(new String[0]);
	}
}
