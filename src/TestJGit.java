import java.io.File;

public class TestJGit {
    public static void main(String args[]) {
        String path="C:\\Users\\cherr\\IdeaProjects\\java_hw_project\\recipes";
        // 创建jGit对象
        jGit wareHouse = new jGit(path);

        try {
            // 初始化仓库
//            jGit.init();

            //测试commit
            jGit.commit("第一次commit");   //结果：4f30cfbc82c9fca11f6222d4b6bfc434422c415 第一次commit
            File file =new File(path+File.separator+"toDe");
            file.delete();
            //测试commit有无变化
            jGit.commit("第二次commit:删除了toDe文件"); //结果：021a77f58b1ee4ec8781b4bfa9b4bc6246a341  第二次commit:删除了toDe文件
            jGit.commit("第三次commit:没有任何改动"); //结果：仓库无变化！ null  第三次commit:没有任何改动

            // 测试branch操作 (new、switch、show、rollBack)
            wareHouse.newBranch("dev_wqq");
            wareHouse.showBranches();

            wareHouse.switchBranch("dev_wqq");
            wareHouse.showBranches();

            wareHouse.rollBack();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
