package fr.masterfire.net.packets.ServerPackets;

import java.net.InetAddress;

import fr.masterfire.net.GameClient;
import fr.masterfire.net.GameServer;
import fr.masterfire.net.packets.Packet;

public class Packet01Disconnect extends Packet{

	private String username;
	
	public Packet01Disconnect(byte[] data) {
		super(01);
		this.username = readData(data);
	}

	public Packet01Disconnect(String username) {
		super(01);
		this.username = username;
	}
	
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClient(getData());
	}

	public byte[] getData() {
		return ("01" + this.username).getBytes();
	}

	public String getUsername() {
		return username;
	}

	public void writeDataWithout(GameServer gameServer, InetAddress address, int port) {
		// TODO Auto-generated method stub
		
	}
	
}
