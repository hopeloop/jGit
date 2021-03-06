import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Commit extends ObjectStore{
    protected String msg;             //commit message
    protected String timeStamp;       //时间戳
    protected String lastTreeKey;     //上一次的tree key
    protected String latestTreeKey;   //当前根目录的tree key
    protected String lastCommitKey;   //上一次的commit id
    protected String latestCommitKey; //本次的commit id

    protected String committer;
    protected String curr_branch;

    //构造Commit类，需要传入参数 commit message
    Commit(String message) throws Exception {
        this.curr_branch=jGit.branch.currBranch;
        this.committer = jGit.committer;
        setType("Commit");
        doTimeStamp();
        this.msg = message;
        if(isCommitable()){
            //生成Commit Key-Value 并将value存入sb中
            StringBuilder sb= generateCommitKV();
            //Key-Value写入文件
            writeIn(latestCommitKey,sb,repoPath+File.separator+objectsSubPath,false);
            //记录到Log中
            writeIn(curr_branch,addToLog(),logsPath,true);
            //更新Head
            updateCommitKey();
        }


    }


    //生成TimeStamp
    private void doTimeStamp(){
        Timestamp time =new Timestamp(System.currentTimeMillis());
        timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }

    //更新当前分支的head
    private void updateCommitKey() throws Exception {
        //以覆盖的方式向文件中写入最新Commit的key
        writeIn(curr_branch,new StringBuilder(latestCommitKey),headsPath,false);
    }

    //判断根目录与上一次commit的根目录相对比有无变化
    private boolean isCommitable() throws Exception {
        //读取branch的第一行
        ArrayList<String> first_line = getValue_inLines(headsPath+File.separator+curr_branch);
        //生成最新的树,并获取最新的tree key
        latestTreeKey = new Tree("").getKey();
        //如果branch中存有最新一次的commit key，则取出进而取出上一次的tree key
        if(first_line.size()!=0){
            lastCommitKey = first_line.get(0);
            File file = getValue(lastCommitKey);
            String treeKey = getValue_inLines_from_File(file).get(0);
            lastTreeKey = treeKey.substring(5);
        }
        //branch中没有内容说明之前没有commit过，直接返回true
        else
            return true;

        if(latestTreeKey.equals(lastTreeKey)){
            System.out.println("仓库无变化！");
            return false;
        }
        else
            return true;
    }

    //生成本次commit的Key-Value,返回value
    private StringBuilder generateCommitKV() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("tree "+latestTreeKey+"\n");
        //如果有parent commit 则写入，没有则跳过这行
        if(lastCommitKey !=null)
            sb.append("parent "+lastCommitKey+"\n");
        sb.append("Time: "+timeStamp+"\n");      //加入时间戳
        sb.append("Committer:"+committer+"\n");  //加入committer
        sb.append(msg);
        //生成新commit key
        latestCommitKey = new Hash(sb).getHashcode();

        return sb;
    }

    private StringBuilder addToLog(){
        StringBuilder sb = new StringBuilder();
        sb.append(lastCommitKey+" ");
        sb.append(latestCommitKey +" ("+curr_branch+") ");
        sb.append(committer+" ");
        sb.append(timeStamp+" ");
        sb.append(msg+"\n");

        return sb;
    }
}
