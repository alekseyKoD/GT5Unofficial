package gregtech.api.datasystem;

import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_DataCable;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_DataSystemController;

import java.util.ArrayList;

public interface INodeContainer {

    void acceptBundle(GT_InformationBundle aBundle);

    GT_DataNode getNode();

    void onPacketStuck();

    void initConnections(GT_MetaTileEntity_DataSystemController aController, ArrayList<GT_MetaPipeEntity_DataCable> aCables, GT_DataNode aLastNode);

}
