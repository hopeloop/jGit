import java.util.Scanner;
import java.util.regex.*;

public class interact {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("请输入用户名：");
        jGit.username = input.nextLine();
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
               //查看分支
               else if(in.equals("git branch")){
                   wareHouse.showBranches();
               }
               //新建分支
               else if(Pattern.matches("git branch.*",in)){
                   wareHouse.newBranch("dev");
               }
               //切换分支
               else if(Pattern.matches("git checkout.*",in)){
                   wareHouse.switchBranch("dev");
               }
               //回滚分支
               else if(in.equals("git rollback")){
                   wareHouse.rollBack();
               }
               else if(in.equals("exit")){
                   s=false;
               }
               else
                   System.out.println("不存在这项指令，请重新输入。\n");
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
