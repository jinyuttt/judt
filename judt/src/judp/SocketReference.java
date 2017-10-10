/**
 * 
 */
package judp;


import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @author jinyu
 * @param <T>
 *
 */
public class SocketReference<T> extends WeakReference<T> {
    private long   socketid=-1;
	public SocketReference(T referent) {
		super(referent);
	   
	}
	public SocketReference(T referent,long id) {
		super(referent);
	   this.socketid=id;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SocketReference(T referent, long id, ReferenceQueue q) {
		super(referent,q);
		 this.socketid=id;
	}
	public long getid()
	{
		return socketid;
	}
}
