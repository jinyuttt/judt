package Test;

import java.util.concurrent.TimeUnit;

import judp.judpClient;

public class TestClient {

	public static void main(String[] args) {
		while(true)
		{
			judpClient client=new judpClient();
			client.connect("192.168.64.128", 5555);
			byte[]data="hello word".getBytes();
			client.sendData(data);
			client.close();
			try {
				System.out.println("µÈ´ý");
				TimeUnit.SECONDS.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
