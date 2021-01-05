import java.io.*;
import java.util.ArrayList;

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

    public void viewLog() throws IOException {
        ArrayList<String> arrayList = new ArrayList<>();

        File log = new File(repoPath+File.separator+ "jGit" + File.separator +"logs"+File.separator+branch.currBranch);
        FileReader fr = new FileReader(log);
        BufferedReader br = new BufferedReader(fr);
        String str;

        while((str = br.readLine())!=null){
            arrayList.add(str);
        }

        br.close();
        fr.close();

        int len = arrayList.size();
        for(int i=len-1; i>-1; i--){
            String content = arrayList.get(i);
            String[] arr= content.split(" ");
            System.out.println("commit "+arr[1]+arr[2]);
            System.out.println("Author:"+arr[3]);
            System.out.println("Date:"+arr[4]+" "+arr[5]+"\n");
            System.out.println(arr[6]);
        }
    }

//    public void rollBack() throws Exception {
//        new RollBack(repoPath, currBranch);
//    }
}
