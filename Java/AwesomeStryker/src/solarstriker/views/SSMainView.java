package solarstriker.views;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import solarstriker.controller.SSBulletController;
import solarstriker.controller.SSEndGameController;
import solarstriker.controller.SSHomeController;
import solarstriker.controller.SSMenuController;
import solarstriker.controller.SSPanelController;
import solarstriker.model.SSEnemyHandler;
import solarstriker.model.SSKeyHandler;
import solarstriker.model.SSMainModel;
import solarstriker.views.animation.SpriteLoader;

public class SSMainView extends JFrame {
	private static final long serialVersionUID = 6429088028244092454L;
	private SSMainModel model;
	private int globalTicks = 0;
	private int ticks = -20;
	private SSGameView gamePanel;
    private SSEnemyHandler enemyHandler;
    private SSMenuController ssMenu;
    private SSPanelView statusBar;
    private JPanel homeView;
    private JPanel playButtonHolder;
    private JLabel playButton;
    public JButton endGameButton;
    public JDialog endGameDialog;
    private SSEndGameController endGameController;
    
	Image backgroundImg = (Image)SpriteLoader.loadSprite("SolarStrikerArt_01.jpg"); 
	Image backGroundImgResize = backgroundImg.getScaledInstance(600, 450,  java.awt.Image.SCALE_SMOOTH);
	ImageIcon backgroundImgAsIcon = new ImageIcon(backGroundImgResize);
	
	Image playButtonImg = (Image)SpriteLoader.loadSprite("play_button"); 
	Image playButtonImgResize = playButtonImg.getScaledInstance(450, 70,  java.awt.Image.SCALE_SMOOTH);
	ImageIcon playButtonImgAsIcon = new ImageIcon(playButtonImgResize);
	
	Image playButton2Img = (Image)SpriteLoader.loadSprite("play_button2"); 
	Image playButton2ImgResize = playButton2Img.getScaledInstance(450, 70,  java.awt.Image.SCALE_SMOOTH);
	ImageIcon playButton2ImgAsIcon = new ImageIcon(playButton2ImgResize);
	
	public SSMainView(SSMainModel model) { // View
		super("Solar Stryker");
		this.model = model;
		
	 //Creating the home screen	
	    JLabel homeViewBackground = new JLabel();
	    homeViewBackground.setSize(600, 550);
	    homeViewBackground.setIcon(backgroundImgAsIcon);
	    homeViewBackground.setOpaque(true);
	    
	    playButton = new JLabel(playButtonImgAsIcon, JLabel.CENTER);
	    playButton.setBackground(Color.WHITE);
	    
	    playButtonHolder =  new JPanel();
	    playButtonHolder.setSize(600, 50);
	    playButtonHolder.setPreferredSize(new Dimension(600, 50));
	    playButtonHolder.setLayout(new BorderLayout());
	    playButtonHolder.setOpaque(true);
	    playButtonHolder.setBackground(Color.WHITE);
	    playButtonHolder.add(playButton, BorderLayout.CENTER);
	    //playButtonHolder.add(fillerLabel1, BorderLayout.EAST);
	    //playButtonHolder.add(fillerLabel2, BorderLayout.WEST);
	    
	    homeView = new JPanel();
	    homeView.setLayout(new BorderLayout());
	    setBackground(Color.WHITE);
	    homeView.add(homeViewBackground, BorderLayout.NORTH);
	    homeView.add(playButtonHolder, BorderLayout.CENTER);
	    repaint();
	    validate();
		
	//Creating the playable area	
		//TOP NAVIGATION BAR/NORTH:
		//Lets the user navigate the app and pause
		JMenuBar menuBar = new JMenuBar();
	    setJMenuBar(menuBar);   
	    ssMenu = new SSMenuController(model, "Menu", this);
	    menuBar.add(ssMenu);

        
        /* CENTER OF SCREEN/CENTER:
         * The panel where the user plays the game
         * The playable game area.
         */
        gamePanel = new SSGameView(model, this);
        add(gamePanel, BorderLayout.CENTER);
        gamePanel.setBackground(Color.BLACK);
        Dimension size = gamePanel.getSize();
        model.setSize(size.width, size.height); 
        
        /* BOTTOM OF SCREEN/SOUTH:
         * A status bar for telling user what happens.
         * Still need to make this a bit bigger
         */
		statusBar = new SSPanelView(model, this);
		
		endGameButton = new JButton("Close");
        
        displayPanel();
	}
	
