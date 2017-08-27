/**
 * 
 */
package judp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import udt.UDTSession;
import udt.UDTSocket;
import udt.packets.Destination;

/**
 * @author jinyu
 *
 *服务端返回的网络接口对象
 */
public class judpSocket {
private  final int bufSize=1500;
private UDTSocket socket=null;
private long start=System.currentTimeMillis();
private boolean isClose=false;
private long flushTime=0;
private final long waitDataLen=30*1000;//30秒
private long  readLen=0;//读取数据
private long sendLen=0;//发送数据
public long socketID=0;//ID
private static final Logger logger=Logger.getLogger(judpSocket.class.getName());
public boolean getCloseState()
{
	return isClose;
}
public judpSocket(UDTSocket  usocket)
{
	this.socket=usocket;
	socketID=socket.getSession().getSocketID();
}

/**
 * 关闭
 */
public void close()
{
	isClose=true;
	//不能真实关闭
	if(sendLen==0)
	{
		//没有发送则可以直接关闭，不需要等待数据发送完成
		 try {
			socket.close();
			UDTSession serversession=socket.getEndpoint().removeSession(socketID);
			if(serversession!=null)
			{
				serversession.getSocket().close();
			     socket.getReceiver().stop();
			     socket.getSender().stop();
				System.out.println("物理关闭socket:"+serversession.getSocketID());
			}
			
			serversession=null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		 System.out.println("物理关闭socket");
	}
	else
	{
		//有过发送数据则缓冲
		SocketManager.getInstance().add(socket);
	}
}

/**
 * 读取数据
 * 返回接收的字节大小
 */
public int readData(byte[]data)
{
    if(isClose)
     {
	   return -1;
     }
	try {
	  int r=socket.getInputStream().read(data);
	  readLen+=r;
	  flushTime=System.currentTimeMillis();
	  if(flushTime-start>waitDataLen&&readLen==0)
		{
	      //等待时间长度，没有发送过接收过数据，则退出
	       logger.info("缓冲时间到退出读取:"+socketID);
		   return -1;
		}
	 return r;
	} catch (IOException e) {
		e.printStackTrace();
	}
	return -1;
}

/**
 * 读取全部数据
 */
public byte[] readData()
{
	 byte[] result=null;
	  if(socket!=null)
	  {
		  byte[]  readBytes=new byte[bufSize];//接收区
		  byte[] buf=new byte[bufSize];//数据区
		  int index=0;
		  int r=0;
		  try {
			  while(true)
			  {
				  if(isClose)
					{
						return null;
					}
		          r=socket.getInputStream().read(readBytes);
		          if(r==-1)
		          {
		        	  break;
		          }
		          else
		          {
		              readLen+=r;
		        	  if(r==0)
		        	  {
		        		  try {
							TimeUnit.MILLISECONDS.sleep(100);
							flushTime=System.currentTimeMillis();
							if(flushTime-start>waitDataLen&&readLen==0)
							{
							    //没有使用过数据
								break;
							}
							continue;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		        	  }
		        	  if(r<=bufSize)
		        	  {
		        		  //result=new byte[r];
		        		  //System.arraycopy(readBytes, 0, result, 0, r);
		        		  if(index+r<buf.length)
		        		  {
		        			  System.arraycopy(readBytes, 0, buf, index, r);
		        			  index+=r;
		        		  }
		        		  else
		        		  {
		        			  //扩展数据区
		        			  int len=(int) (buf.length*0.75);
		        			  if(len<bufSize)
		        			  {
		        				  len=bufSize;
		        			  }
		        			  //最小扩展bufSize；一定比r大
		        			  byte[] tmp=new byte[buf.length+len];
		        			  System.arraycopy(buf, 0, tmp, 0, index+1);//拷贝数据
		        			  System.arraycopy(readBytes, 0, tmp, index, r);//拷贝数据
		        			  buf=tmp;
		        			  index+=r;
		        		  }
		        	  }
		          }
			  }
		     
		} catch (IOException e) {
		
			e.printStackTrace();
		} 
		  result=new byte[index+1];//长度
		  System.arraycopy(buf, 0, result, 0,index+1);//拷贝数据
	  }
	  
	  return result;
}

/*
 * 获取初始化序列
 */
public long getInitSeqNo()
{
	if(socket!=null)
	{
	   return	socket.getSession().getInitialSequenceNumber();
	}
	return 0;
}

/**
 * 发送包长
 */
public int getDataStreamLen()
{
    return socket.getSession().getDatagramSize();
}


public Destination getDestination()
{

    if(socket!=null)
    {
       return   socket.getSession().getDestination();
    }
    Destination tmp = null;
    try {
        tmp = new Destination(InetAddress.getLocalHost(), 0);
    } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    return tmp;
}
/**
 * 发送数据
 * 空数据不能发送
 */
public boolean sendData(byte[]data) {
	if(isClose)
	{
		return false;
	}
	try {
		socket.getOutputStream().write(data);
		sendLen=+1;
		flushTime=System.currentTimeMillis();
		return true;
	} catch (IOException e) {
		e.printStackTrace();
	}
	return false;
}


}
