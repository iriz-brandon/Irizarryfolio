package solarstriker.views.animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class SpriteLoader {
	public static SpriteLoader instance;
    private static BufferedImage spriteSheet;
	private static BufferedImage bulletImage, shipImage, powerupImageDefault;
	private static BufferedImage[] enemyImage, bossImage;
	private static BufferedImage[] deathSprite;
	private static HashMap<String, BufferedImage> resources = new HashMap<String, BufferedImage>();
	private static HashMap<Color, BufferedImage> powerupImage;
	
    public static final int TILE_SIZE = 96;
    
    public SpriteLoader() {
    	instance = this;
    }
	
    public static BufferedImage loadSprite(String file) {
    	if(resources == null)
    		resources = new HashMap<String, BufferedImage>();
    	
        BufferedImage sprite = null;
        if(!resources.containsKey(file)) {
	        try {
	        	String ext = file.endsWith(".jpg") ? "" : ".png";
	        	InputStream stream = instance.getClass().getClassLoader().getResourceAsStream(file + ext);
	            sprite = ImageIO.read(stream);
	            resources.put(file, sprite);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        } else
        	sprite = resources.get(file);

        return sprite;
    }
    
    public static BufferedImage recolor(BufferedImage image, Color color) {
    	int w = image.getWidth(), h = image.getHeight();

    	for(int i = 0; i < w; i++)
    	    for(int j = 0; j < h; j++) {
    	        int oldColor = image.getRGB(i, j);
    	        /*int r = (oldColor	   ) & 0xFF & color.getRed();
    	        int g = (oldColor >> 8 ) & 0xFF & color.getGreen();
    	        int b = (oldColor >> 16) & 0xFF & color.getBlue();
    	        int a = (oldColor >> 24) & 0xFF & color.getAlpha();
    	        int newColor = (a << 24) | (r << 16) | (g << 8) | b;*/
    	        int a = (oldColor & 0xff000000) >> 24; // & color.getAlpha();
    	    	int r = (oldColor & 0x00ff0000) >> 16 & color.getRed();
    	    	int g = (oldColor & 0x0000ff00) >> 8 & color.getGreen();
    	    	int b = (oldColor & 0x000000ff) & color.getBlue();
    	        int newColor = (a << 24) | (r << 16) | (g << 8) | b;
    	        image.setRGB(i, j, newColor);
    	    }
    	
    	return image;
    }
    
	public static BufferedImage getBulletImage() {
		if(bulletImage == null)
			bulletImage = loadSprite("bullet");
		
		return bulletImage;
	}
    
	public static BufferedImage getShipImage() {
		if(shipImage == null)
			shipImage = loadSprite("mob_4"); //recolor(loadSprite("mob_4"), Color.MAGENTA);
		
		return shipImage;
	}
	
	public static BufferedImage getPowerupImageDefault() {
		if(powerupImageDefault == null)
			powerupImageDefault = resize(loadSprite("powerup"), 50, 50);
		
		return powerupImageDefault;
	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
	
	public static BufferedImage getPowerupImage(Color c) {
		if(powerupImage == null)
			powerupImage = new HashMap<Color, BufferedImage>();
			
		if(!powerupImage.containsKey(c)) {
			if(c == Color.WHITE)
				powerupImage.put(c, getPowerupImageDefault());
			else
				powerupImage.put(c, recolor(getPowerupImageDefault(), c));
		}
		
		return powerupImage.get(c);
	}
	
	public static BufferedImage getEnemyImage(int enemyID) {
		if(enemyImage == null)
			enemyImage = new BufferedImage[20];
		
		if(enemyImage[enemyID] == null)
			enemyImage[enemyID] = loadSprite("mob_" + enemyID);
		
		return enemyImage[enemyID];
	}
	
	public static BufferedImage getBossImage(int bossID) {
		if(bossImage == null)
			bossImage = new BufferedImage[20];
		
		if(bossImage[bossID] == null)
			bossImage[bossID] = loadSprite("boss_" + bossID);
		
		return bossImage[bossID];
	}

    public static BufferedImage[] getDeathSprites() {
        if (spriteSheet == null)
            spriteSheet = loadSprite("deathanimation");
        
        if(deathSprite == null) {
	    	int r = (int) (spriteSheet.getHeight(null) / (double)TILE_SIZE);
	    	int c = (int) (spriteSheet.getWidth(null) / (double)TILE_SIZE);
	    	
	    	deathSprite = new BufferedImage[r * c + 1];
	    	for(int j = 0; j < r; j++)
	        	for(int i = 0; i < c; i++)
	        		deathSprite[i + j * c] = getSprite(i, j);
	    	
	    	deathSprite[deathSprite.length - 1] = deathSprite[deathSprite.length - 2];
        }
    	
    	return deathSprite;
    }
    
    public static BufferedImage getSprite(int xGrid, int yGrid) {
        if (spriteSheet == null)
            spriteSheet = loadSprite("deathanimation");

        return spriteSheet.getSubimage(xGrid * TILE_SIZE, yGrid * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

}