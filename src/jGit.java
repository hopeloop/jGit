import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class jGit {
    public static String repoPath; // 待管理项目的路径
    public static Branch branch; // 分支对象
    public static String committer;// 提交者

    public jGit(String repoPath) {
        this.repoPath = repoPath;
        // 初始分支为master
        branch = new Branch(repoPath, "master");
    }

    // 初始化仓库
    public static void init() throws Exception {
        // 创建jGit仓库目录
        File wareHouse = new File(repoPath + File.separator + "jGit");
        if (!wareHouse.exists()) {
            wareHouse.mkdirs();
            File refHouse = new File(repoPath + File.separator + "jGit" + File.separator + "refs" + File.separator + "heads");
            refHouse.mkdirs();
        } else {
            System.out.println("JGit warehouse already exists!");
            return;
        }
        // 新建HEAD文件并指向master分支
        File head = new File(repoPath + File.separator + "jGit" + File.separator + "HEAD");
        FileWriter fw = new FileWriter(head, false);
        fw.write("ref: refs/heads/master");
        fw.flush();
        fw.close();
        head.createNewFile();
        // 新建master分支文件
        branch.newBranch("master");
    }


    public static void commit(String message) throws Exception {
        Commit commit = new Commit(message);
        System.out.println(commit.latestCommitKey+"\n"+commit.msg);
    }

    public boolean newBranch(String newBranch) throws Exception {
        return branch.newBranch(newBranch);
    }

    public boolean switchBranch(String theBranch) throws Exception {
        return branch.switchBranch(theBranch);
    }

    public void showBranches() {
        branch.showBranches();
    }

    public void rollBack() throws Exception {
        branch.rollBack();
    }

//    public void rollBack() throws Exception {
//        new RollBack(repoPath, currBranch);
//    }
}
