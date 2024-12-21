package com.algorithmicintegration.lyy.entity.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

@Component
public class SSHClientUtil {
    private static final Logger log = LoggerFactory.getLogger(SSHClientUtil.class);
    private static ChannelExec channelExec;
    private static Session session = null;
    private static final int timeout = 60000;
    /**
     * 连接远程服务器
     * @param host ip地址
     * @param userName 登录名
     * @param password 密码
     * @param port 端口
     * @throws Exception
     */
    public static void FileUploader(String host,String userName,String password,int port,String localFilePath,String remoteFilePath) throws Exception{
        log.info("尝试连接到....host:" + host + ",username:" + userName + ",password:" + password + ",port:"
                + port);
        JSch jsch = new JSch(); // 创建JSch对象
        session = jsch.getSession(userName, host, port); // 根据用户名，主机ip，端口获取一个Session对象
        session.setPassword(password); // 设置密码
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config); // 为Session对象设置properties
        session.setTimeout(timeout); // 设置timeout时间
        session.connect(); // 通过Session建立链接
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand("scp -t " + remoteFilePath);
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();
        channel.connect();

        if (checkAck(in) != 0) {
            System.exit(0);
        }

        File localFile = new File(localFilePath);
        long fileSize = localFile.length();
        String command = "C0644 " + fileSize + " ";
        command += localFile.getName() + "\n";
        out.write(command.getBytes());
        out.flush();

        FileInputStream fis = new FileInputStream(localFilePath);
        byte[] buf = new byte[1024];
        while (true) {
            int len = fis.read(buf, 0, buf.length);
            if (len <= 0) break;
            out.write(buf, 0, len);
        }
        fis.close();
        out.write(0);
        out.flush();

        if (checkAck(in) != 0) {
            System.exit(0);
        }

        out.close();
        channel.disconnect();
        session.disconnect();

        System.out.println("File uploaded successfully.");
    }


    public String DockerRun(String host,String userName,String password,int port,String geoJsonString) throws Exception{
        log.info("尝试连接到....host:" + host + ",username:" + userName + ",password:" + password + ",port:"
                + port);
        JSch jsch = new JSch();
        Session session = jsch.getSession(userName, host, 22);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        // 构建 Docker 命令
        String dockerCommand = "docker run -i -t areaimage";

        // 在远程服务器上执行 Docker 命令
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(dockerCommand);
        OutputStream out = channelExec.getOutputStream(); // 获取标准输入流
        InputStream in = channelExec.getInputStream();
        channelExec.connect();

        // 将 GeoJSON 字符串写入标准输入
        out.write(geoJsonString.getBytes());
        out.flush();
        out.close(); // 关闭标准输入流

        // 读取命令执行结果
        StringBuilder result = new StringBuilder();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                result.append(new String(tmp, 0, i));
            }
            if (channelExec.isClosed()) {
                if (in.available() > 0) continue;
                break;
            }
            Thread.sleep(60000);
        }
        // 关闭连接
        channelExec.disconnect();
        session.disconnect();

        // 处理返回结果
        System.out.println("Docker container started successfully.");
        System.out.println("Result: " + result.toString());
        return result.toString();
    }

    private static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuilder sb = new StringBuilder();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // 错误
                System.out.print(sb.toString());
            }
            if (b == 2) { // 致命错误
                System.out.print(sb.toString());
            }
        }
        return b;
    }
}