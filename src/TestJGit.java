public class TestJGit {
    public static void main(String args[]) {
        // 创建jGit对象
        jGit wareHouse = new jGit("C:\\Users\\cherr\\IdeaProjects\\java_hw_project\\recipes");
        try {
            // 初始化仓库
            jGit.init();

            //测试commit
            jGit.commit("第二次commit");

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
