package CW_SEM2;

public class Node {
    String jobID;
    String dependencyID;
    Node next;

    public Node(String jobID, String dependencyID) {
        this.jobID = jobID;
        this.dependencyID = dependencyID;
        this.next = null;
    }
}
