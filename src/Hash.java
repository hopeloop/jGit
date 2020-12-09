import java.io.*;
import java.security.MessageDigest;

public class Hash {

    // 计算文件hashcode
    public static String SHA1Checksum(File file) throws Exception {
        FileInputStream is = new FileInputStream(file);
        // 用于计算hash值的文件缓冲区
        byte[] buffer = new byte[1024];
        // 使用SHA1哈希/摘要算法
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        int numRead = 0;
        do{
            // 读出numRead字节到buffer中
            numRead = is.read(buffer);
            if (numRead > 0){
                // 根据buffer[0:numRead]的内容更新hash值
                complete.update(buffer, 0, numRead);
            }
        }while (numRead != -1);
        //关闭字节流
        is.close();

        byte[] sha1 = complete.digest();
        String result = "";
        for (int i = 0; i < sha1.length; i++) {
            result += Integer.toString(sha1[i]&0xFF, 16);
        }
        return result;
    }

}
