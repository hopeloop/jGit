import java.io.File;

public class KVCache {

    private String workspace; // 存放objects的目录
    private String repoPath; // 用户要管理的目录（生成jGit仓库的目录）

    // 新建仓库（相当于git init，只是此处需要将路径作为参数输入）
    public KVCache(String repoPath) {
        this.repoPath = repoPath;
        workspace = repoPath + "/jGit/objects/";
        File workDir = new File(workspace);
        workDir.mkdirs();
    }

    // 为一个文件(夹)生成tree、blob
    public void createKVs(String path) throws Exception {
        File file = new File(repoPath + path);
        if (file.isDirectory()) {
            new Tree(workspace).createTree(file);
        } else {
            new Blob(workspace).createBlob(file);
        }
    }

    /**
     * （可外部调用）给定key也就是hash值，返回文件的绝对地址
     * @param key
     */
    public File get(String key) {
        File file = new File(workspace);
        File[] files = file.listFiles();
        for (File KVFile : files)
            if (KVFile.getName().equals(key)) {
                return KVFile;
            }
        return new File(workspace, "file_not_found");
    }

}
