/**
 * 
 */
package net.File;

import java.io.IOException;
import java.util.logging.Logger;




/**
 * @author jinyu
 *
 */
public class TestRecFiles {
	private static  Logger log=Logger.getLogger(TestRecFiles.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 //192.168.30.128
		ReadXml rd=new ReadXml();
		   String xml=	rd.readXml(ReadXml.getPath()+"/config.xml");
		   String[] config=null;
		  if(xml!=null)
		  {
			  config=xml.split(",");
		  }
		RecviceFiles rec=new RecviceFiles();
            String dir=config[2];
			rec.setDir(dir);
			rec.start(config[0], Integer.valueOf(config[1]));
			log.info("启动接收文件");
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
