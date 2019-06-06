package fr.masterfire.net.packets.PlayerPackets;

import java.net.InetAddress;

import fr.masterfire.net.GameClient;
import fr.masterfire.net.GameServer;
import fr.masterfire.net.packets.Packet;

public class Packet04Health extends Packet{

	private int health;
	private int playerID;
	
	public Packet04Health(byte[] data) {
		super(04);
		String[] dataArray = readData(data).split(",");
		this.playerID = Integer.parseInt(dataArray[0]);
		this.health = Integer.parseInt(dataArray[1]);
	}
	public Packet04Health(int playerID, int health) {
		super(04);
		this.playerID = playerID;
		this.health = health;
	}
	
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClient(getData());
	}

	public void writeDataWithout(GameServer server, InetAddress address, int port) {
		server.sendToAllWithout(getData(), address, port);
	}
	
	public byte[] getData() {
		return ("04"+ this.playerID + "," + this.health).getBytes();
	}
	
	public int getHealth() {
		return this.health;
	}
	
	public int getPlayerID() {
		return this.playerID;
	}
	
}
