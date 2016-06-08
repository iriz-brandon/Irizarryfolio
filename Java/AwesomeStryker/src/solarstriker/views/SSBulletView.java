package solarstriker.views;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import solarstriker.model.SSMainModel;
import solarstriker.views.animation.SpriteLoader;

public class SSBulletView extends JPanel { // View
	private static final long serialVersionUID = -135998038499949161L;
	private SSMainModel model;
	private BufferedImage refImage;
	private Polygon bounds;
	final int bulletSpeed = 20;
	final int boundingOffset = 0;
	private boolean own;
	private int theta;
	
	public SSBulletView(SSMainModel model, SSMainView view) {
		this.model = model;
		//this.view = view;
		refImage = SpriteLoader.getBulletImage();
		setSize(refImage.getWidth(null), refImage.getHeight(null));
		
		setPhysicsBody();
		//System.out.println("The W and H: " + getWidth() + ", " + getHeight());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int angle = theta % 360;
		if(angle == 0) {
			g.drawImage(refImage, 0, 0, null);
		} else {
			Graphics2D g2d = (Graphics2D)g;
			
			if(!model.getRotates().containsKey(angle)) {
				double rotationRequired = Math.toRadians(angle);
				AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, getWidth() / 2,  getHeight() / 2);
				model.getRotates().put(angle, tx);
			}
			// Drawing the rotated image at the required drawing locations
			g2d.drawImage(refImage, model.getRotates().get(angle), null);
		} 
		//setLocation(getX(), getY() - bulletSpeed); 
	} 

	public int getTheta() {
		return theta;
	}

	public void setTheta(int theta) {
		this.theta = theta;
	}
	
	public int getSpeed(){
		return bulletSpeed;
	}
	
	public Rectangle getPhysicsBody() {
		Rectangle rect = getBounds();
		rect.setLocation(getX(), getY());
		return rect;
	}
	
	public void setPhysicsBody() {
		Rectangle rect = getBounds();
		rect.x += boundingOffset * 2;
		rect.y += boundingOffset * 2;
		rect.height -= boundingOffset;
		rect.width -= boundingOffset;
		bounds = new Polygon();
		bounds.addPoint(rect.x, rect.y);
		bounds.addPoint(rect.x + rect.width, rect.y);
		bounds.addPoint(rect.x + rect.width, rect.y + rect.height);
		bounds.addPoint(rect.x, rect.y + rect.height);
	}

	public boolean isOwn() {
		return own;
	}

	public void setOwn(boolean own) {
		this.own = own;
	}
}
