package CW_SEM2;

import java.util.Scanner;

public class JobManager {
    private Job[] jobDetails;
    private static int count;
    private CustomLinkedList dependencyList;
    private final int max_size = 100;

    public JobManager() {
        jobDetails = new Job[max_size];
        count = 0;
        dependencyList = new CustomLinkedList();
    }

    public void addJob(String jobID, String jobName, String jobDescription, String jobOwner) {
        if (isFull()) {
            System.out.println("List is full you can't add any more jobs!!");
            return;
        }
        if (jobIDExists(jobID)) {
            System.out.println("Job with ID " + jobID + " is already added.");
            return;
        }
        System.out.println("Adding jobID " + jobID + " to the list!!!");
        jobDetails[count] = new Job(jobID, jobName, jobDescription, jobOwner);
        count++;
    }

    public void addMultipleJobs(int numberOfJobs) {
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < numberOfJobs; i++) {
            String jobID, jobName, jobDescription, jobOwner;
            while (true) {
                System.out.print("Enter Job ID for job " + (i + 1) + ": ");
                jobID = scanner.nextLine();
                if (jobID.isEmpty() || jobIDExists(jobID)) {
                    System.out.println("Invalid input. Job ID cannot be empty or already exist.");
                } else {
                    break;
                }
            }
            while (true) {
                System.out.print("Enter Job Name for job " + (i + 1) + ": ");
                jobName = scanner.nextLine();
                if (jobName.isEmpty()) {
                    System.out.println("Invalid input. Job Name cannot be empty.");
                } else {
                    break;
                }
            }
            while (true) {
                System.out.print("Enter Job Description for job " + (i + 1) + ": ");
                jobDescription = scanner.nextLine();
                if (jobDescription.isEmpty()) {
                    System.out.println("Invalid input. Job Description cannot be empty.");
                } else {
                    break;
                }
            }
            while (true) {
                System.out.print("Enter Job Owner for job " + (i + 1) + ": ");
                jobOwner = scanner.nextLine();
                if (jobOwner.isEmpty()) {
                    System.out.println("Invalid input. Job Owner cannot be empty.");
                } else {
                    break;
                }
            }
            addJob(jobID, jobName, jobDescription, jobOwner);
        }
    }

    public boolean isFull() {
        return count == max_size;
    }

    public boolean jobIDExists(String jobID) {
        for (int i = 0; i < count; i++) {
            if (jobDetails[i] != null && jobDetails[i].getJobID().equals(jobID)) {
                return true;
            }
        }
        return false;
    }

    public Job getJob(String jobID) {
        for (int i = 0; i < count; i++) {
            if (jobDetails[i].getJobID().equals(jobID)) {
                return jobDetails[i];
            }
        }
        return null;
    }

    public void addDependency(String jobID, String dependencyIDs) {
        if (count < 2) {
            System.out.println("You need at least 2 jobs to add dependencies.");
            return;
        }
        if (!jobIDExists(jobID)) {
            System.out.println("Job with ID " + jobID + " does not exist.");
            return;
        }

        String[] dependencies = dependencyIDs.split(",");
        for (String dependencyID : dependencies) {
            dependencyID = dependencyID.trim();
            if (!jobIDExists(dependencyID)) {
                System.out.println("Dependency Job with ID " + dependencyID + " does not exist.");
                return;
            }
            if (dependencyList.contains(jobID + " -> " + dependencyID)) {
                System.out.println("Dependency " + dependencyID + " already exists for Job " + jobID);
                continue;
            }

            // Check for circular dependencies before adding
            dependencyList.addNode(jobID + " -> " + dependencyID);
            if (hasCircularDependency(jobID)) {
                dependencyList.remove(jobID + " -> " + dependencyID);
                System.out.println("Adding this dependency creates a circular dependency. Dependency not added.");
                return;
            }
        }

        // Ensure there is at least one job without dependencies
        if (noExecutableJob()) {
            for (String dependencyID : dependencies) {
                dependencyList.remove(jobID + " -> " + dependencyID.trim());
            }
            System.out.println("Adding these dependencies will result in no executable job. Dependencies not added.");
            System.out.println("Please modify the dependencies to ensure at least one job remains executable.");
        }
    }

    private boolean hasCircularDependency(String jobID) {
        CustomLinkedList visited = new CustomLinkedList();
        CustomLinkedList stack = new CustomLinkedList();
        return detectCycle(jobID, visited, stack);
    }

    private boolean detectCycle(String jobID, CustomLinkedList visited, CustomLinkedList stack) {
        if (stack.contains(jobID)) {
            return true;
        }
        if (visited.contains(jobID)) {
            return false;
        }

        visited.addNode(jobID);
        stack.addNode(jobID);

        CustomLinkedList.Node current = dependencyList.getHead();
        while (current != null) {
            String[] parts = current.getJobID().split(" -> ");
            if (parts[0].equals(jobID)) {
                if (detectCycle(parts[1], visited, stack)) {
                    return true;
                }
            }
            current = current.getNext();
        }

        stack.remove(jobID);
        return false;
    }

    private boolean noExecutableJob() {
        for (int i = 0; i < count; i++) {
            if (!hasDependencies(jobDetails[i].getJobID())) {
                return false;
            }
        }
        return true;
    }

    private boolean hasDependencies(String jobID) {
        CustomLinkedList.Node current = dependencyList.getHead();
        while (current != null) {
            String[] parts = current.getJobID().split(" -> ");
            if (parts[0].equals(jobID)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    public boolean isJobReady(String jobID) {
        if (!jobIDExists(jobID)) {
            System.out.println("Job with ID " + jobID + " does not exist.");
            return false;
        }

        CustomLinkedList.Node current = dependencyList.getHead();
        while (current != null) {
            String[] parts = current.getJobID().split(" -> ");
            if (parts[0].equals(jobID)) {
                String dependencyID = parts[1];
                if (!getJob(dependencyID).isCompleted()) {
                    return false;
                }
            }
            current = current.getNext();
        }

        return true;
    }

    public String getNextExecutableJob() {
        for (int i = 0; i < count; i++) {
            if (isJobReady(jobDetails[i].getJobID()) && !jobDetails[i].isCompleted()) {
                return jobDetails[i].getJobID();
            }
        }
        return null;
    }

    public void executeJob(String jobID) {
        if (!isJobReady(jobID)) {
            System.out.println("Job with ID " + jobID + " is not ready to be executed.");
            return;
        }
        Job job = getJob(jobID);
        if (job != null && !job.isCompleted()) {
            job.completeJob();
            System.out.println("Job " + jobID + " has been executed.");
        }
    }

    public void displayJobs() {
        System.out.println("Job List:");
        for (int i = 0; i < count; i++) {
            System.out.println(jobDetails[i]);
        }
    }

    public void displayDependencies() {
        System.out.println("Job Dependencies:");
        dependencyList.display();
    }

    public static void main(String[] args) {
        JobManager manager = new JobManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add a single job");
            System.out.println("2. Add multiple jobs");
            System.out.println("3. Add dependencies");
            System.out.println("4. Check if a job is ready to be executed");
            System.out.println("5. Get the next executable job");
            System.out.println("6. Execute a job");
            System.out.println("7. Display jobs");
            System.out.println("8. Display dependencies");
            System.out.println("9. Exit");

            int choice = 0;
            while (true) {
                try {
                    System.out.print("Enter your choice: ");
                    choice = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 9.");
                }
            }

            switch (choice) {
                case 1:
                    String jobID, jobName, jobDescription, jobOwner;
                    while (true) {
                        System.out.print("Enter Job ID: ");
                        jobID = scanner.nextLine();
                        if (jobID.isEmpty() || manager.jobIDExists(jobID)) {
                            System.out.println("Invalid input. Job ID cannot be empty or already exist.");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.print("Enter Job Name: ");
                        jobName = scanner.nextLine();
                        if (jobName.isEmpty()) {
                            System.out.println("Invalid input. Job Name cannot be empty.");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.print("Enter Job Description: ");
                        jobDescription = scanner.nextLine();
                        if (jobDescription.isEmpty()) {
                            System.out.println("Invalid input. Job Description cannot be empty.");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.print("Enter Job Owner: ");
                        jobOwner = scanner.nextLine();
                        if (jobOwner.isEmpty()) {
                            System.out.println("Invalid input. Job Owner cannot be empty.");
                        } else {
                            break;
                        }
                    }
                    manager.addJob(jobID, jobName, jobDescription, jobOwner);
                    break;
                case 2:
                    int numberOfJobs;
                    while (true) {
                        System.out.print("Enter the number of jobs to add: ");
                        try {
                            numberOfJobs = Integer.parseInt(scanner.nextLine());
                            if (numberOfJobs > 100) {
                                System.out.println("Invalid input. You can add up to 100 jobs only.");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                        }
                    }
                    manager.addMultipleJobs(numberOfJobs);
                    break;
                case 3:
                    if (count < 2) {
                        System.out.println("You need at least 2 jobs to add dependencies.");
                        break;
                    }
                    System.out.print("Enter Job ID to add dependencies for: ");
                    String jobIDToAddDependencies = scanner.nextLine();
                    System.out.print("Enter comma-separated list of dependency Job IDs: ");
                    String dependencyIDs = scanner.nextLine();
                    manager.addDependency(jobIDToAddDependencies, dependencyIDs);
                    break;
                case 4:
                    System.out.print("Enter Job ID to check if it's ready to be executed: ");
                    String jobIDToCheck = scanner.nextLine();
                    boolean ready = manager.isJobReady(jobIDToCheck);
                    if (ready) {
                        System.out.println("Job " + jobIDToCheck + " is ready to be executed.");
                    } else {
                        System.out.println("Job " + jobIDToCheck + " is not ready to be executed.");
                    }
                    break;
                case 5:
                    String nextExecutableJob = manager.getNextExecutableJob();
                    if (nextExecutableJob != null) {
                        System.out.println("Next executable job is: " + nextExecutableJob);
                    } else {
                        System.out.println("No executable job found.");
                    }
                    break;
                case 6:
                    System.out.print("Enter Job ID to execute: ");
                    String jobIDToExecute = scanner.nextLine();
                    manager.executeJob(jobIDToExecute);
                    break;
                case 7:
                    manager.displayJobs();
                    break;
                case 8:
                    manager.displayDependencies();
                    break;
                case 9:
                    System.out.println("Exiting the job manager system.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 9.");
                    break;
            }
        }
    }
}
