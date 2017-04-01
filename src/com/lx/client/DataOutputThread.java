package com.lx.client;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import javax.imageio.ImageIO;

public class DataOutputThread implements Runnable{

	private DataOutputStream dout;
	public DataOutputThread(DataOutputStream dout){
		this.dout = dout;
	}
	public void run(){
		try{
			while(true){
				byte[] data = createImage();
				//先发送图片数据长度
				dout.writeInt(data.length);
				//写入图片数据
				dout.write(data);
				dout.flush();
				Thread.sleep(100);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//取得一张屏幕图片，转成字节数组返回
		private byte[] createImage()throws Exception{
			Robot robot = new Robot();
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension dm = tk.getScreenSize();
			//设定区域的大小
			Rectangle rt = new Rectangle(0, 0, dm.width, dm.height);
			//取得指定大小的图片
			BufferedImage image = robot.createScreenCapture(rt);
			//创建一端内存流
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//将图片数据写入内存流
			ImageIO.write(image, "jpeg", baos);
			//作为字节数组返回
			byte[] data = baos.toByteArray();
			return data;
		}
}
