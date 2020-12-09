import java.io.File;

public class KVTest {

    public static void main(String args[]) {
        // 在指定目录下建立仓库
        KVCache jGit = new KVCache("/Users/wangqinqi/desktop/tryjgit");
        try {
            // add要管理的文件(夹)
            jGit.createKVs("/2");
            // get指定hashcode为名的文件
            File f = jGit.get("b3dc55668532aa2f35196a182af14cbd781");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