	public void displayPanel() {
        if (model.getHomeFlag()) {
        	model.pause(false);
            System.out.println("Should display Home");
            add(homeView, BorderLayout.CENTER);
            remove(gamePanel);
            if(statusBar != null)
            	remove(statusBar);
            
            requestFocus();
            validate();
            repaint();
        } else if(model.getShip().getLives() > 0){
            System.out.println("Should display Game");
            if(!model.getHomeFlag()) {
	            remove(homeView); 
	            add(gamePanel, BorderLayout.CENTER);
	            add(statusBar, BorderLayout.SOUTH);
            }
            
            requestFocus();
            validate();
            repaint();
        } else {
        	model.pause(true);
            //add(homeView, BorderLayout.CENTER);
        	//remove(gamePanel);
        	//remove(statusBar);

          	endGameDialog = new JDialog();
          	endGameDialog.addWindowListener(endGameController);
          	endGameDialog.setLocationRelativeTo(null);
        	endGameDialog.setSize(200, 120);
        	endGameDialog.setLayout(new BorderLayout());
        	
        	JLabel endGameBoxText = new JLabel("Game Over!"); 
        	endGameBoxText.setHorizontalAlignment(JLabel.CENTER);
        	endGameBoxText.setPreferredSize(new Dimension(120, 40));
        	endGameDialog.add(endGameBoxText, BorderLayout.NORTH);
        	
        	JLabel endGameBoxText2 = new JLabel("Score: " + model.getScoreCount());
        	endGameBoxText2.setHorizontalAlignment(JLabel.CENTER);
        	endGameBoxText2.setPreferredSize(new Dimension(120, 40));
        	endGameDialog.add(endGameBoxText2, BorderLayout.CENTER);
        	
        	endGameButton.setPreferredSize(new Dimension(120, 24));
        	endGameDialog.add(endGameButton, BorderLayout.SOUTH);
        	
        	endGameDialog.setVisible(true);
        }     
    }
    
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width,  height);
		
		model.setSize(width, height);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	public void addBullet(SSBulletView bullet) {
		gamePanel.add(bullet);
	}
	
	public void update() {
		repaint();
		
		if(model.isPaused() || model.getHomeFlag())
			return;
		
		if(enemyHandler == null)
			enemyHandler = model.getEnemyHandler();
		
		if(ticks > 80){
			SSEnemyView enemyView = new SSEnemyView(model, this, false);
			enemyView.setOpaque(false);
			enemyHandler.getEnemies().add(enemyView);
			gamePanel.add(enemyView);
			ticks = 0;
		}
		enemyHandler.update();
		gamePanel.update();
		ticks++;
		globalTicks++;
	}
	
	public SSGameView getGameView() { return gamePanel; }
	public SSPanelView getPanelView() { return statusBar; }
	public int getGlobalTicks() { return globalTicks; }

	
	public void registerListeners(SSKeyHandler inputController, SSHomeController homeController, SSPanelController panelController, SSEndGameController endGameController, SSBulletController bulletController) {
		this.endGameController = endGameController;
		addKeyListener(inputController);
		this.endGameButton.addActionListener(endGameController);
		//model.registerPanelController(panelController);
		
		this.playButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				model.adjustShowingHome(false);
				displayPanel();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				playButton.setIcon(playButton2ImgAsIcon);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				playButton.setIcon(playButtonImgAsIcon);
			}
		});
	}
}		
