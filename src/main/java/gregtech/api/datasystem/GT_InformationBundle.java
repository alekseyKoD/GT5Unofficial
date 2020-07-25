package gregtech.api.datasystem;


public class GT_InformationBundle {
    public int mDataFlow;

    public boolean isAutomated = false;
    public GT_DataNode to = null;
    public GT_DataNode from = null;

    public GT_InformationBundle(int aDataFlow){
        mDataFlow = aDataFlow;
    }

    public GT_InformationBundle  automate(GT_DataNode to){
        this.to = to;
        isAutomated = true;
        return this;
    }

    public GT_InformationBundle addSender(GT_DataNode node){
        from = node;
        return this;
    }
}
