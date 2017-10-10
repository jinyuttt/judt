/**
 * 
 */
package judp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import udt.UDTSocket;

/**
 * @author jinyu
 * 按照目的分组
 */
public class judpGroupSocket {
private ArrayList<UDTSocket> list=new ArrayList<UDTSocket>();
private static final Logger logger=Logger.getLogger(judpGroupSocket.class.getName());
public judpGroupSocket()
{
	
}

/**
 * 添加socket
 * @param socket
 */
public void addSocket(UDTSocket socket)
{
	list.add(socket);
}

/**
 * 获取有数据socket
 * 并且移除其它无用socket
 * @return
 */
public UDTSocket getSocket()
{
	
	int index=-1;
	for( int i = 0 ; i < list.size() ; i++) {
	    try {
	    	if(index==-1)
	    	{
			  if(list.get(i).getInputStream().isHasData())
			   {
				//已经找到；其余的移除关
				  index=i;
				   i=-1;//重新遍历
			   }
	    	}
	    	else
	    	{
	    		//
	    		if(i==index)
	    		{
	    			continue;
	    		}
	    		else
	    		{
	    			list.get(i).close();
	    			long id=list.get(i).getSession().getSocketID();
	    			list.get(i).getEndpoint().removeSession(id);
	    			list.get(i).getReceiver().stop();
	    			list.get(i).getSender().stop();
	    			list.get(i).getSender().pause();
	    			logger.info("移除无用socket:"+id);
	    		}
	    		
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	if(index!=-1)
	{
		return list.get(index);
	}
	return null;
	
}
/**
 * 清除所有socket
 */
public void clear()
{
	list.clear();
}
}
