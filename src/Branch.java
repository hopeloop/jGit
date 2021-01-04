import java.io.*;

public class Branch {
    private static String repoPath; // 仓库路径(绝对)
    private static String headPath; // heads文件夹路径(绝对)
    private String currBranch; // 当前分支名
    private ObjectStore objStore; // 用该对象调用getValue()

    public Branch(String repoPath, String currBranch) {
        this.repoPath = repoPath;
        this.headPath = repoPath + File.separator +"jGit" + File.separator + "refs" + File.separator + "heads";
        this.currBranch = currBranch;
        objStore = new ObjectStore(repoPath);
    }

    // 获得当前分支名
    public String getBranch() {
        return currBranch;
    }

    // 生成一个新分支(不自动切换到该分支)，入参为新分支名称
    public boolean newBranch(String branchName) throws Exception {
        File newBranch = new File(headPath + File.separator + branchName);
        if (newBranch.exists()) {
            System.out.println("Branch '" + branchName + "' already exists!");
            return false;
        }
        newBranch.createNewFile(); // 在heads文件夹下新建文件保存分支的HEAD
        return true;
    }

    // 显示本地所有分支
    public void showBranches() {
        File[] branches = new File(headPath).listFiles();
        for (File f : branches) {
            if (f.getName().equals(".DS_Store"))
                continue;
            String name = f.getName();
            System.out.print(name);
            if (name.equals(currBranch))
                System.out.println("*");
            else
                System.out.println();
        }
    }

    // 当前分支回滚为上一个提交
    public Boolean rollBack() throws Exception {
        String currCommit = getCommit();
        System.out.println(currCommit);
        if (currCommit == null) // 当前branch没有commit
            return false;
        String lastCommit = getParent(currCommit);
        if (lastCommit == null) // 当前commit没有parent
            return false;

        clearOldFiles(); // 先清空已有文件
//        changeWareHouse(lastCommit); // 还原上一次commit的文件状态
        editBranchHead(lastCommit); // 修改Head文件指向上一次commit
        return true;
    }

    // 切换分支，入参为要切换到的分支名
    public boolean switchBranch(String theBranch) throws Exception {
        File branchRec = new File(headPath + File.separator + theBranch);
        if (!branchRec.exists()) {
            System.out.println("Branch not found!");
            return false;
        }
        this.currBranch = theBranch;
        editHEAD(theBranch); // 修改HEAD文件指向切换到的分支

        clearOldFiles(); // 先清空已有文件
        changeWareHouse(getCommit());
        return true;
    }

    // 获取分支最新的commitId
    private String getCommit() throws Exception {
        File head = new File(headPath + File.separator + currBranch);
        FileReader fr = new FileReader(head);
        BufferedReader reader = new BufferedReader(fr);
        String currCommitId = reader.readLine();
        reader.close();
        fr.close();

        if (currCommitId == null) // 若当前分支没有commit
            System.out.println("No commit on current branch!");

        return currCommitId;
    }

    // 获得commit对应的parent
    private String getParent(String commitId) throws Exception {
        FileReader fr2 = new FileReader(objStore.getValue(commitId));
        BufferedReader reader2 = new BufferedReader(fr2);
        String lastCommitId = null;
        for (String tempStr = reader2.readLine(); tempStr != null; )
            if (tempStr.startsWith("parent"))
                lastCommitId = tempStr.substring(7);

        reader2.close();
        fr2.close();
        if (lastCommitId == null) // 没有parent
            System.out.println("No parent commit!");
        return lastCommitId;
    }

    // 获得commit对应的tree
    private String getTree(String commitId) throws IOException {
        FileReader fr = new FileReader(objStore.getValue(commitId));
        BufferedReader reader = new BufferedReader(fr);
        String lastTreeId = reader.readLine().substring(5); // 读取commit对应tree的key
        reader.close();
        fr.close();
        return lastTreeId;
    }

    // 清空仓库中原有文件(夹)
    private static void clearOldFiles() {
        File[] files = new File(repoPath).listFiles();
        for (File f : files) {
            if (f.getName().equals("jGit")) // jGit文件夹保留
                continue;
            deleteFiles(f);
        }
    }

    // 删除文件夹操作
    private static void deleteFiles(File f) {
        File[] files = f.listFiles();
        if (files != null) // 非空文件夹，递归删除
            for (File subF : files)
                deleteFiles(subF);
        f.delete();
    }

    // 修改分支文件指向上一次commit
    private void editBranchHead(String lastCommitId) throws Exception{
        BufferedWriter bw = new BufferedWriter(new FileWriter(headPath + File.separator + currBranch));
        bw.write(lastCommitId);
        bw.flush();
        bw.close();
    }

    // 修改HEAD文件指向另一个分支
    private void editHEAD(String theBranch) throws IOException {
        File head = new File(repoPath + File.separator + "jGit" + File.separator + "HEAD");
        FileWriter fw = new FileWriter(head, false); // 覆盖写入HEAD
        fw.write("ref: refs/heads/" + theBranch);
        fw.flush();
        fw.close();
        if (!head.exists())
            head.createNewFile();
    }

    // 仓库状态回到指定的commit
    private void changeWareHouse(String commitId) throws Exception {
        // 获得commit对应的tree
        String lastTreeId = getTree(commitId);
        // 读取上次提交对应的tree，恢复文件(夹)
        recoverWithTree(lastTreeId);
    }

    // 恢复tree对应的仓库状态
    private void recoverWithTree(String treeId) throws Exception {
        FileReader fr = new FileReader(objStore.getValue(treeId));
        BufferedReader reader = new BufferedReader(fr);
        String toRecover = null;
        while ((toRecover = reader.readLine()) != null) {
            String[] info = toRecover.split(" "); // split出来info[0]为类型，[1]为文件hash，[2]为文件(夹)名
            if (info[0].equals("Blob")) { // 恢复blob

            } else { // 恢复tree
                recoverWithTree(toRecover.substring(5));
            }
        }
        reader.close();
        fr.close();
    }
}
