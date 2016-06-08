package solarstriker;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.Timer;

import solarstriker.controller.SSBulletController;
import solarstriker.controller.SSEndGameController;
import solarstriker.controller.SSHomeController;
import solarstriker.controller.SSPanelController;
import solarstriker.model.SSKeyHandler;
import solarstriker.model.SSMainModel;
import solarstriker.views.SSMainView;
import solarstriker.views.animation.SpriteLoader;

public class SolarStriker { // Main
	public static Timer timer = null;
	public static SpriteLoader loader = new SpriteLoader();
	public static BufferedImage bulletImage;
	public static BufferedImage[] enemyImage;
	
	public static void main(String[] args) {
		SSMainModel model = new SSMainModel();
 		SSMainView view = new SSMainView(model);
		
		SSKeyHandler inputController = new SSKeyHandler(model, view);
		SSRepainter repaintController = new SSRepainter(model, view);
		SSBulletController bulletController = new SSBulletController(model, view);
		SSPanelController panelController = new SSPanelController(model, view);
		SSHomeController homeController = new SSHomeController(model, view);
		SSEndGameController endGameController = new SSEndGameController(model,view);
		
		timer = new Timer(25, repaintController);
	    timer.start();
		
		view.registerListeners(inputController, homeController, panelController, endGameController, bulletController);
		
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setSize(600, 700);
		view.setResizable(false);
		view.setLocationRelativeTo(null);
		view.setVisible(true);
		
        view.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	timer.stop();
            	System.exit(0);
            }
        });
	}
}