package fr.masterfire.player;

import java.awt.*;

import fr.masterfire.math.Vector;

public class Bullet{

	private int sender;
	
	private Vector playerVector;
	public Vector dirVector;
	public Vector bulletVector;
	
	private int mX, mY;
	private float playerX, playerY;
	
	private float timeStep = 1f / 60f;
	private float speed = 900f;

	public Bullet(int playerID, int mX, int mY, float playerX, float playerY) {
		this.sender = playerID;
		this.mX = mX;
		this.mY = mY;
		this.playerX = playerX;
		this.playerY = playerY;
		setUpVector();
	}
	
	private void setUpVector() {
		playerVector = new Vector((float) playerX + 25, (float) playerY + 25);

		if (mY != 0 && mX != 0) {
			dirVector = new Vector(mX, mY);
		}
		if (dirVector != null) {
			dirVector.x -= playerVector.x;
			dirVector.y -= playerVector.y;
			dirVector.normalize();
		}

		bulletVector = new Vector(playerVector.x, playerVector.y);

	}
	
	public void tick() {
		if (bulletVector != null && dirVector != null) {
			bulletVector.x += speed * dirVector.x * timeStep;
			bulletVector.y += speed * dirVector.y * timeStep;
		}
	}

	public void render(Graphics g) {
		g.setColor(Color.red);
		g.fillOval((int) bulletVector.x, (int) bulletVector.y, 15, 15);
	}

	public int getSender() {
		return this.sender;
	}

}
