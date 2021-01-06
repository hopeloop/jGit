import java.io.*;
import java.util.ArrayList;

public class jGit {
    public static String repoPath; // 待管理项目的路径
    public static Branch branch; // 分支对象
    public static String committer;// 提交者
    public static String jGitPath;

    public jGit(String repoPath) {
        this.jGitPath=repoPath+File.separator+"jGit";
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
            File dir2 = new File(repoPath+File.separator+ "jGit" + File.separator +"logs");
            if(!dir2.exists())
                dir2.mkdir();
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

    public void reset(String commitId)  throws Exception {
        branch.changeWareHouse(commitId);
    }

    public void viewLog() throws IOException {
        //获取log
        ArrayList<String> arrayList =
            ObjectStore.getValue_inLines(repoPath+File.separator+ "jGit" + File.separator +"logs"+File.separator+branch.currBranch);

        int len = arrayList.size();
        if(len==0){
            System.out.println("No Commit has been performed.");
        }
        for(int i=len-1; i>-1; i--){
            String content = arrayList.get(i);
            String[] arr= content.split(" ");
            System.out.println("commit "+arr[1]+arr[2]);
            System.out.println("Author:"+arr[3]);
            System.out.println("Date:"+arr[4]+" "+arr[5]+"\n");
            System.out.println(arr[6]);
        }
    }

    // add操作，入参是相对路径
    public static void add(String addPath) throws Exception {
        File file = new File(repoPath + File.separator + addPath);
        if(file.isFile()){
            Blob b = new Blob(file.getPath().replace(repoPath+File.separator,""));
            String[] v = b.toString().split(" ");
            String value = v[1]+" "+v[2];
            ObjectStore.writeIn("index",new StringBuilder(value),jGitPath,true);
            System.out.println("已更新到index:"+v);
        }
        else{
            File[] files = file.listFiles();
            for(File f:files){
                Blob b = new Blob(f.getPath().replace(repoPath+File.separator,""));
                String value = b.getKey()+" "+f.getPath().replace(repoPath+File.separator,"")+"\n";
                ObjectStore.writeIn("index",new StringBuilder(value),jGitPath,true);
                System.out.println("已更新到index:"+value);
            }
        }
    }

}
