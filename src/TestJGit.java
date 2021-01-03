public class TestJGit {
    public static void main(String args[]) {
        // 创建jGit对象
        jGit wareHouse = new jGit("/Users/wangqinqi/desktop/test/recipes");
        try {
            // 初始化仓库
            jGit.init();
            // 测试add操作
            wareHouse.add("");
            // 测试rollBack操作
//            wareHouse.rollBack();
            // 测试branch操作 (new、switch、show)
            wareHouse.newBranch("dev_wqq");
            wareHouse.showBranches();
            wareHouse.switchBranch("dev_wqq");
            wareHouse.showBranches();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
