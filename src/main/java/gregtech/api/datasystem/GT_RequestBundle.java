package gregtech.api.datasystem;

public class GT_RequestBundle extends GT_InformationBundle {

    public int mComputation, mTime;
    public boolean isApproved;
    public IDataDevice mSender;

    public GT_RequestBundle(int aComputation, int aTime, IDataDevice aSender){
        super(0);
        mComputation = aComputation;
        mTime = aTime;
        mSender = aSender;
    }

    public GT_RequestBundle approve(){
        isApproved = true;
        return this;
    }
}
