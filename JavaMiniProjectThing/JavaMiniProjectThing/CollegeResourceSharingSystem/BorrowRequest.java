
public class BorrowRequest
{
    private static int nextRequestId = 7001;

    private final int requestId;
    private final Student requester;
    private final Resource resource;

    private String status;
    private String decisionMessage;

    public BorrowRequest(Student requester, Resource resource)
    {
        this.requestId = nextRequestId++;
        this.requester = requester;
        this.resource = resource;
        this.status = "PENDING";
        this.decisionMessage = "Awaiting owner decision";
    }

    public int getRequestId()
    {
        return requestId;
    }

    public Student getRequester()
    {
        return requester;
    }

    public Resource getResource()
    {
        return resource;
    }

    public String getStatus()
    {
        return status;
    }

    public String getDecisionMessage()
    {
        return decisionMessage;
    }

    public boolean isPending()
    {
        return status.equals("PENDING");
    }

    public boolean accept()
    {
        if (!isPending())
        {
            return false;
        }

        status = "ACCEPTED";
        decisionMessage = "Accepted by resource owner";
        return true;
    }

    public boolean reject()
    {
        if (!isPending())
        {
            return false;
        }

        status = "REJECTED";
        decisionMessage = "Rejected by resource owner";
        return true;
    }

    public void displayRequest()
    {
        System.out.println("--------------------------------------------");
        System.out.println("Request ID : " + requestId);
        System.out.println("Resource   : " + resource.getName());
        System.out.println("Resource ID: " + resource.getResourceId());
        System.out.println("Owner      : " + resource.getOwner().getName());
        System.out.println("Requester  : " + requester.getName());
        System.out.println("Requester ID: " + requester.getStudentId());
        System.out.println("Status     : " + status);
        System.out.println("Decision   : " + decisionMessage);
        System.out.println("--------------------------------------------");
    }
}
