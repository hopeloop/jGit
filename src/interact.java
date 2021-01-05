import java.util.Scanner;
import java.util.regex.*;

public class interact {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("请输入用户名：");
        jGit.committer= input.nextLine();
        System.out.println("请输入管理仓库的绝对地址：");
        String path= input.nextLine();

        // 创建jGit对象
        jGit wareHouse = new jGit(path);

        try {
            // 初始化仓库
           jGit.init();
           System.out.println("仓库已初始化！");

           boolean s = true;
           while(s){
               System.out.println("请输入命令：");
               String in = input.nextLine();
               if(Pattern.matches("git commit",in)){
                   System.out.println("输入message:");
                   jGit.commit(input.nextLine());
               }
               //查看分支 命令：git branch
               else if(in.equals("git branch")){
                   wareHouse.showBranches();
               }
               //新建分支 命令：git branch branch_name
               else if(Pattern.matches("git branch.*",in)){
                   Pattern r = Pattern.compile("git branch (.*)");
                   Matcher m = r.matcher(in);
                   m.find();

                   wareHouse.newBranch(m.group(1));
               }
               //切换分支 命令：git checkout branch_name
               else if(Pattern.matches("git checkout.*",in)){
                   Pattern r = Pattern.compile("git checkout (.*)");
                   Matcher m = r.matcher(in);
                   m.find();
                   wareHouse.switchBranch(m.group(1));
               }
               //回滚到本分支上一次commit 命令：git reset
               else if(in.equals("git reset")){
                   wareHouse.rollBack();
               }
               else if(in.equals("git log")){
                   wareHouse.viewLog();
               }
               else if(Pattern.matches("git reset --soft .*",in)){
                   Pattern r = Pattern.compile("git reset --soft (.*)");
                   Matcher m = r.matcher(in);
                   m.find();
                   wareHouse.reset(m.group(1));
               }
               //退出程序
               else if(in.equals("exit")){
                   s=false;
                   input.close();
               }
               else
                   System.out.println("不存在这项指令，请重新输入。\n");
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
