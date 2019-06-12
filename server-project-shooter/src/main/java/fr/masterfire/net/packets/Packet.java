package fr.masterfire.net.packets;

import java.net.InetAddress;

import fr.masterfire.net.GameClient;
import fr.masterfire.net.GameServer;

public abstract class Packet {

	public static enum PacketTypes {

		INVALID(-1), LOGIN(00), DISCONNECT(01), MOVE(02), SHOOT(03), HEALTH(04), ASK(05), RESPAWN(06);

		private int packetId;

		private PacketTypes(int packetId) {
			this.packetId = packetId;
		}

		public int getId() {
			return packetId;
		}

	}

	public byte packetId;

	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}

	public abstract void writeData(GameClient client);

	public abstract void writeData(GameServer server);

	public abstract byte[] getData();

	public String readData(byte[] data) {
		String message = new String(data).trim();
		return message.substring(2);
	}
	
	public static PacketTypes lookuPacket(String id) {
		try {
			return lookuPacket(Integer.parseInt(id));
		} catch (Exception e) {
			return PacketTypes.INVALID;
		}
	}

	public static PacketTypes lookuPacket(int id) {
		for (PacketTypes p : PacketTypes.values()) {
			if (p.getId() == id) {
				return p;
			}
		}
		return PacketTypes.INVALID;
	}

	public abstract void writeDataWithout(GameServer gameServer, InetAddress address, int port);

}
