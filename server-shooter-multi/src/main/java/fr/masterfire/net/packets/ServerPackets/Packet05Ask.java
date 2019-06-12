package fr.masterfire.net.packets.ServerPackets;

import java.net.InetAddress;

import fr.masterfire.net.GameClient;
import fr.masterfire.net.GameServer;
import fr.masterfire.net.packets.Packet;

public class Packet05Ask extends Packet{

	private String ask;
	
	public Packet05Ask(byte[] data) {
		super(05);
		this.ask = readData(data);
	}

	public Packet05Ask(String ask) {
		super(05);
		this.ask = ask;
	}
	
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	public void writeData(GameServer server) {
		server.sendDataToAllClient(getData());
	}

	public byte[] getData() {
		return ("05" + this.ask).getBytes();
	}

	public String getAsk() {
		return ask;
	}

	public void writeDataWithout(GameServer gameServer, InetAddress address, int port) {
		// TODO Auto-generated method stub
		
	}
	
}
