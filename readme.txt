--------------------
非常抱歉各位，现在的测试程序不能直接使用；因为我是逐步完成测试的，不同的测试建立不同的文件，不同的main方法。
这样生成的启动包可能就对不上了；
但是我看了测试源码还在，只是没有生成。需要大家自己动手生成下。
项目里面有个Test包，下面就是测试代码，但是输出的jar文件却对不起来。
大家根据自己需要测试的，自己导出jar包运行。
我使用的eclipse。
---------------------


当前版本1.2
一.源码修改
2017.10.11
1.修改初始seqNo重置
2.修改Acknowledgment2实现getAdditionalInfo方法
3.修改UDTSender接收ack,最大值可能不正确
4.修改接收UDTInputStream添加hasData及方法，判断数据接收
5.UDTInputStream添加大数据接收方法接口resetBufMaster，setLargeRead，因为数据会覆盖（再读取慢时）
6.修改UDTSender添加数据判断，数据全部发送则返回true,f否则false
7.UDTClient修改，添加close字段及线程字段，添加close方法等待10s数据发送，原方法shutdown保留，直接立即关闭
二.封装代码   judp与net.File这2部分为扩展封装，不影响源代码使用
（1）judp 主要为了解决服务端产生很大无效的sesion问题
1.SocketManager不再使用，外部使用不在管理，需要自己使用完成后关闭

2.judpClient封装UDTClient数据发送端

3.judpServer封装UDTServerSocket数据接收端

4.judpSocket封装UDTServerSocket返回的udtsocket

5.judpRecviceFile 是客户端向服务端请求文件，接收服务端传回的文件‘

6.judpSendFiles  是服务端，接收客户端请求，向客户端传输文件
这部分的文件传输与 net.File部分的传输方向恰好相反
（2） net.File 通过封装的judp进行文件传输，同时提供目录监视功能
1.RecviceFiles封装文件接收，按照封装代码judp部分重新编写(源码文件接收没有动），服务端直接接收客户端传来的文件

2.SendFiles封装文件发送，按照封装代码重新编写(源码文件发送没有动），客户端直接向服务端发送文件


更新时间（更新内容见update.txt）
 2018-08-25
 2018-08-28


