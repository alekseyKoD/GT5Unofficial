package gregtech.api.interfaces.metatileentity;

import gregtech.api.datasystem.GT_DataNode;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_DataCable;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_DataSystemController;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashSet;

public interface IMetaTileEntityDataCable extends IMetaTileEntity {
    public void initConnections(byte aSide, GT_MetaTileEntity_DataSystemController aHatch, HashSet<TileEntity> aAlreadyPassedSet, ArrayList<GT_MetaPipeEntity_DataCable> aCables, GT_DataNode sLastNode);

}
