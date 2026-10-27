public class LabEquipment extends Resource
{
    private String equipmentType;
    private String laboratory;

    public LabEquipment(String name,
                        String description,
                        String condition,
                        Student owner,
                        String equipmentType,
                        String laboratory)
    {
        super(
            name,
            "Lab Equipment",
            description,
            condition,
            owner
        );

        this.equipmentType = equipmentType;
        this.laboratory = laboratory;
    }

    public String getEquipmentType()
    {
        return equipmentType;
    }

    public String getLaboratory()
    {
        return laboratory;
    }

    @Override
    public String getResourceType()
    {
        return "Lab Equipment";
    }

    @Override
    public void displaySpecificDetails()
    {
        System.out.println("Equipment   : " + equipmentType);
        System.out.println("Laboratory  : " + laboratory);
    }
}