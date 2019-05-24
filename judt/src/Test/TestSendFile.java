/**    
 * 文件名：TestSendFile.java    
 *    
 * 版本信息：    
 * 日期：2017年8月27日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package Test;

import java.io.IOException;

import judp.judpSendFile;

/**    
 *     
 * 项目名称：judp    
 * 类名称：TestSendFile    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年8月27日 下午6:32:25    
 * 修改人：jinyu    
 * 修改时间：2017年8月27日 下午6:32:25    
 * 修改备注：    
 * @version     
 *     
 */
public class TestSendFile {

    /**    
        
     * TODO(这里描述这个方法适用条件 – 可选)     
       
     * @param   name    
        
     * @return  
       
     *  
    
       
    */
    public static void main(String[] args) {
        judpSendFile jsend=new judpSendFile("192.168.3.104",5555);
        jsend.startSend();
        
        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
