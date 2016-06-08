package solarstriker.views.sound;

import solarstriker.model.SSMainModel;
import solarstriker.model.SSShipHandler;
import solarstriker.views.SSEnemyView;


public class SoundEngine {
	
	SSShipHandler ship;
	SSMainModel model;
	SSEnemyView enemy;
	
	
	public SoundEngine(SSMainModel m, SSShipHandler mship, SSEnemyView mfoe) {
		model = m;
		ship = mship;
		enemy = mfoe;
		
	}
	 
	/*public void playEnemyDeath() {
		
		if(enemy.isDying() == true) {
			InputStream in;
			try {
				in = new FileInputStream("Explosion sound effect.mp3");
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
			// Create an AudioStream object from the input stream.
			AudioStream as;
			try {
				as = new AudioStream(in);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}         
			// Use the static class member "player" from class AudioPlayer to play
			// clip.
			AudioPlayer.player.start(as);            
			AudioPlayer.player.stop(as);
			
		}
		
	}*/
	
	
	
	public void playshipDeath() {
		
	}
	
	public void playshipFiring() {
		
	}
	

}
