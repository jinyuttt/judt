/**
 * 
 */
package net.File;

import java.io.IOException;

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author jinyu
 *
 */
public class FilesWatch {
	
private	WatchService watcher = null;
	private String dir;
	private Thread checkThread=null;
	private LinkedBlockingQueue<FileMonitor> queue=new LinkedBlockingQueue<FileMonitor>();
	private boolean isStop=false;
	public FilesWatch()
	{
		try {
			watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public void setWatch(String dir)
	{
		this.dir=dir;
	}
	public void stop()
	{
		isStop=true;
	}
	public FileMonitor take()
	{
		try {
			return queue.take();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	public void start()
	{
		checkThread =new Thread(new Runnable() {

			@Override
			public void run() {
				try
				{
				   Paths.get(dir).register(watcher,   
			                StandardWatchEventKinds.ENTRY_CREATE,  
			                StandardWatchEventKinds.ENTRY_DELETE,  
			                StandardWatchEventKinds.ENTRY_MODIFY);  
				}
				catch(Exception ex)
				{
					
				}
			        while (!isStop) {  
			            WatchKey key;
						try {
							key = watcher.take();
			            for (WatchEvent<?> event: key.pollEvents()) {  
			            	FileMonitor e=new FileMonitor();
			            	e.file=event.context().toString();
			            	e.kind=event.kind();
							queue.put(e);
			            }  
			              
			            boolean valid = key.reset();  
			            if (!valid) {  
			                break;  
			            }  
						} catch (InterruptedException e) {
							e.printStackTrace();
						} 
			        }  
			}
				
			
			
		});
		checkThread.setDaemon(true);
		checkThread.setName("monitor");
		checkThread.start();
}
}
