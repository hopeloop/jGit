import java.io.*;

public class RollBack {
    private String repoPath; // 仓库路径(绝对)
    private String headPath; // Head文件夹路径(绝对)
    private String lastCommitId; // 应该回滚到的commit的id
    private ObjectStore objStore; // 用该对象调用getValue()

    public RollBack(String repoPath, String currBranch) throws Exception {
        this.repoPath = repoPath;
         this.headPath = repoPath + File.separator + "refs" + File.separator + "heads" + File.separator + currBranch;
         objStore = new ObjectStore(repoPath);
         if (isRollable()) { // 如果可以回滚
             clearOldFiles(); // 先清空已有文件
             roll(); // 还原上一次commit的文件状态
             editHead(); // 修改Head文件指向上一次commit
         }
    }

    // 检查是否有可回滚的提交，有则记录lastCommitId
    private boolean isRollable() throws Exception {
        File head = new File(headPath);
        // 检查当前分支是否有提交
        if (!head.exists()) {
            System.out.println("No commit on current branch!");
            return false;
        }
        // 获取当前commitId
        FileReader fr = new FileReader(head);
        BufferedReader reader = new BufferedReader(fr);
        String currCommitId = reader.readLine();
        reader.close();
        fr.close();
        // 检查是否有前一个commit可以回滚
        FileReader fr2 = new FileReader(objStore.getValue(currCommitId));
        BufferedReader reader2 = new BufferedReader(fr2);
        String tempStr = null;
        while ((tempStr = reader2.readLine()) != null) {
            if (tempStr.substring(0,6).equals("parent")) {
                lastCommitId = tempStr.substring(7);
                reader2.close();
                fr2.close();
                return true;
            }
        }
        reader2.close();
        fr2.close();
        System.out.println("No former commit to roll back!");
        return false;
    }

    // 清空仓库中原有文件(夹)
    private void clearOldFiles() {
        File[] files = new File(repoPath).listFiles();
        for (File f: files) {
            if (f.isFile())
                f.delete();
            else {
                if (!f.getName().equals("jGit")) {
                    deleteFolder(f);
                }
            }
        }
    }

    // 删除文件夹操作
    private void deleteFolder(File f) {
        File[] files = f.listFiles();
        for (File subF : files) {
            if (f.isFile())
                f.delete();
            else
                deleteFolder(f);
        }
        f.delete();
    }

    // 修改Head文件指向上一次commit
    private void editHead() throws Exception{
        BufferedWriter bw = new BufferedWriter(new FileWriter(headPath));
        bw.write(lastCommitId);
        bw.flush();
        bw.close();
    }

    // 回滚
    private void roll() throws Exception {
        // 获取待回滚提交对应的tree
        FileReader fr = new FileReader(objStore.getValue(lastCommitId));
        BufferedReader reader = new BufferedReader(fr);
        String lastTreeId = reader.readLine().substring(5);
        reader.close();
        fr.close();
        // 读取上次提交对应的tree，恢复文件(夹)
        recoverWithTree(lastTreeId);
    }

    // 恢复Tree
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
