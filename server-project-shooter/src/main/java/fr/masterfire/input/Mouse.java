package fr.masterfire.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import fr.masterfire.Game;

public class Mouse implements MouseListener {

	private int mouseX;
	private int mouseY;

	private Game game;

	public Mouse(Game game) {
		this.game = game;
	}

	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		game.player.shoot();
	}

	public void mouseReleased(MouseEvent e) {
		mouseX = 0;
		mouseY = 0;
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
