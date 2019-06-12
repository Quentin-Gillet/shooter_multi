package fr.masterfire.main;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.*;

import fr.masterfire.Game;
import fr.masterfire.net.packets.ServerPackets.Packet01Disconnect;

public class Window extends Canvas implements WindowListener {

	private static final long serialVersionUID = -52273597974759019L;

	public JFrame frame;
	private Game game;

	public Window(int width, int height, String title, Game game) {

		this.game = game;

		frame = new JFrame();

		frame.setPreferredSize(new Dimension(width, height));
		frame.setMaximumSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));

		frame.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(this.game);
		frame.setVisible(true);
		this.game.start();
		frame.addWindowListener(this);

		frame.addWindowFocusListener(new WindowFocusListener() {

			public void windowLostFocus(WindowEvent e) {
				if (game.player != null) {
					game.player.setVelX(0);
					game.player.setVelY(0);
				}
				;

			}

			public void windowGainedFocus(WindowEvent e) {

			}
		});
	}

	public void windowOpened(WindowEvent e) {

	}

	public void windowClosing(WindowEvent e) {
		if (game.player != null) {
			Packet01Disconnect packet = new Packet01Disconnect(game.player.getUsername());
			packet.writeData(game.socketClient);
			
			//game.socketClient.closeConnection();
		}
	}

	public void windowClosed(WindowEvent e) {

	}

	public void windowIconified(WindowEvent e) {

	}

	public void windowDeiconified(WindowEvent e) {

	}

	public void windowActivated(WindowEvent e) {

	}

	public void windowDeactivated(WindowEvent e) {

	}

}
