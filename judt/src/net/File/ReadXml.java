/**
 * 
 */
package net.File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLDecoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author jinyu
 *
 */
public class ReadXml {
	public static String getPath() {  
        URL url = ReadXml.class.getProtectionDomain().getCodeSource().getLocation();  
        String filePath = null;  
        try {  
            filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        if (filePath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"  
            // 截取路径中的jar包名  
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);  
        }  
          
        File file = new File(filePath);  
          
        // /If this abstract pathname is already absolute, then the pathname  
        // string is simply returned as if by the getPath method. If this  
        // abstract pathname is the empty abstract pathname then the pathname  
        // string of the current user directory, which is named by the system  
        // property user.dir, is returned.  
        filePath = file.getAbsolutePath();//得到windows下的正确路径  
        return filePath;  
    }  
public String readXml(String file)
{
	File f=new File(file);
	if(!f.exists())
	{
		return "";
	}
	//
	String xmlStr= readFile(file).trim();
	StringReader sr = new StringReader(xmlStr); 
	InputSource is = new InputSource(sr); 
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
	DocumentBuilder builder = null;
	try {
		builder = factory.newDocumentBuilder();
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	Document doc = null;
	try {
		doc = (Document) builder.parse(is);
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	NodeList list=doc.getElementsByTagName("IP");
    String ip=	list.item(0).getTextContent();
    list=doc.getElementsByTagName("Port");
    String port=list.item(0).getTextContent();
    list=doc.getElementsByTagName("Dir");
    String dir=list.item(0).getTextContent();
    String strxml=ip+","+port+","+dir;
    return strxml;
	}
private String readFile(String file)
{
	 StringBuilder result = new StringBuilder();
     try{
         BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
         String s = null;
         while((s = br.readLine())!=null){//使用readLine方法，一次读一行
             result.append(System.lineSeparator()+s);
         }
         br.close();    
     }catch(Exception e){
         e.printStackTrace();
     }
     return result.toString();
}
}
