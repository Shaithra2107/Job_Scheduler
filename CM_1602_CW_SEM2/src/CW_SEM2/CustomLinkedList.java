package CW_SEM2;

public class CustomLinkedList {
    private Node head;

    // Constructor initializes the linked list to be empty
    public CustomLinkedList() {
        this.head = null;
    }

    // Method to add a new node at the beginning of the linked list
    public void addNode(String jobID) {
        Node newNode = new Node(jobID);
        newNode.next = head;
        head = newNode;
    }

    // Method to check if a node with the given jobID exists in the linked list
    public boolean contains(String jobID) {
        Node current = head;
        while (current != null) {
            if (current.getJobID().equals(jobID)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    // Method to remove a node with the given jobID from the linked list
    public void remove(String jobID) {
        Node current = head;
        Node previous = null;
        while (current != null) {
            if (current.getJobID().equals(jobID)) {
                if (previous != null) {
                    previous.next = current.next;
                } else {
                    head = current.next;
                }
                return;
            }
            previous = current;
            current = current.next;
        }
    }

    // Method to get the head of the linked list
    public Node getHead() {
        return head;
    }

    // Method to display all nodes in the linked list
    public void display() {
        Node current = head;
        while (current != null) {
            System.out.println("JobID: " + current.getJobID());
            current = current.getNext();
        }
    }

    // Inner class representing a node in the linked list
    public static class Node {
        private String jobID;
        private Node next;

        // Constructor to create a new node with the given jobID
        public Node(String jobID) {
            this.jobID = jobID;
            this.next = null;
        }

        // Getter for jobID
        public String getJobID() {
            return jobID;
        }

        // Getter for next node
        public Node getNext() {
            return next;
        }

        // Setter for next node
        public void setNext(Node next) {
            this.next = next;
        }
    }
}
