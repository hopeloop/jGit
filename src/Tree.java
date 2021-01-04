public class Tree extends ObjectStore {
    public Tree(String treePath) throws Exception {
        setType("Tree");
        createFile(treePath);
    }

    @Override
    public String toString() {
        return getType() + " " + getKey() + " " + getName() + "\n";
    }
}
