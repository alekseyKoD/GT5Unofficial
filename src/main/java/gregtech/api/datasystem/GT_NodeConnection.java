package gregtech.api.datasystem;

import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_DataCable;

import java.util.ArrayList;

public class GT_NodeConnection {

    protected GT_DataNode mFirst, mSecond;

    public long maxDataFlow, currentFlow;
    public int maxDataChannels, usedDataChannels;
    private int maxFlow=  0;

    public GT_NodeConnection(ArrayList<GT_MetaPipeEntity_DataCable> aCables, GT_DataNode aFirst, GT_DataNode aSecond){
        mFirst = aFirst;
        mSecond = aSecond;
        maxDataFlow = Long.MAX_VALUE;
        maxDataChannels = Integer.MAX_VALUE;
        for(GT_MetaPipeEntity_DataCable tCable: aCables){
            if(tCable.mDataAmount < maxDataFlow)
                maxDataFlow = tCable.mDataAmount;
            if(tCable.mDataChannels < maxDataChannels)
                maxDataChannels = tCable.mDataChannels;
        }
        mFirst.addConnection(this);
        mSecond.addConnection(this);
    }

    public GT_DataNode getOppositeNode(GT_DataNode aNode){
        if(mFirst.equals( aNode))
            return mSecond;
        else if(mSecond.equals(aNode))
            return mFirst;
        else
            return null;
    }

    public void onTick(){
        currentFlow = 0;
        usedDataChannels = 0;
    }

}
