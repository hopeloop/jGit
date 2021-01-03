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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
