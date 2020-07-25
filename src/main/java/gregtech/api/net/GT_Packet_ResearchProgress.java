package gregtech.api.net;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_ComputerTerminal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

/**
 * Used to transfer Block Events in a much better fashion
 */
public class GT_Packet_ResearchProgress extends GT_Packet {
    private int mX, mZ;
    private short mY;

    int mID, mProgress;

    public GT_Packet_ResearchProgress() {
        super(true);
    }

    public GT_Packet_ResearchProgress(int aX, short aY, int aZ, int aID, int aProgress) {
        super(false);
        mX = aX;
        mY = aY;
        mZ = aZ;
        mID = aID;
        mProgress = aProgress;
    }


    @Override
    public byte[] encode() {
        ByteArrayDataOutput tOut = ByteStreams.newDataOutput(34);
        tOut.writeInt(mX);
        tOut.writeShort(mY);
        tOut.writeInt(mZ);
        tOut.writeInt(mID);
        tOut.writeInt(mProgress);
        return tOut.toByteArray();
    }

    @Override
    public GT_Packet decode(ByteArrayDataInput aData) {
        return new GT_Packet_ResearchProgress(aData.readInt(), aData.readShort(),aData.readInt(),aData.readInt(),aData.readInt());
    }

    @Override
    public void process(IBlockAccess aWorld) {
        if (aWorld != null) {
            TileEntity tTileEntity = aWorld.getTileEntity(mX, mY, mZ);
            if(tTileEntity instanceof IGregTechTileEntity && ((IGregTechTileEntity)tTileEntity).getMetaTileEntity() instanceof GT_MetaTileEntity_ComputerTerminal){
                ((GT_MetaTileEntity_ComputerTerminal)(((IGregTechTileEntity)tTileEntity).getMetaTileEntity())).acceptServerInformationSingleProcessing(mID,mProgress);
            }
        }
    }

    @Override
    public byte getPacketID() {
        return 11;
    }
}