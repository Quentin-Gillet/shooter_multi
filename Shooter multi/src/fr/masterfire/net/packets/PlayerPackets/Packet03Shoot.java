package fr.masterfire.net.packets.PlayerPackets;

import java.net.InetAddress;

import fr.masterfire.net.GameClient;
import fr.masterfire.net.GameServer;
import fr.masterfire.net.packets.Packet;

public class Packet03Shoot extends Packet{

	//private float bulletVectorX, bulletVectorY;
	private int mX, mY;
	private float playerX, playerY;
	private int sender;
	
	public Packet03Shoot(byte[] data) {
		super(03);
		String[] dataArray = readData(data).split(",");
		this.sender = Integer.parseInt(dataArray[0]);
		this.mX = Integer.parseInt(dataArray[1]);
		this.mY = Integer.parseInt(dataArray[2]);
		this.playerX = Float.parseFloat(dataArray[3]);
		this.playerY = Float.parseFloat(dataArray[4]);
	}
	public Packet03Shoot(int sender, int mX, int mY, float playerX, float playerY) {
		super(03);
		this.sender = sender;
		this.mX = mX;
		this.mY = mY;
		this.playerX = playerX;
		this.playerY = playerY;
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
		return ("03" + this.sender + "," + this.mX + "," + this.mY + "," + this.playerX + "," + this.playerY).getBytes();
	}
	
	public int getSender() {
		return this.sender;
	}
	public int getmX() {
		return mX;
	}
	public int getmY() {
		return mY;
	}
	public float getPlayerX() {
		return playerX;
	}
	public float getPlayerY() {
		return playerY;
	}
	
	
}
