package gregtech.api.datasystem;

import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.HashSet;

public class GT_DataNode {

    public GT_NodeConnection[] mConnections;

    protected INodeContainer mContainer;

    public GT_DataNode(){
        this(null);
    }

    public GT_DataNode(INodeContainer aContainer){
        mContainer = aContainer;
    }

    protected ArrayList<Byte> findNode(ArrayList<Byte> aPath, GT_DataNode aGoal, HashSet<GT_DataNode> aAlreadyPassedSet) {
        for(byte i = 0; i < mConnections.length; i++){
            GT_DataNode aNode = mConnections[i].getOppositeNode(this);
            if(!aAlreadyPassedSet.contains(aNode)){
                aAlreadyPassedSet.add(aNode);
                ArrayList<Byte> lPath = copy(aPath);
                lPath.add(i);
                if(aGoal.equals(aNode)){
                    return lPath;
                }
                ArrayList<Byte> tPath = aNode.findNode(lPath,aGoal, aAlreadyPassedSet);
                if(tPath!=null)
                    return tPath;
            }
        }
        return null;
    }

    public void acceptBundle(GT_InformationBundle aBundle){
        if(mContainer!=null)
            mContainer.acceptBundle(aBundle);
    }

    public void addConnection(GT_NodeConnection connection){
        if (mConnections == null)
            mConnections = new GT_NodeConnection[0];
        ArrayList<GT_NodeConnection> tConnections = new ArrayList<>(Arrays.asList(mConnections));
        tConnections.add(connection);
        mConnections = tConnections.toArray(new GT_NodeConnection[tConnections.size()]);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    private ArrayList<Byte> copy(ArrayList<Byte> arrayList){
        ArrayList<Byte> out = new ArrayList<>();
       for(Byte b :arrayList)
           out.add(b);
        return out;
    }

    public INodeContainer getContainer(){
        return mContainer;
    }
}
