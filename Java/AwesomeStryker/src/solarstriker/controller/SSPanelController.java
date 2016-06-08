package solarstriker.controller;

import solarstriker.model.SSMainModel;
import solarstriker.views.SSMainView;
import solarstriker.views.SSPanelView;

public class SSPanelController { // Controller
	SSMainModel model;
	SSMainView view;
	SSPanelView panelView;

	public SSPanelController(SSMainModel model, SSMainView view) {
		this.model = model;
		this.view = view;
		this.panelView = view.getPanelView();
		
		model.registerPanelController(this);
	}
	
	public void update(boolean changeViews) {
		panelView.updateLives(changeViews);
		panelView.updateScore();
	}
}

