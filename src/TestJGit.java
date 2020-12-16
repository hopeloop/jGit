public class TestJGit {
    public static void main(String args[]) {
        // 创建jGit对象
        jGit wareHouse = new jGit("/Users/wangqinqi/desktop/1");
        try {
            // 初始化仓库
            jGit.init();
            // 测试add操作
            wareHouse.add("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
