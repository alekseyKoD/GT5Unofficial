package gregtech.api.net;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_ComputerTerminal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * Used to transfer Block Events in a much better fashion
 */
public class GT_Packet_StartResearch extends GT_Packet {
    private int mX, mZ, mDim;
    private short mY;
    private int mID, mValue;

    public GT_Packet_StartResearch() {
        super(true);
    }

    public GT_Packet_StartResearch(int aDim, int aX, short aY, int aZ, int aID, int aValue) {
        super(false);
        mDim = aDim;
        mX = aX;
        mY = aY;
        mZ = aZ;
        mID = aID;
        mValue = aValue;
    }

    @Override
    public byte[] encode() {
        ByteArrayDataOutput tOut = ByteStreams.newDataOutput(30);
        tOut.writeInt(mDim);
        tOut.writeInt(mX);
        tOut.writeShort(mY);
        tOut.writeInt(mZ);
        tOut.writeInt(mID);
        tOut.writeInt(mValue);
        return tOut.toByteArray();
    }

    @Override
    public GT_Packet decode(ByteArrayDataInput aData) {
        return new GT_Packet_StartResearch(aData.readInt(),aData.readInt(), aData.readShort(), aData.readInt(), aData.readInt(), aData.readInt());
    }

    @Override
    public void process(IBlockAccess aWorld) {
        World w = DimensionManager.getWorld(mDim);
        if (w != null) {
            TileEntity tTileEntity = w.getTileEntity(mX, mY, mZ);
            if(tTileEntity instanceof IGregTechTileEntity && ((IGregTechTileEntity)tTileEntity).getMetaTileEntity() instanceof GT_MetaTileEntity_ComputerTerminal){
                ((GT_MetaTileEntity_ComputerTerminal) ((IGregTechTileEntity) tTileEntity).getMetaTileEntity()).startResearch(mValue);
            }
        }
    }

    @Override
    public byte getPacketID() {
        return 9;
    }
}