package fr.masterfire.player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.net.InetAddress;
import java.util.ArrayList;

import fr.masterfire.Game;
import fr.masterfire.input.KeyInput;
import fr.masterfire.input.Mouse;
import fr.masterfire.net.packets.PlayerPackets.Packet02Move;
import fr.masterfire.net.packets.PlayerPackets.Packet03Shoot;
import fr.masterfire.net.packets.PlayerPackets.Packet04Health;
import fr.masterfire.player.Bullet;

public class Player {

	public ArrayList<Bullet> bullets = new ArrayList<>();

	private int size;
	private int x, y;
	private int velX, velY;

	private Color color;
	public Mouse mouse;
	private Game game;

	private int lastPlayerIDTouch;
	
	private String username;
	public int playerID;
	
	public int port;
	public InetAddress ipAddress;

	public int healt;
	public int bounds = 0;
	public int oldHealt;
	
	private int cooldown = 20;
	
	public Player(int x, int y, int size, Color color, String username, int playerID, InetAddress ipAddress, int port, boolean keyBoard) {
		this.game = Game.game;
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = color;
		this.username = username;
		this.playerID = playerID;
		this.port = port;
		this.ipAddress = ipAddress;
		this.healt = 100;
		this.oldHealt = healt;

		if (keyBoard) {
			mouse = new Mouse(game);
			game.addKeyListener(new KeyInput(this));
			game.addMouseListener(mouse);
		}
	}

	public void shoot() {
		if (cooldown >= 20) {
			Packet03Shoot packet = new Packet03Shoot(playerID, mouse.getMouseX(), mouse.getMouseY(), this.x, this.y);
			packet.writeData(game.socketClient);
			cooldown = 0;
		}
	}

	public void tick() {
		healt = Game.clamp(healt, 0, 100);
		
		x += velX;
		y += velY;

		x = Game.clamp((int) x, 0, Game.WIDTH - 66);
		y = Game.clamp((int) y, 25, Game.HEIGHT - 89);
		
		if (velX != 0 || velY != 0) {
			Packet02Move packet = new Packet02Move(this.getUsername(), this.x, this.y);
			packet.writeData(game.socketClient);
		}

		for (int i = 0; i < bullets.size(); i++) {
			if (bullets.get(i).bulletVector.x <= 0 || bullets.get(i).bulletVector.x >= Game.WIDTH || bullets.get(i).bulletVector.y <= 0 || bullets.get(i).bulletVector.y >= Game.HEIGHT) {
				bullets.remove(i);
			} else {
				bullets.get(i).tick();
				collision();
			}
		}
		cooldown++;

		if (healt != oldHealt && healt != 100) {
			Packet04Health packet = new Packet04Health(this.playerID, this.healt);
			packet.writeData(game.socketClient);
		}
		
		oldHealt = healt;
	}

	private void collision() {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			if (intersects(this, b) && b.getSender() != playerID) {
				bullets.remove(i);
				this.lastPlayerIDTouch = b.getSender();
				this.healt -= 10;
			}
		}
	}
	
	public boolean intersects(Player rectangle, Bullet circle)
	{
		float DeltaX = circle.bulletVector.x - Math.max(rectangle.x, Math.min(circle.bulletVector.x, rectangle.x + rectangle.size));
		float DeltaY = circle.bulletVector.y - Math.max(rectangle.y, Math.min(circle.bulletVector.y, rectangle.y + rectangle.size));
		return (DeltaX * DeltaX + DeltaY * DeltaY) < (15 / 2 * 15 / 2);
	}
	
	public void render(Graphics g) {
		g.setColor(color);
		g.fillRect((int) x, (int) y, size, size);

		if (!username.equals("pasdenom8617")) {
			g.setFont(new Font("arial", 1, 15));
			g.setColor(Color.black);
			g.drawString(username.toLowerCase(), x, (y - 15));
		}

		g.setColor(Color.GRAY);
		g.fillRect(x, (y - 10), 50, 5);
		g.setColor(Color.GREEN);
		g.fillRect(x, (y - 10), (int) healt / 2, 5);

		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getVelX() {
		return velX;
	}

	public void setVelX(int velX) {
		this.velX = velX;
	}

	public int getVelY() {
		return velY;
	}

	public void setVelY(int velY) {
		this.velY = velY;
	}

	public String getUsername() {
		return this.username;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public void setHealth(int health) {
		this.healt = health;
	}

	public int getHealth() {
		return this.healt;
	}

	public int getPort() {
		return port;
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public int getLastPlayerIDTouch() {
		return lastPlayerIDTouch;
	}


}
