package fr.masterfire.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fr.masterfire.player.Player;


public class KeyInput extends KeyAdapter {

	private Player player;

	private boolean uP = false;
	private boolean dP = false;
	private boolean lP = false;
	private boolean rP = false;

	public KeyInput(Player player) {
		this.player = player;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_Z) {
			uP = true;
			player.setVelY(-5);
		}
		if (key == KeyEvent.VK_S) {
			dP = true;
			player.setVelY(5);
		}
		if (key == KeyEvent.VK_Q) {
			lP = true;
			player.setVelX(-5);
		}
		if (key == KeyEvent.VK_D) {
			rP = true;
			player.setVelX(5);
		}

	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (key == KeyEvent.VK_Z){
            uP = false;
            if (dP){
                player.setVelY(5);
            }else {
                player.setVelY(0);
            }
        }
        if (key == KeyEvent.VK_S){
            dP = false;
            if (uP){
                player.setVelY(-5);
            }else {
                player.setVelY(0);
            }
        }
        if (key == KeyEvent.VK_Q){
            lP = false;
            if (rP){
                player.setVelX(5);
            }else {
                player.setVelX(0);
            }
        }
        if (key == KeyEvent.VK_D){
            rP = false;
            if (lP){
                player.setVelX(-5);
            }else {
                player.setVelX(0);
            }
        }
		
	}

}
