package solarstriker.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import solarstriker.model.SSMainModel;
import solarstriker.views.SSMainView;

//not set up yet, but will help with my(Brandon) menu navigation part

@SuppressWarnings("serial")
public class SSMenuController extends JMenu implements ActionListener {
	SSMainModel model;
	SSMainView view;
	JMenuItem homeItem, pauseItem, exitItem;
	
	public SSMenuController(SSMainModel model, String title, SSMainView view) {
		super(title);
		this.model = model;
		this.view = view;
		initializeJMenu();
	}
    
    public void initializeJMenu(){
		setMnemonic('M');
		
		homeItem = new JMenuItem("Home");
		homeItem.setMnemonic('H');
		homeItem.addActionListener(this);
		add(homeItem);
		
		pauseItem = new JMenuItem("Pause/Continue");
		pauseItem.setMnemonic('P');
		pauseItem.addActionListener(this);
		add(pauseItem);
		
		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic('E');
		exitItem.addActionListener(this);
		add(exitItem);
    }
    
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == homeItem) {
			System.out.println("Home Pressed");
			model.adjustShowingHome();
			view.displayPanel();
		} else if(e.getSource() == pauseItem) {
			model.pause(!model.isPaused());
		} else if(e.getSource() == exitItem) {
			System.exit(0);
		}
	}
}