package Test;

import java.util.concurrent.TimeUnit;

import judp.judpClient;

public class TestClient {

	public static void main(String[] args) {
		long num=0;
		while(true)
		{
			judpClient client=new judpClient();
			client.connect("192.168.3.104", 9000);
			byte[]data=("hello word jinyu").getBytes();
			//16
			client.sendData(data);
			client.close();
			try {
				System.out.println("µÈ´ý");
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			num++;
		}

	}

}
