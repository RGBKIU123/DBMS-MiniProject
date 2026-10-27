public class Textbook extends Book
{
    private int edition;

    public Textbook(String name,
                    String description,
                    String condition,
                    Student owner,
                    String author,
                    String subject,
                    int edition)
    {
        super(
            name,
            "Textbook",
            description,
            condition,
            owner,
            author,
            subject
        );

        this.edition = edition;
    }

    public int getEdition()
    {
        return edition;
    }

    public void setEdition(int edition)
    {
        this.edition = edition;
    }

    @Override
    public String getResourceType()
    {
        return "Textbook";
    }

    @Override
    public void displaySpecificDetails()
    {
        super.displaySpecificDetails();

        System.out.println("Edition     : " + edition);
    }
}