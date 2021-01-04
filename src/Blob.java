public class Blob extends ObjectStore {
    //构造Blob
    Blob(String blobPath) throws Exception {
        setType("Blob");
        createFile(blobPath);
    }

    @Override
    public String toString() {
        return getType() + " " + getKey() + " " + getName() + "\n";
    }
}
