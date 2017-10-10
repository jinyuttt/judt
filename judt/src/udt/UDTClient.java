/*********************************************************************************
 * Copyright (c) 2010 Forschungszentrum Juelich GmbH 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * (1) Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the disclaimer at the end. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * (2) Neither the name of Forschungszentrum Juelich GmbH nor the names of its 
 * contributors may be used to endorse or promote products derived from this 
 * software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************************/

package udt;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import udt.packets.Destination;
import udt.packets.Shutdown;
import udt.util.UDTStatistics;

public class UDTClient {

	private static final Logger logger=Logger.getLogger(UDTClient.class.getName());
	private final UDPEndPoint clientEndpoint;
	private ClientSession clientSession;
	private boolean close=false;
    private Thread closeThread=null;//cd
    private final int waitClose=10*1000;
	public UDTClient(InetAddress address, int localport)throws SocketException, UnknownHostException{
		//create endpoint
		clientEndpoint=new UDPEndPoint(address,localport);
		logger.info("Created client endpoint on port "+localport);
	}

	public UDTClient(InetAddress address)throws SocketException, UnknownHostException{
		//create endpoint
		clientEndpoint=new UDPEndPoint(address);
		logger.info("Created client endpoint on port "+clientEndpoint.getLocalPort());
	}

	public UDTClient(UDPEndPoint endpoint)throws SocketException, UnknownHostException{
		clientEndpoint=endpoint;
	}

	/**
	 * establishes a connection to the given server. 
	 * Starts the sender thread.
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 */
	public void connect(String host, int port)throws InterruptedException, UnknownHostException, IOException{
		InetAddress address=InetAddress.getByName(host);
		Destination destination=new Destination(address,port);
		//create client session...
		clientSession=new ClientSession(clientEndpoint,destination);
		clientEndpoint.addSession(clientSession.getSocketID(), clientSession);

		clientEndpoint.start();
		clientSession.connect();
		//wait for handshake
		while(!clientSession.isReady()){
			Thread.sleep(500);
		}
		logger.info("The UDTClient is connected");
		Thread.sleep(500);
	}

	/**
	 * sends the given data asynchronously
	 * 
	 * @param data
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void send(byte[]data)throws IOException, InterruptedException{
		if(close)
		{
			return;//cd
		}
		clientSession.getSocket().doWrite(data);
	}

	public void sendBlocking(byte[]data)throws IOException, InterruptedException{
		if(close)
		{
			return;//cd
		}
		clientSession.getSocket().doWriteBlocking(data);
	}

	public int read(byte[]data)throws IOException, InterruptedException{
		return clientSession.getSocket().getInputStream().read(data);
	}

	/**
	 * flush outstanding data (and make sure it is acknowledged)
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void flush()throws IOException, InterruptedException{
		clientSession.getSocket().flush();
	}


	public void shutdown()throws IOException{

		if (clientSession.isReady()&& clientSession.active==true) 
		{
			Shutdown shutdown = new Shutdown();
			shutdown.setDestinationID(clientSession.getDestination().getSocketID());
			shutdown.setSession(clientSession);
			try{
				clientEndpoint.doSend(shutdown);
				TimeUnit.MILLISECONDS.sleep(100);
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE,"ERROR: Connection could not be stopped!",e);
			}
			clientSession.getSocket().getReceiver().stop();
			clientEndpoint.stop();
			//cd 添加
			clientEndpoint.removeSession(clientSession.getSocketID());
			clientSession.getSocket().getSender().stop();
			close=true;
		}
	}

	public UDTInputStream getInputStream()throws IOException{
		return clientSession.getSocket().getInputStream();
	}

	public UDTOutputStream getOutputStream()throws IOException{
		return clientSession.getSocket().getOutputStream();
	}

	public UDPEndPoint getEndpoint()throws IOException{
		return clientEndpoint;
	}

	public UDTStatistics getStatistics(){
		return clientSession.getStatistics();
	}
    
	public long getSocketID()
	{
	    //cd 
	    return clientSession.getSocketID();
	}
	
	/**
	 * 同步关闭
	 * 等待数据发送完成后再关闭
	 * 但是只等待10ss
	 */
	public synchronized void close()
	{
		close=true;
		if(closeThread==null)
		{
			closeThread=new Thread(new Runnable() {

				@Override
				public void run() {
					int num=0;
				while(true)
				{
					if(clientSession.getSocket().getSender().isSenderEmpty())
					{
						try {
							shutdown();
							break;
						} catch (IOException e) {
						
							e.printStackTrace();
						}
					}
					else
					{
						try {
							TimeUnit.MILLISECONDS.sleep(100);
							num++;
							if(waitClose<=num*100)
							{
								try {
									shutdown();
								} catch (IOException e) {
								
									e.printStackTrace();
								}
								break;
							}
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
					}
				}
					
				}
				
			});
			closeThread.setDaemon(true);
			closeThread.setName("closeThread");
		}
		if(closeThread.isAlive())
		{
			return;
		}
		else
		{
			closeThread.start();
		}
	}
	
	public boolean isClose()
	{
	    return close;
	}
}
