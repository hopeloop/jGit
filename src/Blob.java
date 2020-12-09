import java.io.*;

public class Blob {

    private String workspace;

    Blob(String workspace) {
        this.workspace = workspace;
    }

    /**
     * 在path目录下创建被管理目录的blob文件（filename:源文件hash值 content:源文件内容）
     * @param file
     * @throws Exception
     */
    public String createBlob(File file) throws Exception {
        //创建源文件的File对象
        // File file = new File(resourceFilePath);
        String hashcode = new Hash().SHA1Checksum(file);

        //如果已经有对应的blob，那就不需要创建新文件，直接返回
        if (existOrNot(hashcode))
            return hashcode;
        else {
            //在path地址处创建名字为hash值的文件
            File blobFile = new File(workspace, hashcode);
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
        return hashcode;
    }

//    public void createDirBlobs(File file) throws Exception {
//        File[] fs = file.listFiles();
//        for (File f : fs) {
//            if (f.isDirectory()) // 是文件夹则递归生成其下每个文件的blob文件
//                createDirBlobs(f);
//            else
//                createFileBlob(f); // 是文件则生成其blob文件
//        }
//    }

    // 传入blob文件名（哈希值），判断待创建的blob文件是否已存在
    private boolean existOrNot(String blobName) {
        File f = new File(workspace);
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

}
