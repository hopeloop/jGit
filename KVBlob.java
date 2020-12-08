import java.io.*;
import java.security.MessageDigest;


public class KVBlob {

    private String path;

    //如果用户没有指定存储位置，则在根目录新建文件夹object，用于存储生成的blob文件
    KVBlob(){
        File file = new File("object");
        System.out.println("创建object文件夹："+file.mkdir());
        path = "./object";
    }

    //也可使用用户指定的存储地点(输入相对地址)
    KVBlob(String user_assigned_path){
        path = user_assigned_path;
    }

    //（内部方法）获得hash值
    private static String SHA1Checksum(InputStream is) throws Exception {
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

        byte[] sha1 = complete.digest();
        String result = "";
        for (int i = 0; i < sha1.length; i++) {
            result += Integer.toString(sha1[i]&0xFF, 16);
        }
        return result;
    }

    //(内部方法) 创建文件在path目录下创建 参数：文件名hash,源文件产生的文件字节流 is（filename:原文件hash值 content:原文件内容）
    public void createBlobFile(String hash,FileInputStream is) throws IOException {
        //如果已经有对应的blob，那就不需要创建新文件，直接返回
        if(existOrNot(hash))
            return;
        else {
            //在path地址处创建名字为hash值的文件
            File blobFile = new File(path,hash);
            //建立文件缓冲区
            byte[] buffer = new byte[1024];
            //从文件名建立一个FileOutputStream
            FileOutputStream out = new FileOutputStream(blobFile);
            int numRead = 0;
            do{
                numRead = is.read(buffer);
                if (numRead > 0){
                    out.write(buffer,0,numRead);
                }
            }while (numRead != -1);
            out.close();
        }
    }

    //(内部方法)传入blob文件名（哈希值），判断待创建的blob文件是否已存在
    private boolean existOrNot(String blobname){
        File f = new File(path);
        //列出存储位置的所有文件，遍历一遍，如果有文件名与待创建的文件名相同则返回true
        File[] files = f.listFiles();
        for(File file:files){
            if(file.getName().equals(blobname)){
                System.out.println("重复文件");
                return true;
            }
        }
        return false;
    }


    /**
     * (可外部调用)传入待hash文件的地址
     * 将在path目录下创建单个文件的Blob（filename:源文件hash值 content:源文件内容）
     * @param resourceFilePath
     */
    public void putKV(String resourceFilePath){
        try{
            File file = new File(resourceFilePath);
            //从源文件中读取字节，创建FileInputStream对象
            FileInputStream is = new FileInputStream(file);
            //获取源文件hash值
            String hash = SHA1Checksum(is);
//            System.out.println(hash);
            //实现该文件的KV存储
            createBlobFile(hash,is);
            //关闭字节流
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * （可外部调用）给定key也就是hash值，返回文件的绝对地址
     * @param key
     */
    public String getValue(String key){
        File file = new File(path);
        File[] files = file.listFiles();
        for(File KVFile:files)
            if(KVFile.getName().equals(key)){
                System.out.println("文件地址如下：");
                //返回该文件的绝对地址
                return KVFile.getAbsolutePath();
            }
        return "找不到对应的value";
    }
}
