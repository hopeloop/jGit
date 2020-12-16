public class Tree extends ObjectStore {
    public Tree(String repoPath, String treePath) throws Exception {
        super(repoPath);
        setType("Tree");
        createFile(treePath);
    }

    @Override
    public String toString() {
        return getType() + " " + getKey() + " " + getName() + "\n";
    }
}
