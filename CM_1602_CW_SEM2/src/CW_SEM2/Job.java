package CW_SEM2;

public class Job {
    private String jobID;
    private String jobName;
    private String jobDescription;
    private String jobManager;
    private boolean isCompleted;

    public Job(String jobID, String jobName, String jobDescription, String jobManager) {
        this.jobID = jobID;
        this.jobName = jobName;
        this.jobDescription = jobDescription;
        this.jobManager=jobManager;
        this.isCompleted = false;
    }

    public String getJobID() {
        return jobID;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void completeJob() {
        this.isCompleted = true;
    }

    public String getJobManager() {
        return jobManager;
    }
    public void setJobManager(String jobOwner) {
        this.jobManager = jobOwner;
    }

    @Override
    public String toString() {
        return "JobID: " + jobID + ", Name: " + jobName + ", Description: " + jobDescription + ",Job Manager: "+jobManager+", Completed: " + isCompleted;
    }
}
