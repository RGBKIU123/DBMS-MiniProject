public abstract class Resource implements Borrowable, Searchable
{
    private static int nextResourceId = 5001;

    private final int resourceId;
    private String name;
    private String category;
    private String description;
    private String condition;
    private Student owner;
    private boolean available;

    public Resource(String name,
                    String category,
                    String description,
                    String condition,
                    Student owner)
    {
        this.resourceId = nextResourceId++;
        this.name = name;
        this.category = category;
        this.description = description;
        this.condition = condition;
        this.owner = owner;
        this.available = true;
    }

    public int getResourceId()
    {
        return resourceId;
    }

    public String getName()
    {
        return name;
    }

    public String getCategory()
    {
        return category;
    }

    public String getDescription()
    {
        return description;
    }

    public String getCondition()
    {
        return condition;
    }

    public Student getOwner()
    {
        return owner;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public String getBorrowingStatus()
    {
        if (available)
        {
            return "AVAILABLE";
        }

        return "UNAVAILABLE";
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public abstract String getResourceType();

    public abstract void displaySpecificDetails();

    public void displayDetails()
    {
        System.out.println("--------------------------------------------");
        System.out.println("Resource ID : " + resourceId);
        System.out.println("Name        : " + name);
        System.out.println("Category    : " + category);
        System.out.println("Description : " + description);
        System.out.println("Condition   : " + condition);
        System.out.println("Owner       : " + owner.getName());
        System.out.println("Owner ID    : " + owner.getStudentId());
        System.out.println("Type        : " + getResourceType());
        System.out.println("Status      : " + getBorrowingStatus());

        displaySpecificDetails();

        System.out.println("--------------------------------------------");
    }

    @Override
    public boolean matchesSearch(String searchTerm)
    {
        String term = searchTerm.toLowerCase().trim();

        return name.toLowerCase().contains(term)
                || category.toLowerCase().contains(term)
                || description.toLowerCase().contains(term)
                || condition.toLowerCase().contains(term)
                || getResourceType().toLowerCase().contains(term);
    }
}