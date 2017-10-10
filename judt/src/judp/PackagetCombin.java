/**
 * 
 */
package judp;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * @author jinyu
 *
 */
public class PackagetCombin {
	private  static  ConcurrentHashMap<Long,DataStruct> hash=new  ConcurrentHashMap<Long,DataStruct>();
	private  ConcurrentLinkedQueue<byte[]> queue=new  ConcurrentLinkedQueue<byte[]>();
	
	/**
	 * 添加数据
	 * @param data
	 * @return
	 */
public  boolean addData(byte[] data)
{
	ByteBuffer buf=ByteBuffer.wrap(data);
	long id=buf.getLong();
	int num=buf.getInt();
	DataStruct struct=hash.get(id);
	if(struct==null)
	{
		struct=new DataStruct(num);
		hash.put(id, struct);
	}
    boolean r=	struct.addData(data);
    if(r)
    {
    	byte[]result =struct.getData();
    	byte[] tmp=new byte[result.length];
    	System.arraycopy(result, 0, tmp, 0, tmp.length);
    	queue.offer(tmp);
    	struct.clear();
    	hash.remove(id);
    }
	return r;
	 
}

/**
 * 获取数据
 * @return
 */
public byte[] getData()
{
	return queue.poll();
}
}
