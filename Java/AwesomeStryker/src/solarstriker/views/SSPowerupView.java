package solarstriker.views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import solarstriker.model.SSMainModel;
import solarstriker.views.animation.SpriteLoader;

public class SSPowerupView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8191210884036233518L;
	private BufferedImage refImage;
	protected SSMainModel model;
	protected SSMainView view;
	
	private int powerupID;
	private int limit = 0;
	
	public enum Powerup {
	    FIREPOWER	(0, 1, -1, 3, Color.WHITE),
	    SHIELD 		(1, 3, 200, 0, Color.WHITE),
	    NUKE 		(2, 3, -1, 3, Color.WHITE),
	    LIFE 		(3, 3, -1, 1, Color.WHITE);

	    private final int value;
	    private final int quantity;
	    private final int duration;
	    private final int spawnRate;
	    private final Color color;

	    private Powerup(int value, int quantity, int duration, int spawnRate, Color color) {
	        this.value = value;
	        this.quantity = quantity;
	        this.duration = duration;
	        this.spawnRate = spawnRate;
	        this.color = color;
	    }

	    public int getValue() {
	        return value;
	    }
	    
	    public int getQuantity() {
	        return quantity;
	    }
	    
	    public int getDuration() {
	        return duration;
	    }
	    
	    public int getSpawnRate() {
	        return spawnRate;
	    }
	    
	    public Color getColor() {
	    	return color;
	    }
	}
	
	//Powerups will have a random chance to spawn at an enemy's location upon their death
	public SSPowerupView(SSMainModel model, SSMainView view){
		this.model = model;
		this.view = view;

		refImage = SpriteLoader.getPowerupImage(Color.WHITE);
		setSize(refImage.getWidth(null), refImage.getHeight(null));
		setVisible(false);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(refImage, 0, 0, null);
	}
	
	public Rectangle getPhysicsBody() {
		Rectangle rect = getBounds();
		rect.setLocation(getX(), getY());
		return rect;
	}
	
	public static Powerup getInfo(int id) {
		return Powerup.values()[id];
	}
	
	public int getID() {
		return powerupID;
	}
	
	public void setID(int powerupID) {
		this.powerupID = powerupID;
	}
	
	public void pickPowerup() {
		List<Integer> powerups = new ArrayList<Integer>();
		for(int j = 0; j <  Powerup.values().length; j++) {
			Powerup pup = Powerup.values()[j];
			for(int i = 0; i < pup.getSpawnRate(); i++)
				powerups.add(pup.getValue());
		}

		Random random = new Random();
		int select = random.nextInt(powerups.size());
		
		int pick = powerups.get(select);
		System.out.println(powerups + ", " + select + ", " + pick);

		Powerup pup = Powerup.values()[pick];
		powerupID = pick;
		System.out.println(pup.getColor());
		refImage = SpriteLoader.getPowerupImage(pup.getColor());
	}
	
	public void spawn(int x, int y) {
		limit = 0;
		pickPowerup();
		repaint();
		setLocation(x - getWidth() / 2, y - getWidth() / 2);
		setVisible(true);
	}
	
	public void pushLimit() {
		if(limit > 120)
			despawn();
		limit++;
	}
	
	public void despawn() {
		setVisible(false);
	}
}
