package com.lx.server;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Task implements Callable<Void> {
	private Socket socket;
	private JFrame frame;
	private JLabel label;
	private DataInputStream datain;
	private ObjectOutputStream objout;

	public Task(Socket s, JFrame f, JLabel l) {
		this.socket = s;
		this.frame = f;
		this.label = l;
		try {
			datain = new DataInputStream(socket.getInputStream());
			objout = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setJFrameListener() {
		// Adapter!!!!!的使用 重点
		frame.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				sendEventObject(e);
				super.keyPressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				sendEventObject(e);
				super.keyReleased(e);
			}

			@Override
			public void keyTyped(KeyEvent e) {
				sendEventObject(e);
				super.keyTyped(e);
			}
		});

		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				sendEventObject(e);

			}

			@Override
			public void mousePressed(MouseEvent e) {
				sendEventObject(e);

			}

			@Override
			public void mouseExited(MouseEvent e) {
				sendEventObject(e);

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				sendEventObject(e);

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				sendEventObject(e);

			}
		});

		frame.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				sendEventObject(e);

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				sendEventObject(e);

			}
		});

		frame.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				sendEventObject(e);

			}
		});
	}

	private void sendEventObject(InputEvent event) {
		try {
			objout.writeObject(event);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage resize(Image img, int newW, int newH) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);

		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_BGR);
		Graphics2D g = dimg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
		g.dispose();
		return dimg;
	}

	@Override
	public Void call() {
		try {
			while (true) {
				int len = datain.readInt();
				byte[] data = new byte[len];
				datain.readFully(data);
				// ????
				ByteArrayInputStream bins = new ByteArrayInputStream(data);
				BufferedImage image = ImageIO.read(bins);
				ImageIcon icon = new ImageIcon(image);
				Image img = icon.getImage();
				Toolkit tk = Toolkit.getDefaultToolkit();
				Dimension d = tk.getScreenSize();

				int w = d.width;
				int h = d.height;
				BufferedImage bi = resize(img, 800, 600);
				label.setIcon(new ImageIcon(bi));
				label.repaint();// 销掉以前画的背景
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
