
import java.io.*;

public class Blob {
    private String path;

    //如果用户没有指定存储位置，则在根目录下新建两层文件夹jGit/object，用于存储生成的blob文件
    Blob() {
        File file = new File("jGit/object");
        System.out.println("创建object文件夹：" + file.mkdirs());
        path = "./jGit/object";
    }

    //也可使用用户指定的存储地点
    Blob(String user_assigned_path) {
        path = user_assigned_path;
    }


    /**
     * 在path目录下创建blob文件（filename:源文件hash值 content:源文件内容）
     * @param resourceFilePath
     * @throws Exception
     */
    public void createBlobFile(String resourceFilePath) throws Exception {
        //创建源文件的File对象
        File file = new File(resourceFilePath);
        Hash hash = new Hash(file);

        //如果已经有对应的blob，那就不需要创建新文件，直接返回
        if (existOrNot(hash.getHashcode()))
            return;
        else {
            //在path地址处创建名字为hash值的文件
            File blobFile = new File(path, hash.getHashcode());
            //建立文件缓冲区
            byte[] buffer = new byte[1024];
            //从源文件中读取字节，创建FileInputStream对象
            FileInputStream is = new FileInputStream(file);
            //从文件名建立一个FileOutputStream
            FileOutputStream out = new FileOutputStream(blobFile);
            int numRead = 0;
            do {
                numRead = is.read(buffer);
                if (numRead > 0) {
                    out.write(buffer, 0, numRead);
                }
            } while (numRead != -1);
            is.close();
            out.close();
        }
    }

    //(内部方法)传入blob文件名（哈希值），判断待创建的blob文件是否已存在
    private boolean existOrNot(String blobName) {
            File f = new File(path);
            //列出存储位置的所有文件，遍历一遍，如果有文件名与待创建的文件名相同则返回true
            File[] files = f.listFiles();
            for (File file : files) {
                if (file.getName().equals(blobName)) {
                    System.out.println("重复文件");
                    return true;
                }
            }
            return false;
        }

    /**
     * （可外部调用）给定key也就是hash值，返回文件的绝对地址
     * @param key
     */
    public File getValue(String key) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File KVFile : files)
            if (KVFile.getName().equals(key)) {
                System.out.println("文件地址如下：");
                //返回该文件的绝对地址
                return KVFile;
            }
        return (new File(path,"null"));
    }
}
