import java.io.File;

public class jGit {
    public static String repoPath; // 待管理项目的路径
    public static Branch branch; // 分支对象

    public jGit(String repoPath) {
        this.repoPath = repoPath;
        branch = new Branch(repoPath, "master"); // 初始分支为master
    }

    // 初始化仓库
    public static void init() throws Exception {
        File wareHouse = new File(repoPath + File.separator + "jGit");
        if (!wareHouse.exists()) {
            wareHouse.mkdirs();
            File refHouse = new File(repoPath + File.separator + "jGit" + File.separator + "refs" + File.separator + "heads");
            refHouse.mkdirs();
        }
        branch.newBranch("master");
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

    public boolean newBranch(String newBranch) throws Exception {
        return branch.newBranch(newBranch);
    }

    public boolean switchBranch(String theBranch) throws Exception {
        return branch.switchBranch(theBranch);
    } 

//    public void rollBack() throws Exception {
//        new RollBack(repoPath, currBranch);
//    }
}
