package gregtech.api.datasystem;

import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_DataSystemController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class GT_DataSystem {

    HashMap<GT_DataNode,byte[]> savedPathsToController = new HashMap<>();
    HashMap<GT_DataNode,byte[]> savedPathsFromController = new HashMap<>();

    ArrayList<GT_NodeConnection> mConnections = new ArrayList<>(100);

    GT_MetaTileEntity_DataSystemController mController;

    public GT_DataSystem(GT_MetaTileEntity_DataSystemController aController){
        mController = aController;
    }


    public byte[] getPathToController(GT_DataNode from){
        byte[] path = savedPathsToController.get(from);
        if(path == null){
            path = findPath(from, true);
            savedPathsToController.put(from, path);
            return path;
        }
        else
            return path;
    }

    public byte[] getPathFromController(GT_DataNode to){
        byte[] path = savedPathsFromController.get(to);
        if(path == null){
            path = findPath(to, false);
            savedPathsFromController.put(to, path);
            return path;
        }
        else
            return path;
    }

    protected byte[] findPath(GT_DataNode node, boolean directionTo){
        ArrayList<Byte> tPath;
        if (directionTo) {
            tPath = node.findNode(new ArrayList<Byte>(), mController.getNode(), new HashSet<GT_DataNode>(Arrays.asList(node)));
        }
        else {
            tPath = mController.getNode().findNode(new ArrayList<Byte>(), node, new HashSet<GT_DataNode>(Arrays.asList(mController.getNode())));
        }
        if(tPath!=null)
            return convert(tPath);
        return null;
    }

    protected byte[] convert(ArrayList<Byte> aPath){
        byte[] tPath = new byte[aPath.size()];
        for(int i = 0; i < aPath.size(); i++){
            tPath[i] = aPath.get(i);
        }
        return tPath;
    }

    public void sendInformation(GT_DataNode from, byte[] aPath, GT_InformationBundle aBundle){
        try {
            GT_DataNode tCurrentNode = from;
            for (byte b : aPath) {
                GT_NodeConnection tConnection = tCurrentNode.mConnections[b];
                if (tConnection.maxDataFlow < aBundle.mDataFlow + tConnection.currentFlow)
                    return;
                tConnection.currentFlow += aBundle.mDataFlow;
                tCurrentNode = tConnection.getOppositeNode(tCurrentNode);
            }
            tCurrentNode.mContainer.acceptBundle(aBundle);
        }catch (Exception e){
            if(from.mContainer!= null){
                from.mContainer.onPacketStuck();
            }
        }
    }

    public void sendAutomatedBundle(GT_DataNode from, GT_InformationBundle aBundle, GT_DataNode goal){
        aBundle.automate(goal);
        sendInformation(from, getPathToController(from),aBundle);
    }

    public void onUpdate(){
        for(GT_NodeConnection tConnection:mConnections){
            tConnection.onTick();
        }
    }

    public void resetMaps(){
        savedPathsToController.clear();
        savedPathsFromController.clear();
        mConnections.clear();
    }

    public void addConnection(GT_NodeConnection aConnection){
        mConnections.add(aConnection);
    }

}
