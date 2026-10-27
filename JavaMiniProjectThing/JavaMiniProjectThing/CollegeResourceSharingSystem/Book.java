public abstract class Book extends Resource
{
    private String author;
    private String subject;

    public Book(String name,
                String category,
                String description,
                String condition,
                Student owner,
                String author,
                String subject)
    {
        super(name, category, description, condition, owner);

        this.author = author;
        this.subject = subject;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    @Override
    public void displaySpecificDetails()
    {
        System.out.println("Author      : " + author);
        System.out.println("Subject     : " + subject);
    }
}