/**    
 * 文件名：judpRecviceFile.java    
 *    
 * 版本信息：    
 * 日期：2017年8月27日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package judp;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.NumberFormat;


import udt.UDTReceiver;


/**    
 *     
 * 项目名称：judp    
 * 类名称：judpRecviceFile    
 * 类描述：    接收请求文件
 * 创建人：jinyu    
 * 创建时间：2017年8月27日 下午4:30:29    
 * 修改人：jinyu    
 * 修改时间：2017年8月27日 下午4:30:29    
 * 修改备注：    
 * @version     
 *     
 */
public class judpRecviceFile {
    private final int serverPort;
    private final String serverHost;
    private  String remoteFile;
    private  String localFile;
    private final NumberFormat format;
    //
    public  String localIP=null;
    public  int localPort=0;
    
    public judpRecviceFile(String serverHost, int serverPort, String remoteFile, String localFile){
        this.serverHost=serverHost;
        this.serverPort=serverPort;
        this.remoteFile=remoteFile;
        this.localFile=localFile;
        format=NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(3);
    }
    public void setFile(String remoteFile, String localFile)
    {
        this.remoteFile=remoteFile;
        this.localFile=localFile;
    }
    public void start()
    {
        try{
            UDTReceiver.connectionExpiryDisabled=true;
            //UDTClient client=localPort!=-1?new UDTClient(myHost,localPort):new UDTClient(myHost);
            judpClient client=new judpClient(localIP,localPort);
            client.connect(serverHost, serverPort);
            System.out.println("[ReceiveFile] Requesting file "+remoteFile);
            String reqFile=remoteFile;
            String rspFile=localFile;
            Thread recfile=new Thread(new Runnable(){

                @Override
                public void run() {
                   
                    byte[]fName=reqFile.getBytes();
                    
                    //send file name info
                    byte[]nameinfo=new byte[fName.length+4];
                    System.arraycopy(ApplicationCode.encode(fName.length), 0, nameinfo, 0, 4);
                    System.arraycopy(fName, 0, nameinfo, 4, fName.length);
                    client.sendData(nameinfo);
                    client.pauseOutput();
                    //read size info (an 64 bit number) 
                    byte[]sizeInfo=new byte[8];
                    
                    int total=0;
                    while(total<sizeInfo.length){
                        int r=client.read(sizeInfo);
                        if(r<0)break;
                        total+=r;
                    }
                    long size=ApplicationCode.decode(sizeInfo, 0);
                    
                    File file=new File(new String(rspFile));
                    System.out.println("[ReceiveFile] Write to local file <"+file.getAbsolutePath()+">");
                    FileOutputStream fos=null;
                    try{
                         fos=new FileOutputStream(file);
                    
                        System.out.println("[ReceiveFile] Reading <"+size+"> bytes.");
                        long start = System.currentTimeMillis();
                        ApplicationCode.CopySocketFile(fos, client,size,true);
                        long end = System.currentTimeMillis();
                        double rate=1000.0*size/1024/1024/(end-start);
                        System.out.println("[ReceiveFile] Rate: "+format.format(rate)+" MBytes/sec. "
                                +format.format(8*rate)+" MBit/sec.");
                        System.out.println("接收文件完成："+rspFile);
                        client.close();
                        
                    }
                    catch(Exception ex)
                    {
                        
                    }
                    finally{
                        try {
                            fos.close();
                        } catch (IOException e) {
                           
                            e.printStackTrace();
                        }
                    }     
                }});
            recfile.setDaemon(true);
            recfile.setName("文件接收_"+localFile);
            recfile.start();
              
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
       
            
    }
}
