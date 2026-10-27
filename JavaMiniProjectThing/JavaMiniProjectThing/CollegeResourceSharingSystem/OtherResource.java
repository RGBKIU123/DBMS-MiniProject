public class OtherResource extends Resource
{
    public OtherResource(String name, String category,
                         String description, String condition,
                         Student owner)
    {
        super(name, category, description, condition, owner);
    }

    @Override
    public String getResourceType()
    {
        return "Other";
    }

    @Override
    public void displaySpecificDetails()
    {
        // No additional fields for general resources
    }
}