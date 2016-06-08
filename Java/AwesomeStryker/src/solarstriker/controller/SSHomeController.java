package solarstriker.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import solarstriker.model.SSMainModel;
import solarstriker.views.SSMainView;

public class SSHomeController implements ActionListener {

	private SSMainModel model;
	private SSMainView view;
	
	public SSHomeController(SSMainModel model, SSMainView view) {
		this.model = model;
		this.view = view;
		
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("TEST");
		String command = e.getActionCommand();
		if (command.equals("PLAY")) {
			model.adjustShowingHome();
			view.displayPanel();
		}
	}
}