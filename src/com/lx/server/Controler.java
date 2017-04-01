package com.lx.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Controler{
	public final static int PORT = 8888;
	private JFrame frame;
	private JLabel label;
	
	public Controler(){
		frame = new JFrame("控制端");
		frame.setSize(800, 600);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(3);
		//启动时位于屏幕中心
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		init();
	}
	
	public void init(){
		label = new JLabel();
		label.setSize(800, 600);
		JPanel panel = new JPanel();
		panel.add(label);
		frame.add(panel);
		creatPool();
	}
	
	public void creatPool(){
		ExecutorService pool = Executors.newFixedThreadPool(10);
		
		try (ServerSocket server = new ServerSocket(PORT)){
			while(true){
				System.out.println("server start");
				Socket socket = server.accept();
				System.out.println("有链接");
				Callable<Void> task = new Task(socket,frame,label);
				pool.submit(task);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void main(String[] args) {
		new Controler();
	}
	
}
