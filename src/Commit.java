//import java.io.*;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.Scanner;
//
//public class Commit extends ObjectStore{
//    String msg;
//    String timeStamp;
//    String lastTreeKey;
//    String latestTreeKey;
//    String lastCommitKey;
//    String latestCommitKey;
//
//    Commit() throws Exception {
//        setType("Commit");
//        doTimeStamp();
//        Scanner input = new Scanner(System.in);
//        System.out.println("输入备注");
//        this.msg = input.nextLine();
//        if(isCommitable())
//            createCommit();
//    }
//
//    //生成TimeStamp
//    private void doTimeStamp(){
//        Timestamp time =new Timestamp(System.currentTimeMillis());
//        this.timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
//    }
//
//    private void updateHead() throws Exception {
//        File Head = new File(path+File.pathSeparator+"Head");
//        //如果还没有Head文件，则新生成一个
//        if(!Head.exists()){
//            Head.createNewFile();
//        }
//        //以覆盖的方式向文件中写入最新Commit的key
//        FileWriter fw = new FileWriter(Head,false);
//        fw.write(latestCommitKey);
//        fw.flush();
//        fw.close();
//    }
//
//    protected boolean isCommitable() throws Exception {
//        File Head = new File(path+File.pathSeparator+"Head");
//        //如果Head存在，则取出Commit key，进而取出上一次的tree key
//        if(Head.exists()){
//            FileReader fr = new FileReader(Head);
//            char[] head = new char[20];
//            fr.read(head);
//            lastCommitKey = String.valueOf(head);
//            //读取treekey
//            FileReader fr2 = new FileReader(getValue(lastCommitKey));
//            char[] treekey = new char[45];
//            fr2.read(treekey,4,40);
//            lastTreeKey = String.valueOf(treekey);
//        }
//
//
//        latestTreeKey = new Tree(resourcePath).getKey();
//        if(latestTreeKey.equals(lastCommitKey)){
//            System.out.println("仓库无变化！");
//            return false;
//        }
//        else
//            return true;
//    }
//
//    protected void createCommit() throws Exception {
//        File commit = new File(path);
//        FileWriter fr1 = new FileWriter(commit);
//        fr1.write("tree "+latestTreeKey+"\n");
//        //如果有parent commit 则写入，没有则跳过这行
//        if(!lastCommitKey.isEmpty())
//            fr1.append("parent "+lastCommitKey+"\n");
//        fr1.append("Time: "+timeStamp);
//        fr1.append(msg);
//
//        //计算新commit的key,并命其为文件名
//        latestCommitKey = new Hash(commit).getHashcode();
//        commit.renameTo(new File(latestCommitKey));
//        //更新Head
//        updateHead();
//    }
//}
