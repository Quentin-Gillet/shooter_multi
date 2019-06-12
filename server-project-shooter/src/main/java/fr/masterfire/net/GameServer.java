package fr.masterfire.net;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import fr.masterfire.net.packets.Packet;
import fr.masterfire.net.packets.Packet.PacketTypes;
import fr.masterfire.net.packets.PlayerPackets.Packet02Move;
import fr.masterfire.net.packets.PlayerPackets.Packet03Shoot;
import fr.masterfire.net.packets.PlayerPackets.Packet04Health;
import fr.masterfire.net.packets.PlayerPackets.Packet06Respawn;
import fr.masterfire.net.packets.ServerPackets.Packet00Login;
import fr.masterfire.net.packets.ServerPackets.Packet01Disconnect;
import fr.masterfire.net.packets.ServerPackets.Packet05Ask;
import fr.masterfire.player.Player;

public class GameServer extends Thread {

	private DatagramSocket socket;
	private List<Player> connectedPlayers = new ArrayList<Player>();

	private int playerID = 0;
	
	public GameServer() {
		try {
			this.socket = new DatagramSocket(1545);
		} catch (SocketException e) {
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
			System.out.println("GameServer.parsePacket() invalid SERVER " + message);
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet00Login) packet).getUsername() + " est connecté...");
			Player player;
			player = new Player(((Packet00Login) packet).getX(), ((Packet00Login) packet).getY(), 50, Color.BLUE, ((Packet00Login) packet).getUsername(), playerID, address, port, true);
			if (player != null) {
				this.addConnection(player, ((Packet00Login) packet));
				playerID++;
			}
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet01Disconnect) packet).getUsername() + " est déconnecté...");
			this.removeConnection((Packet01Disconnect) packet);
			break;
		case MOVE:
			packet = new Packet02Move(data);
			this.handleMove(((Packet02Move) packet));
			break;
		case SHOOT:
			packet = new Packet03Shoot(data);
			packet.writeData(this);
			break;
		case HEALTH:
			packet = new Packet04Health(data);
			packet.writeDataWithout(this, address, port);
			break;
		case ASK:
			packet = new Packet05Ask(data);
			if (message.substring(2).equals("playerID")) {
				String dataString = new String("05" + playerID);
				sendData(dataString.getBytes(), address, port);
			}
			break;
		case RESPAWN:
			packet = new Packet06Respawn(data);
			packet.writeDataWithout(this, address, port);
			break;
		default:
			System.out.println("GameServer.parsePacket() DEFAULT SERVER " + message);
			break;
		}
	}

	private void handleMove(Packet02Move packet) {
		if (getPlayer(packet.getUsername()) != null) {
			int index = getPlayerIndex(packet.getUsername());
			this.connectedPlayers.get(index).setX(packet.getX());
			this.connectedPlayers.get(index).setY(packet.getY());
			packet.writeData(this);
		}
	}

	public void removeConnection(Packet01Disconnect packet) {
		this.connectedPlayers.remove(getPlayerIndex(packet.getUsername()));
		packet.writeData(this);
	}

	public int getPlayerIndex(String username) {
		int index = 0;
		for (Player player : this.connectedPlayers) {
			if (player.getUsername().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

	public Player getPlayer(String username) {
		for (Player player : this.connectedPlayers) {
			if (player.getUsername().equals(username)) {
				return player;
			}
		}
		return null;
	}

	public void addConnection(Player player, Packet00Login packet) {
		boolean alreadyConnected = false;
		for (Player p : this.connectedPlayers) {
			if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}

				if (p.port == -1) {
					p.port = player.port;
				}
				p.playerID = player.getPlayerID();
				alreadyConnected = true;
			} else {
				sendData(packet.getData(), p.ipAddress, p.port);
				packet = new Packet00Login(p.getUsername(), p.getX(), p.getY(), p.getPlayerID());
				sendData(packet.getData(), player.ipAddress, player.port);
			}
		}
		if (!alreadyConnected) {
			player.setPlayerID(playerID);
			this.connectedPlayers.add(player);
		}
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToAllWithout(byte[] data, InetAddress address, int port) {
		for (Player p : connectedPlayers) {
			if (p.ipAddress != address && p.port != port) {
				sendData(data, p.ipAddress, p.port);
			}
		}
	}
	
	public void sendDataToAllClient(byte[] data) {
		for (Player p : connectedPlayers) {
			sendData(data, p.ipAddress, p.port);
		}
	}

}
