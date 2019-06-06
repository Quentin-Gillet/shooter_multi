package fr.masterfire;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;

import fr.masterfire.main.Window;
import fr.masterfire.net.GameClient;
import fr.masterfire.net.GameServer;
import fr.masterfire.net.packets.ServerPackets.Packet00Login;
import fr.masterfire.net.packets.ServerPackets.Packet05Ask;
import fr.masterfire.player.Player;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 5804336248501895179L;

	public static final int WIDTH = 640, HEIGHT = 480;
	public static Game game;

	public ArrayList<Player> players = new ArrayList<Player>();

	public Player player;
	public Window frame;

	private int rX, rY;
	public int playerID;

	private Thread thread;
	private boolean running = false;

	public GameServer socketServer;
	public GameClient socketClient;

	public Game() {
		game = this;
		frame = new Window(WIDTH, HEIGHT, "Game dev", this);

		this.rX = ThreadLocalRandom.current().nextInt(70, 590);
		this.rY = ThreadLocalRandom.current().nextInt(70, 430);

		Packet05Ask askPacket = new Packet05Ask("playerID");
		askPacket.writeData(socketClient);

		String username;
		
		do {
			username = JOptionPane.showInputDialog(this, "Entrer un nom d'utilisateur");
		} while (!username.equals("") || username != null);
		
		player = new Player(this.rX, this.rY, 50, Color.blue, username, playerID, null, -1, true);
		getPlayers().add(player);
		Packet00Login loginPacket = new Packet00Login(player.getUsername(), player.getX(), player.getY(), player.getPlayerID());
		if (socketServer != null) {
			socketServer.addConnection((Player) player, loginPacket);
		}
		loginPacket.writeData(socketClient);
		game.frame.frame.setTitle("Game dev Player#" + player.getPlayerID() + " : " + player.getUsername());
	}

	public void start() {
		if (JOptionPane.showConfirmDialog(this, "Voulez vous lancez le server ?") == 0) {
			socketServer = new GameServer();
			socketServer.start();
			System.out.println("SERVER >>> START");
		}

		socketClient = new GameClient(this, "localhost");
		socketClient.start();

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

		if (getPlayers().size() > 0) {
			for (int i = 0; i < getPlayers().size(); i++) {
				getPlayers().get(i).render(g);
			}
		}

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
