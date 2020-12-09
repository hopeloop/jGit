
import java.io.*;

public class test1 {

    public static void main(String[] args) throws Exception {
       Blob blob = new Blob();
       blob.createBlobFile("./java_hw_project/README.md");
       System.out.println(blob.getValue("2de9f02744ff99cd36371cf1ede1c14f811b54f"));
    }
}