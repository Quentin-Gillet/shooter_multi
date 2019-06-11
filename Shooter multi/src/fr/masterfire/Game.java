package fr.masterfire;

import java.awt.Canvas;
import java.util.ArrayList;


import fr.masterfire.net.GameClient;
import fr.masterfire.net.GameServer;
import fr.masterfire.player.Player;

public class Game extends Canvas{

	private static final long serialVersionUID = 5603565997020721039L;
	
	public static final int WIDTH = 800, HEIGHT = 600;
	public static Game game;

	public ArrayList<Player> players = new ArrayList<Player>();
	public Player player;

	public int playerID;

	public GameServer socketServer;
	public GameClient socketClient;

	public Game() {
		game = this;
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

	public static void main(String args[]) {
		new Game();
	}

}
