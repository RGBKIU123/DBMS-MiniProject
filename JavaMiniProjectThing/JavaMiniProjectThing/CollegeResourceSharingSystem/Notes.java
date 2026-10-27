public class Notes extends Book
{
    private String noteType;

    public Notes(String name,
                 String description,
                 String condition,
                 Student owner,
                 String author,
                 String subject,
                 String noteType)
    {
        super(
            name,
            "Notes",
            description,
            condition,
            owner,
            author,
            subject
        );

        this.noteType = noteType;
    }

    public String getNoteType()
    {
        return noteType;
    }

    public void setNoteType(String noteType)
    {
        this.noteType = noteType;
    }

    @Override
    public String getResourceType()
    {
        return "Notes";
    }

    @Override
    public void displaySpecificDetails()
    {
        super.displaySpecificDetails();

        System.out.println("Note Type   : " + noteType);
    }
}