package judp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * 文件操作
 *
 */
public class ApplicationCode {
   static  final int bufsize=10*1024*1024;//读取文件用
   static float speed=0;//M
    static long decode(byte[]data, int start){
        long result = (data[start+3] & 0xFF)<<24
                     |(data[start+2] & 0xFF)<<16
                     |(data[start+1] & 0xFF)<<8
                     |(data[start] & 0xFF);
        return result;
    }
    
    static byte[]encode(long value){
        byte m4= (byte) (value>>24 );
        byte m3=(byte)(value>>16);
        byte m2=(byte)(value>>8);
        byte m1=(byte)(value);
        return new byte[]{m1,m2,m3,m4};
    }
    
    static byte[]encode64(long value){
        byte m4= (byte) (value>>24 );
        byte m3=(byte)(value>>16);
        byte m2=(byte)(value>>8);
        byte m1=(byte)(value);
        return new byte[]{m1,m2,m3,m4,0,0,0,0};
    }
    
    /**
     * 
        
     * 读取文件发送  
       
     * @param   文件，socket,发送包大小    
        
     * @return  
       
     *
     */
    @SuppressWarnings("resource")
    static void CopySocketFile(File file, judpSocket target,int packagetLen)
    {
        //byte[]buf=new byte[8*65536];
        FileInputStream fis=null;
        try {
            fis=new FileInputStream(file);
        } catch (FileNotFoundException e) {
           return;
        }
        byte[]buf=new byte[bufsize];
         int c = 0;
        long read=0;
        long size=file.length();
        byte[]data=null;
        int sendCount=0;
        long sendSum=0;
       if(packagetLen<=0)
       {
           packagetLen=65535;
       }
       final int sendLen=packagetLen-24;
       long waitTime=0;
       if(speed>0)
       {
           waitTime=(long)(speed*1000);
           
       }
       System.out.println("sendFile_"+file.getName()+",socketID:"+target.socketID);
        while(true){
            try {
                c=fis.read(buf);
            } catch (IOException e) {
             
                e.printStackTrace();
            }
            if(c<0)break;
            read+=c;
            if(sendCount>128)
            {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                    sendCount=0;
                    System.gc();
                    System.out.println("文件发送"+file.getName()+","+sendSum);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(c<sendLen)
            {
                data=new byte[c];
                System.arraycopy(buf, 0, data, 0, c);
                target.sendData(data);
                sendCount++;
                sendSum++;
            }
            else
            {
                int offset=0;
                int len=c;
                while(len>0)
                   {
                       int clen=len>sendLen?sendLen:len;
                       data=new byte[clen];
                       System.arraycopy(buf, offset, data, 0, clen);
                       target.sendData(data);
                       len=len-clen;
                       offset+=clen;
                       sendCount++;
                       sendSum++;
                       if(waitTime>0&&sendSum%waitTime==0)
                       {
                           try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException e) {
                         
                            e.printStackTrace();
                        }
                       }
                       
                   }
                   
                    
                }
            if(read>=size && size>-1)break;
            }
          
        }
    
    /*
     * 接收数据
     * 拷贝数据
     */
    static void CopySocketFile(FileOutputStream fos, judpClient target,long size, boolean flush)
    {
        //byte[]buf=new byte[8*65536];
        OutputStream os=new BufferedOutputStream(fos,1024*1024);
        byte[]buf=new byte[bufsize];
         int c = 0;
        long read=0;
        while(true){
            c=target.read(buf);
            if(c<0)break;
            try
            {
            read+=c;
            os.write(buf, 0, c);
            if(flush)os.flush();
            if(read>=size && size>-1)break;
            }
            catch(Exception ex)
            {
                
            }
        }
        if(!flush)
            try {
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
          
        }
    
}
