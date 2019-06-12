package fr.masterfire.net.packets.ServerPackets;

import java.net.InetAddress;

import fr.masterfire.net.GameClient;
import fr.masterfire.net.GameServer;
import fr.masterfire.net.packets.Packet;

public class Packet00Login extends Packet{

	private String username;
	private int x, y;
	private int playerID;
	
	public Packet00Login(byte[] data) {
		super(00);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x = Integer.parseInt(dataArray[1]);
		this.y = Integer.parseInt(dataArray[2]);
		this.playerID = Integer.parseInt(dataArray[3]);
	}

	public Packet00Login(String username, int x, int y, int playerID) {
		super(00);
		this.username = username;
		this.x = x;
		this.y = y;
		this.playerID = playerID;
	}
	
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClient(getData());
	}

	public void writeDataWithout(GameServer gameServer, InetAddress address, int port) {
		gameServer.sendToAllWithout(getData(), address, port);
	}
	
	public byte[] getData() {
		//System.out.println(playerID);
		return ("00" + this.username + "," + getX() + "," + getY() + "," + getPlayerID()).getBytes();
	}

	public String getUsername() {
		return username;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getPlayerID() {
		return playerID;
	}
}
