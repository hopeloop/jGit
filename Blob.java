public class Blob extends ObjectStore {
    //构造Blob
    Blob(String resourceFilePath) throws Exception {
        setType("Blob");
        copyFile(resourceFilePath);
    }

    @Override
    public String toString() {
        return getType()+" "+ getKey() +" "+ getName()+"\n";
    }
}
