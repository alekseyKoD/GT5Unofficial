package gregtech.api.datasystem;

public class GT_CalculationBundle extends GT_InformationBundle {

    public int mType;
    public GT_CalculationBundle(int aType, int aCalculations){
        super(aCalculations);
        mType = aType;

    }
}
