public class test1 {
    public static void main(String[] args) {
        //创建对象，没有传入地址,在根目录新建文件夹object，用于存储生成的blob文件
        KVBlob f= new KVBlob();
        //传入源文件地址，创建KV文件
        f.putKV("./java_hw_project/README.md");
        //给定指定key返回对应文件的绝对地址
        System.out.println(f.getValue("2de9f02744ff99cd36371cf1ede1c14f811b54f"));
    }
}