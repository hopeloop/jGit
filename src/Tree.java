import java.io.File;
import java.io.FileOutputStream;

public class Tree {

    private String workspace;
//    private Tree[] trees;
//    private Blob[] blobs;

    Tree(String workspace) {
        this.workspace = workspace;
    }

    public String createTree(File file) throws Exception {
        // 创建tree文件对象，创建输入流、临时命名
        File treeFile = new File(workspace, "temp_tree" + System.currentTimeMillis());
        treeFile.createNewFile();
        FileOutputStream os = new FileOutputStream(treeFile);
        File[] fs = file.listFiles();
        // 写入文件夹下每个文件(夹)对应生成的文件类型、hashcode、文件(夹)名称
        for (File f : fs) {
            byte[] objType; // 文件(夹)的类型
            String hashcode; // 文件(夹)的hashcode
            // 深度遍历文件夹，判断文件(夹)类型、计算hashcode
            if (f.isDirectory()) {
                hashcode = createTree(f);
                objType = "tree ".getBytes();
            } else {
                hashcode = new Blob(workspace).createBlob(f);
                objType = "blob ".getBytes();
            }
            // 写入文件类型
            os.write(objType);
            // 写入hashcode
            os.write(hashcode.getBytes());
            // 写入文件名称
            String joinName = " " + f.getName() + "\r\n";
            os.write(joinName.getBytes());
        }
        os.close();
        // 生成treeFile的hashcode，用hashcode命名文件、返回
        String treeFileHashcode = new Hash().SHA1Checksum(treeFile);
        treeFile.renameTo(new File(workspace, treeFileHashcode));
        return treeFileHashcode;
    }

}
