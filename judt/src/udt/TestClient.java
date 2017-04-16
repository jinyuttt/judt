/**
 * 
 */
package udt;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author jinyu
 *
 */
public class TestClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 while(true)
		 {
		 UDTClient client = null;
		try {
			client = new UDTClient(null, 0);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			client.connect("192.168.3.189", 5555);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 String str="select * from test";
//		 while(true)
//		 {
//			 try {
//				System.in.read();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		 try {
			 byte[]data=str.getBytes();
			// client.send(data);
			 client.sendBlocking(data);
			 //int r=client.read(data);
			 while(client.read(data)==0)
			 {
				 Thread.sleep(1000);
			 }
			 String dd=new String(data);
			 System.out.println(dd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 }

	}

}
