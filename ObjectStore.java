import java.io.*;

public class KeyValueStore {
    protected String path;
    private String key;  //object的key值
    private String type; //object的类型
    private String name;

    //如果用户没有指定存储位置，则在根目录下新建两层文件夹jGit/object，用于存储object
    KeyValueStore() {
        File file = new File("jGit/object");
        file.mkdirs();
        path = "./jGit/object";
    }

    //也可使用用户指定的存储地点
    KeyValueStore(String user_assigned_path) {
        path = user_assigned_path;
    }

    public void setType(String t){
            this.type = t;
    }

    public String getType(){
            return this.type;
    }

    public String createKey(File file) throws Exception{
        Hash hash = new Hash(file);
        this.key=hash.getHashcode();
        return this.key;
    }

    public String getKey(){
            return this.key;
    }

    /**
     * 在path目录下创建object文件（filename:源文件hash值 content:源文件内容）
     * @param resourceFilePath
     * @throws Exception
     */
    public void copyFile(String resourceFilePath) throws Exception {
        //创建源文件的File对象
        File file = new File(resourceFilePath);
        name = file.getName();

        //如果源文件已创建过object，那就不需要创建新object，直接返回
        if (existOrNot(createKey(file)))
            return;
        else {
            //在path地址处创建名字为hash值的文件
            File KVFile = new File(path, key);
            //建立文件缓冲区
            byte[] buffer = new byte[1024];
            //从源文件中读取字节，创建FileInputStream对象
            FileInputStream is = new FileInputStream(file);
            //从文件名建立一个FileOutputStream
            FileOutputStream out = new FileOutputStream(KVFile);
            int numRead = 0;
            do {
                numRead = is.read(buffer);
                if (numRead > 0) {
                    out.write(buffer, 0, numRead);
                }
            } while (numRead != -1);
            is.close();
            out.close();
        }
    }

    //传入文件名key（哈希值），判断待创建的blob文件是否已存在
    private boolean existOrNot(String key) {
            File f = new File(path);
            //列出存储位置的所有文件，遍历一遍，如果有文件名与待创建的文件名相同则返回true
            File[] files = f.listFiles();
            for (File file : files) {
                if (file.getName().equals(this.key)) {
                    return true;
                }
            }
            return false;
        }

    /**
     * （可外部调用）给定key也就是hash值，返回文件
     * @param key
     */
    public File getValue(String key) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (File KVFile : files)
            if (KVFile.getName().equals(key)) {
                return KVFile;
            }
        return (new File(path,"null"));
    }

    public String getName() {
        return name;
    }
}
