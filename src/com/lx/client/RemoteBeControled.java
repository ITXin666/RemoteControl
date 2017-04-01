package com.lx.client;

import java.awt.FlowLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.lx.client.DataOutputThread;

public class RemoteBeControled extends JFrame{

	private JButton jbutton_add;
	private JButton jbutton_transf;
	private Socket socket;
	private JTextField text_host;
	private JTextField text_port;
	private JLabel label_host;
	private JLabel label_port;
	private String host;
	private int port;
	private DataOutputStream dout;
	private ObjectInputStream ins;
	private InputEvent event;
	public RemoteBeControled(){
		super("远程桌面受控制端");
		this.setSize(400, 300);
		this.setDefaultCloseOperation(3);
		this.setLocationRelativeTo(null);
		jbutton_add = new JButton("添加连接");
		jbutton_transf = new JButton("传输界面");
		text_host = new JTextField(10);
		text_port = new JTextField(10);
		label_host = new JLabel("IP地址：");
		label_port = new JLabel("端口号：");
		initialize();
		this.setVisible(true);
	}
	public void initialize(){
		this.setLayout(new FlowLayout());
		this.add(jbutton_add);
		//this.add(jbutton_transf);
		this.add(label_host);
		this.add(text_host);
		this.add(label_port);
		this.add(text_port);
		text_port.setText("8888");
		text_host.setText("127.0.0.1");
		text_port.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				try{
					if(text_port.getText().equals("")){
						JOptionPane.showMessageDialog(RemoteBeControled.this, "请输入端口号");
					}
					else
						port = Integer.parseInt(text_port.getText());
						
				}catch(NumberFormatException e1){
					JOptionPane.showMessageDialog(RemoteBeControled.this, "请输入合法的端口号");
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		text_host.addFocusListener(new FocusAdapter(){
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				super.focusLost(e);
				if(text_host.getText().equals("")){
					JOptionPane.showMessageDialog(RemoteBeControled.this, "请输入IP地址");
				}
			}
		});
		jbutton_add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					port = Integer.parseInt(text_port.getText());
					host = text_host.getText();
					socket = new Socket(host, port);
					dout = new DataOutputStream(socket.getOutputStream());
					ins = new ObjectInputStream(socket.getInputStream());
					inEvent();
					DataOutputThread d = new DataOutputThread(dout);
					Thread t = new Thread(d);
					t.start();
					
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(RemoteBeControled.this, "没有找到所输入的IP地址");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(RemoteBeControled.this, "socket连接出错");
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
		});
	}
	//实现控制端操作
	public void inEvent(){
		
		Runnable r = new Runnable() {
			
			@Override
			public void run(){
				try{
					while(true){
						event = (InputEvent)ins.readObject();
						if(event!=null){
							handleEvent(event);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	private void handleEvent(InputEvent e)throws Exception{
		Robot action = new Robot();
		//鼠标事件
		MouseEvent mevent = null;
		//鼠标滚动事件
		MouseWheelEvent mwevent = null;
		//键盘事件
		KeyEvent kevent = null;
		//鼠标按键
		int mouseButtonMask = -100;
		switch(e.getID()){
			case MouseEvent.MOUSE_MOVED:	//鼠标移动
				mevent = (MouseEvent)e;
				action.mouseMove(mevent.getX(), mevent.getY());
				break;
			case MouseEvent.MOUSE_PRESSED:	//鼠标键按下
				mevent = (MouseEvent)e;
				action.mouseMove(mevent.getX(), mevent.getY());
				mouseButtonMask = getMouseClick( mevent.getButton() );
				if(mouseButtonMask!=-100)
					action.mousePress(mouseButtonMask);
				break;
			case MouseEvent.MOUSE_RELEASED:	//鼠标键松开
				mevent = (MouseEvent)e;
				action.mouseMove(mevent.getX(), mevent.getY());
				mouseButtonMask = getMouseClick( mevent.getButton() );
				if(mouseButtonMask!=-100)
					action.mouseRelease(mouseButtonMask);
				break;
			case MouseEvent.MOUSE_WHEEL:	//鼠标滚动
				mwevent = (MouseWheelEvent)e;
				action.mouseWheel(mwevent.getWheelRotation());
				break;
			case MouseEvent.MOUSE_DRAGGED:	//鼠标拖拽
				mevent = (MouseEvent)e;
				action.mouseMove(mevent.getX(), mevent.getY());
				break;
			case KeyEvent.KEY_PRESSED:	//按键
				kevent = (KeyEvent)e;
				action.keyPress(kevent.getKeyCode());
				break;
			case KeyEvent.KEY_RELEASED:	//松键
				kevent = (KeyEvent)e;
				action.keyRelease(kevent.getKeyCode());
				break;
				default:break;
		}
	}
	 private int getMouseClick(int button) {    //取得鼠标按键
         if (button == MouseEvent.BUTTON1) //左键 ,中间键为BUTTON2
             return InputEvent.BUTTON1_MASK;
         if (button == MouseEvent.BUTTON3) //右键
             return InputEvent.BUTTON3_MASK;
         return -100;
     }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new RemoteBeControled();
	}

}
