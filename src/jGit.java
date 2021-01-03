import java.io.File;

public class jGit {
    // 待管理项目的路径
    public static String repoPath;
    public String currBranch;

    public jGit(String repoPath) {
        this.repoPath = repoPath;
        this.currBranch = "master";
    }

    // 初始化仓库
    public static void init() throws Exception {
        File wareHouse = new File(repoPath + File.separator + "jGit");
        if (!wareHouse.exists()) {
            wareHouse.mkdirs();
            File refHouse = new File(repoPath + File.separator + "jGit" + File.separator + "refs" + File.separator + "heads");
            refHouse.mkdirs();
        }
    }

    // add操作，入参是相对路径
    public void add(String addPath) throws Exception {
        if (new File(repoPath + File.separator + addPath).isFile())
            new Blob(repoPath, addPath);
        else
            new Tree(repoPath, addPath);
    }

    public static void commit() throws Exception {

    }

//    public void rollBack() throws Exception {
//        new RollBack(repoPath, currBranch);
//    }
}
