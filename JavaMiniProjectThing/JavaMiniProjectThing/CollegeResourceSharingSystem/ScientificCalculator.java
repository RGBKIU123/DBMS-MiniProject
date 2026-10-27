public class ScientificCalculator extends Resource
{
    private String brand;
    private String model;
    private boolean hasCover;

    public ScientificCalculator(String name,
                                String description,
                                String condition,
                                Student owner,
                                String brand,
                                String model,
                                boolean hasCover)
    {
        super(
            name,
            "Scientific Calculator",
            description,
            condition,
            owner
        );

        this.brand = brand;
        this.model = model;
        this.hasCover = hasCover;
    }

    public String getBrand()
    {
        return brand;
    }

    public String getModel()
    {
        return model;
    }

    public boolean hasCover()
    {
        return hasCover;
    }

    @Override
    public String getResourceType()
    {
        return "Scientific Calculator";
    }

    @Override
    public void displaySpecificDetails()
    {
        System.out.println("Brand       : " + brand);
        System.out.println("Model       : " + model);
        System.out.println("Cover       : " + (hasCover ? "Yes" : "No"));
    }
}