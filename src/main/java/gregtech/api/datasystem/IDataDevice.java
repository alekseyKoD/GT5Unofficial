package gregtech.api.datasystem;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public interface IDataDevice {

    void onProcessAborted();

    GT_DataNode getNode();

    void onDisconnected();

    IGregTechTileEntity getBaseTile();

}
