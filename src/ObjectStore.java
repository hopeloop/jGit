import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ObjectStore {
    protected String repoPath=jGit.repoPath; // 仓库
    protected String objectsSubPath; // object存放目录相对路径
    protected String logsPath;//log文件存放的绝对地址
    protected String headsPath;//branch head存放的绝对地址
    private String key;  // object的key值
    private String type; // object的类型
    private String name; // object源文件(夹)的名称

    protected ObjectStore() {
        logsPath=repoPath+File.separator+"jGit"+File.separator+"logs";
        objectsSubPath = "jGit" + File.separator + "objects";
        headsPath = Branch.getHeadPath();
        // 检测objects目录是否存在，不存在则创建
        File dir = new File(repoPath + File.separator + objectsSubPath);
        if (!dir.exists())
            dir.mkdir();
    }

    public void setType(String t){
        this.type = t;
    }

    public String getType(){
        return this.type;
    }

    public String createKey(File file) throws Exception{
        Hash hash = new Hash(file);
        key = hash.getHashcode();
        return key;
    }

    public String getKey(){
        return this.key;
    }

    /**
     * 在path目录下创建object文件（filename:源文件hash值 content:源文件内容）
     * @param targetSubPath
     * @throws Exception
     */
    protected void createFile(String targetSubPath) throws Exception { // 源文件(夹)相对路径
        //创建源文件(夹)的File对象
        File file = new File(repoPath + File.separator + targetSubPath);
        name = file.getName();

        // 是Blob对象
        if (type.equals("Blob")) {
            //如果源文件已创建过object，那就不需要创建新object，直接返回
            if (existOrNot(createKey(file)))
                return;
            // 在objects目录创建文件暂存输出
            File KVFile = new File(repoPath + File.separator + objectsSubPath, key);
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
        // 是Tree对象
        else if (type.equals("Tree")) {
            // 在objects目录创建文件暂存输出
            File KVFile = new File(repoPath + File.separator + objectsSubPath, "temp" + System.currentTimeMillis());
            // 新建输出流
            FileOutputStream os = new FileOutputStream(KVFile);
            String s = new String();
            // 遍历为文件夹下的文件和文件夹创建Blob、Tree
            File[] files = file.listFiles();
            for (File f : files) {
                ObjectStore obj = null;
                if (f.isFile()) {
                    if (f.getName().equals(".DS_Store"))
                        continue;
                    obj = new Blob(targetSubPath + File.separator + f.getName());
                } else {
                    if (f.getName().equals("jGit"))
                        continue;
                    obj = new Tree(targetSubPath + File.separator + f.getName());
                }
                s += obj.getType() + " " + obj.getKey() + " " + obj.getName() + "\n";
            }
            os.write(s.getBytes());
            os.close();
            // 将文件名改为key值
            key = new Hash(KVFile).getHashcode();
            KVFile.renameTo(new File(repoPath + File.separator + objectsSubPath, key));
        }
    }

    //传入文件名key（哈希值），判断待创建的blob文件是否已存在
    private boolean existOrNot(String key) {
        File f = new File(repoPath + File.separator + objectsSubPath);
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
     * （可外部调用）给定key，返回文件
     * @param key
     */
    protected File getValue(String key) {
        File file = new File(repoPath + File.separator + objectsSubPath);
        File[] files = file.listFiles();
        for (File KVFile : files)
            if (KVFile.getName().equals(key)) {
                return KVFile;
            }
        System.out.println("No file named '" + key + "'!");
        return (new File(repoPath + File.separator + objectsSubPath,"null"));
    }

    protected String getName() {
        return name;
    }

    protected static ArrayList<String> getValue_inLines(String filePath) throws IOException {
        File file = new File(filePath);
        return getValue_inLines_from_File(file);
    }

    protected static ArrayList<String> getValue_inLines_from_File(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content;
        ArrayList<String> value = new ArrayList<>();
        while((content = br.readLine())!=null){
            value.add(content);
        }
        br.close();
        return value;
    }

    protected void writeIn(String FileName,StringBuilder value,String savePath,boolean append) throws IOException {
        File file = new File(savePath+File.separator+FileName);
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fr = new FileWriter(file,append);
        fr.write(value.toString());
        fr.flush();
        fr.close();
    }
}
