package solarstriker.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import solarstriker.model.SSMainModel;
import solarstriker.views.animation.SpriteLoader;

public class SSPanelView extends JPanel {
	private static final long serialVersionUID = 3004995352926957301L;
	ImageIcon icon = null;
	SSMainModel model;
	SSMainView view;
	
	JPanel lifeCounter = new JPanel();
	JLabel lifeNameLabel = new JLabel();
	JLabel shipLife1 = new JLabel();
	JLabel shipLife2 = new JLabel();
	JLabel shipLife3 = new JLabel();
	
	JLabel imgHolder = new JLabel();
	JLabel scoreCounter = new JLabel();
	
	long lastUpdate = 0;
	
	public SSPanelView(SSMainModel model, SSMainView view) {
		this.model = model;
		this.view = view;
		
		setSize(600, 50);
	    setPreferredSize(new Dimension(600, 50));
		//setPreferredSize(new Dimension(700,50));
        setBackground(Color.WHITE);
        setOpaque(true);
        setLayout(new BorderLayout());

        lifeCounter.setSize(250, 50);
        setMyIcon("mob_4");
        
        updateScore();
        lifeNameLabel.setHorizontalAlignment(JLabel.CENTER);
        add(lifeCounter, BorderLayout.EAST);
        scoreCounter.setHorizontalAlignment(JLabel.CENTER);
        add(scoreCounter, BorderLayout.WEST);
	}
	
	public void setMyIcon(String img){
		Image roughIcon = (Image)SpriteLoader.loadSprite(img);  
	    Image iconResize = roughIcon.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH);
	    Image iconResize2 = roughIcon.getScaledInstance(27, 27, java.awt.Image.SCALE_SMOOTH);
	    ImageIcon newIcon = new ImageIcon(iconResize);  
	    ImageIcon lifeIcon = new ImageIcon(iconResize2);
	    icon = newIcon;
	    imgHolder.setIcon(newIcon);
	    add(imgHolder, BorderLayout.WEST);
	    
	    lifeCounter.setLayout(new BorderLayout());
	    lifeCounter.setBackground(Color.WHITE);
	    lifeNameLabel.setText("Lives");
	    lifeCounter.add(lifeNameLabel, BorderLayout.NORTH);
	    shipLife1.setIcon(lifeIcon);
	    shipLife2.setIcon(lifeIcon);
	    shipLife3.setIcon(lifeIcon);
	    lifeCounter.add(shipLife1, BorderLayout.WEST);
	    lifeCounter.add(shipLife2, BorderLayout.CENTER);
	    lifeCounter.add(shipLife3, BorderLayout.EAST);
	    updateLives(true);
	    
	    validate();
	}
	
	public void updateLives(boolean changeViews) {
		switch(model.getShip().getLives()){
			case 0:
				shipLife1.setVisible(false);
				shipLife2.setVisible(false);
				shipLife3.setVisible(false);
				break;
			case 1:
				shipLife1.setVisible(true);
				shipLife2.setVisible(false);
				shipLife3.setVisible(false);
				break;
			case 2:
				shipLife1.setVisible(true);
				shipLife2.setVisible(true);
				shipLife3.setVisible(false);
				break;
			default:
				shipLife1.setVisible(true);
				shipLife2.setVisible(true);
				shipLife3.setVisible(true);
				break;
		}
		if(changeViews) { 
			Date time = new Date();
			if(time.getTime() > lastUpdate + 1000) {
				view.repaint();
				view.displayPanel();
				lastUpdate = time.getTime();
			}
		}
	}
	
	public ImageIcon getIcon(){ return icon; }
	public void updateScore() {
        scoreCounter.setText("Score: " + model.getScoreCount());
	}
}