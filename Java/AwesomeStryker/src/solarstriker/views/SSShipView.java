package solarstriker.views;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import solarstriker.model.SSMainModel;
import solarstriker.model.SSShipHandler;
import solarstriker.views.animation.AnimationEngine;
import solarstriker.views.animation.SpriteLoader;

public class SSShipView extends JPanel { // View
	private static final long serialVersionUID = 4000859714473985282L;
	//private HashMap<Integer, AffineTransform> rotates;
	private BufferedImage refImage;
	private SSMainModel model;
	private SSShipHandler ship;
	private Polygon bounds;
	private AnimationEngine death;
	final int boundingOffset = 15;
	
	public SSShipView(SSMainModel model, SSMainView view) {
		this.model = model;
		//rotates = model.getRotates();
		
		refImage = SpriteLoader.getShipImage();
		setSize(refImage.getWidth(null), refImage.getHeight(null));
		
		model.setShipSize(getWidth(), getHeight());
		setPhysicsBody();
	}
	
	public Polygon getPhysicsBody() {
		//Rectangle rect = getBounds();
		//rect.setLocation(getX(), getY());
		setPhysicsBody();
		return bounds;
	}
	
	public void setPhysicsBody() {
		Rectangle rect = getBounds();
		rect.x += boundingOffset * 2;
		rect.y += boundingOffset * 2;
		rect.height -= boundingOffset;
		rect.width -= boundingOffset;
		
		bounds = new Polygon();
		bounds.addPoint(rect.x + rect.width, rect.y + rect.height);
		bounds.addPoint(rect.x + rect.width / 2, rect.y);
		bounds.addPoint(rect.x, rect.y + rect.height);
	}
	
	/**
	 * This will be used to paint the ship onto the JPanel initially.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(ship != null && (ship.isShipAlive() || !ship.isDying())) {
			//int angle = ship.getAngle() % 360;
			//paints the ship image on the game.
			if(ship.getShield() > 0) {
				g.setColor(Color.BLUE);
				int compress = 20;
				g.drawOval(compress, compress, getWidth() - compress * 2, getHeight() - compress * 2);
				
				g.drawImage(refImage, refImage.getWidth(null) / 2, refImage.getWidth(null) / 2, null);
			} else
				g.drawImage(refImage, 0, 0, null);
		} else if(death != null) {
			g.drawImage(death.getSprite(), 0, 0, null);
		}
	}
	
	public void update() {
		if(ship == null)
			ship = model.getShip();
		
		setLocation(model.getShip().getX(), model.getShip().getY());
		ship.decreaseBuffer();
		ship.approveBounds();
		
		if(!ship.isDying()) {
			if(ship.getShield() > 0)
				setSize(refImage.getWidth(null) * 2, refImage.getHeight(null) * 2);
			else
				setSize(refImage.getWidth(null), refImage.getHeight(null));
		}
		
		if(death != null) {
			if(!death.isAnimating() && ship.isDying())
				ship.respawn();
			death.update();
		}
	}
	
	public void animatedDeath() {
		if(death == null)
			death = new AnimationEngine(SpriteLoader.getDeathSprites(), 1);
		
		setSize(SpriteLoader.TILE_SIZE, SpriteLoader.TILE_SIZE);
		death.setRepeatable(false);
		death.start();
	}
}
