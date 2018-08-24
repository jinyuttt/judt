/**
 * 
 */
package net.File;


import java.util.logging.Logger;

import net.File.FileMonitor;
import net.File.FilesWatch;




/**
 * @author jinyu
 *
 */
public class TestSendFiles {
	private static  Logger log=Logger.getLogger(TestSendFiles.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//SendFiles  send=new SendFiles("192.168.9.152", 5555);
		//192.168.30.128
		
		ReadXml rd=new ReadXml();
	   String xml=	rd.readXml(ReadXml.getPath()+"/Config.xml");
	   String[] config=null;
	  if(xml!=null)
	  {
		  config=xml.split(",");
	  }
		SendFiles  send=new SendFiles(config[0], Integer.valueOf(config[1]));
		FilesWatch watch=new FilesWatch();
         String dir=config[2];
		 watch.setWatch(dir);
		 watch.start();
		 while(true)
		 {
			 FileMonitor ff=  watch.take();
			 log.info(ff.file);
			 if(ff.file.endsWith(".tmp"))
			 {
				 continue;
			 }
			 send.sendFile(dir+"/"+ff.file);
		 }
	}

}
