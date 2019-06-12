package fr.masterfire.net.packets.PlayerPackets;

import java.net.InetAddress;

import fr.masterfire.net.GameClient;
import fr.masterfire.net.GameServer;
import fr.masterfire.net.packets.Packet;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.IncompatibleAddressException;

public class Packet06Respawn extends Packet{

	private int x, y;
	private int playerID;
	private int lastPlayerIDTouch;
	
	private String username;
	
	private InetAddress address;
	private int port;
	
	public Packet06Respawn(byte[] data) {
		super(06);
		String[] dataArray = readData(data).split(",");
		this.x = Integer.parseInt(dataArray[0]);
		this.y = Integer.parseInt(dataArray[1]);
		this.username = dataArray[2];
		this.playerID = Integer.parseInt(dataArray[3]);
		String addr = dataArray[3];
		try {
			IPAddressString str = new IPAddressString(addr);
			IPAddress addressIpAddress = str.toAddress();
			this.address = addressIpAddress.toInetAddress();
		} catch (AddressStringException | IncompatibleAddressException e) {
			e.printStackTrace();
		}
		this.port = Integer.parseInt(dataArray[5]);
		this.lastPlayerIDTouch = Integer.parseInt(dataArray[6]);
	}
	public Packet06Respawn(int x, int y, String username, int playerID, InetAddress address, int port, int lastPlayerIDTouch) {
		super(06);
		this.x = x;
		this.y = y;
		this.username = username;
		this.playerID = playerID;
		this.lastPlayerIDTouch = lastPlayerIDTouch;
		this.address = address;
		this.port = port;
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
		return ("06"+ this.x + "," + this.y + "," + this.username + "," + this.playerID + "," + this.address + "," + this.port + "," + this.lastPlayerIDTouch).getBytes();
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
	public String getUsername() {
		return username;
	}
	public InetAddress getAddress() {
		return address;
	}
	public String getStringAdress() {
		return address.toString();
	}
	public int getPort() {
		return port;
	}
	public int getLastPlayerIDTouch() {
		return lastPlayerIDTouch;
	}
	
}
