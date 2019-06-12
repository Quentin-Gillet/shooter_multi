package fr.masterfire.net;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import fr.masterfire.Game;
import fr.masterfire.net.packets.Packet;
import fr.masterfire.net.packets.Packet.PacketTypes;
import fr.masterfire.net.packets.PlayerPackets.Packet02Move;
import fr.masterfire.net.packets.PlayerPackets.Packet03Shoot;
import fr.masterfire.net.packets.PlayerPackets.Packet04Health;
import fr.masterfire.net.packets.PlayerPackets.Packet06Respawn;
import fr.masterfire.net.packets.ServerPackets.Packet00Login;
import fr.masterfire.net.packets.ServerPackets.Packet01Disconnect;
import fr.masterfire.player.Bullet;
import fr.masterfire.player.Player;

public class GameClient extends Thread {

	private InetAddress ipAddress;
	private DatagramSocket socket;
	private Game game;

	private int packetRespawn = 0;
	
	public GameClient(Game game, String ipAddress) {
		this.game = game;
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes packetTypes = Packet.lookuPacket(message.substring(0, 2));
		Packet packet;
		switch (packetTypes) {
		case INVALID:
			System.out.println("GameServer.parsePacket() invalid CLIENT " + message);
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			handleLogin((Packet00Login) packet, address, port);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet01Disconnect) packet).getUsername() + " a quitt� la partie...");
			game.removePlayer(((Packet01Disconnect) packet).getUsername());
			break;
		case MOVE:
			packet = new Packet02Move(data);
			handleMove((Packet02Move) packet);
			break;
		case SHOOT:
			packet = new Packet03Shoot(data);
			handleShoot((Packet03Shoot) packet);
			break;
		case HEALTH:
			packet = new Packet04Health(data);
			handleHealth((Packet04Health) packet);
			break;
		case ASK:
			game.playerID = Integer.parseInt(message.substring(2));
			break;
		case RESPAWN:
			packet = new Packet06Respawn(data);
			handleRespawn((Packet06Respawn) packet);
			break;
		default:
			System.out.println("DEFAULT CLIENT");
			break;
		}
	}

	private void handleRespawn(Packet06Respawn packet) {
		packetRespawn++;
		if (packetRespawn == 1) {
			if (packet.getLastPlayerIDTouch() == game.player.getPlayerID()) {
				game.kills++;
			}
		}
		if (packetRespawn == 5) {
			game.getPlayers().add(new Player(packet.getX(), packet.getY(), 50, Color.blue, packet.getUsername(), packet.getPlayerID(), packet.getAddress(), packet.getPort(), false));
			packetRespawn = 0;
		}
	}

	private void handleHealth(Packet04Health packet) {
		for (int i = 0; i < game.players.size(); i++) {
			Player p = game.players.get(i);
			if (p.getPlayerID() == packet.getPlayerID()) {
				p.setHealth(packet.getHealth());
				if (packet.getHealth() <= 0) {
					game.getPlayers().remove(packet.getPlayerID());
				}
			}
		}
	}

	private void handleShoot(Packet03Shoot packet) {
		for (Player p : game.getPlayers()) {
			if (p.getPlayerID() == game.player.getPlayerID()) {
				game.player.bullets.add(new Bullet(packet.getSender(), packet.getmX(), packet.getmY(), packet.getPlayerX(), packet.getPlayerY()));
			}
		}
	}

	private void handleLogin(Packet00Login packet, InetAddress address, int port) {
		System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getUsername() + " a rejoint la partie...");
		Player player = new Player(packet.getX(), packet.getY(), 50, Color.BLUE, packet.getUsername(), packet.getPlayerID(), address, port, false);
		game.players.add(player);
	}

	private void handleMove(Packet02Move packet) {
		this.game.movePlayer(packet.getUsername(), packet.getX(), packet.getY());
	}

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1545);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		socket.close();
	}

}
