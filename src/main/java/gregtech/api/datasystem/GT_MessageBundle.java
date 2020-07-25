package gregtech.api.datasystem;

public class GT_MessageBundle extends GT_InformationBundle {

    public int mID, mInformation;
    public Object[] mExtraInfromation;

    public GT_MessageBundle(int aID, int aInformation, Object... aExtraInformation){
        super(10);
        mID = aID;
        mInformation = aInformation;
        mExtraInfromation = aExtraInformation;
    }
}
