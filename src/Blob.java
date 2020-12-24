public class Blob extends ObjectStore {
    //构造Blob
    Blob(String repoPath, String blobPath) throws Exception {
        super(repoPath);
        setType("Blob");
        createFile(blobPath);
    }

    @Override
    public String toString() {
        return getType() + " " + getKey() + " " + getName() + "\n";
    }
}
