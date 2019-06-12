package fr.masterfire;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import fr.masterfire.net.GameClient;
import fr.masterfire.net.GameServer;
import fr.masterfire.net.packets.PlayerPackets.Packet06Respawn;
import fr.masterfire.player.Player;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 5804336248501895179L;

	public static final int WIDTH = 800, HEIGHT = 600;
	public static Game game;

	public ArrayList<Player> players = new ArrayList<Player>();

	public Player player;

	public int kills;

	public int playerID;

	private Thread thread;
	private boolean running = false;

	public GameServer socketServer;
	public GameClient socketClient;

	private boolean respawn = false;

	private int respawnTime = -1;

	public Game() {
		game = this;
	}

	public void start() {

		socketServer = new GameServer();
		socketServer.start();
		
		thread = new Thread(this);
		running = true;
		thread.start();
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized ArrayList<Player> getPlayers() {
		return this.players;
	}

	public void tick() {
		if (getPlayers().size() > 0) {
			for (int i = 0; i < getPlayers().size(); i++) {
				getPlayers().get(i).tick();
			}
		}

		if (player != null) {

			if (player.getHealth() <= 0) {
				respawn = true;
				respawnTime = 5;
				respawn();
			}
			if (respawnTime == 0) {
				getPlayers().add(player);
				respawn = false;
				respawnTime = -1;
			}

		}
	}

	private void respawn() {
		removePlayer(player.getUsername());

		player.setX(ThreadLocalRandom.current().nextInt(20, WIDTH - 50));
		player.setY(ThreadLocalRandom.current().nextInt(20, HEIGHT - 50));
		player.setHealth(100);
	}

	private void render() {
		// CREATE GRAPHICS
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		if (respawn) {
			g.setFont(new Font("arial", 1, 30));
			g.setColor(Color.black);
			g.drawString("Respawn dans " + respawnTime + " seconde(s)", WIDTH / 2 - 220, HEIGHT / 2 - 5);
		}

		if (getPlayers().size() > 0) {
			for (int i = 0; i < getPlayers().size(); i++) {
				getPlayers().get(i).render(g);
			}
		}
		g.setFont(new Font("arial", 1, 15));
		g.setColor(Color.black);
		g.drawString("Kills: " + kills, 10, 15);

		g.dispose();
		bs.show();
	}

	public static int clamp(int var, int min, int max) {
		if (var >= max)
			return var = max;
		else if (var <= min)
			return var = min;
		return var;
	}

	public static float clamp(float var, float min, float max) {
		if (var >= max)
			return var = max;
		else if (var <= min)
			return var = min;
		return var;
	}

	@Override
	public void run() {
		// TICK METHOD
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amoutOfTicks = 60.0;
		double ns = 1000000000 / amoutOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		@SuppressWarnings("unused")
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			render();
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				if (respawnTime > 0) {
					respawnTime--;
					Packet06Respawn packet = new Packet06Respawn(this.player.getX(), this.player.getY(), this.player.getUsername(), this.player.getPlayerID(), this.player.getIpAddress(), this.player.getPort(), this.player.getLastPlayerIDTouch());
					packet.writeData(socketClient);
				}
				// System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		stop();
	}

	public void removePlayer(String username) {
		int index = 0;
		for (Player p : getPlayers()) {
			if (p.getUsername().equals(username)) {
				break;
			}
			index++;
		}
		this.getPlayers().remove(index);
	}

	private int getPlayerIndex(String username) {
		int index = 0;
		for (Player p : getPlayers()) {
			if (p.getUsername().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

	public void movePlayer(String username, int x, int y) {
		int index = getPlayerIndex(username);
		x = clamp((int) x, 0, WIDTH - 66);
		y = clamp((int) y, 0, HEIGHT - 89);
		this.getPlayers().get(index).setX(x);
		this.getPlayers().get(index).setY(y);
	}

	public static void main(String args[]) {
		new Game();
	}

}
