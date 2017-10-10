/**
 * 
 */
package judp;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;
/**
 * @author jinyu
 *
 */
public class PackagetSub {
 private static AtomicLong sessionid=new AtomicLong(0);
 public static int  dataSzie=1472;
 private static  int bufsize=0;
 private static int headLen=20;
 
 /**
  * 分割数据
  * @param data
  * @return
  */
 public static byte[][] splitData(byte[]data)
 {
	 if(bufsize==0)
     {
     	bufsize=dataSzie-headLen;
     }
     long session=sessionid.incrementAndGet();
     int dataLen=data.length;
     int num=data.length/bufsize+data.length%bufsize>0?1:0;
     byte[][]sendData=new byte[num][];
     int index=0;
     ByteBuffer buf=ByteBuffer.allocate(dataSzie);
     for(int i=0;i<num;i++)
     {
    	 buf.putLong(session);
    	 buf.putInt(num);
    	 buf.putInt(i);
    	 buf.putInt(dataLen);
     	if(index+bufsize<data.length)
     	{
     	   buf.put(data, index, bufsize);
     	   index+=bufsize;
     	}
     	else
     	{
     		buf.put(data, index, data.length-index);
     	}
     	//
    	buf.flip();
     	byte[]tmp=new byte[buf.limit()];
     	buf.get(tmp);
     	sendData[i]=tmp;
     	buf.clear();
     }
   return sendData;
 }
 
 /**
  * 单独分割数据
  * @param data
  * @param len
  * @return
  */
 public  byte[][] split(byte[]data,int len)
 {
	 int size=len-headLen;
     long session=sessionid.incrementAndGet();
     int dataLen=data.length;
     int num=(data.length/size)+(data.length%size>0?1:0);
     byte[][]sendData=new byte[num][];
     int index=0;
     ByteBuffer buf=ByteBuffer.allocate(len);
     for(int i=0;i<num;i++)
     {
    	
    	 buf.putLong(session);
    	 buf.putInt(num);
    	 buf.putInt(i);
    	 buf.putInt(dataLen);
     	if(index+size<data.length)
     	{
     	   buf.put(data, index, size);
     	}
     	else
     	{
     		buf.put(data, index, data.length-index-1);
     	}
     	//
    	buf.flip();
     	byte[]tmp=new byte[buf.limit()];
     
     	buf.get(tmp);
     	sendData[i]=tmp;
     	buf.clear();
     }
   return sendData;
 }
 
}
