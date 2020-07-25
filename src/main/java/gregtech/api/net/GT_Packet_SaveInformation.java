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
public class GT_Packet_SaveInformation extends GT_Packet {
    private int mX, mZ, mDim;
    private short mY;
    private int mResearchID, mStackID;

    public GT_Packet_SaveInformation() {
        super(true);
    }

    public GT_Packet_SaveInformation(int aDim,int aX, short aY, int aZ, int aResearchID, int aStackID) {
        super(false);
        mDim = aDim;
        mX = aX;
        mY = aY;
        mZ = aZ;
       mResearchID = aResearchID;
       mStackID = aStackID;
    }

    @Override
    public byte[] encode() {
        ByteArrayDataOutput tOut = ByteStreams.newDataOutput(34);
        tOut.writeInt(mDim);
        tOut.writeInt(mX);
        tOut.writeShort(mY);
        tOut.writeInt(mZ);
        tOut.writeInt(mResearchID);
        tOut.writeInt(mStackID);
        return tOut.toByteArray();
    }

    @Override
    public GT_Packet decode(ByteArrayDataInput aData) {
        return new GT_Packet_SaveInformation(aData.readInt(),aData.readInt(), aData.readShort(),aData.readInt(),aData.readInt(),aData.readInt());
    }

    @Override
    public void process(IBlockAccess aWorld) {
        World w = DimensionManager.getWorld(mDim);
        if (w != null) {
            TileEntity tTileEntity = w.getTileEntity(mX, mY, mZ);
            if(tTileEntity instanceof IGregTechTileEntity && ((IGregTechTileEntity)tTileEntity).getMetaTileEntity() instanceof GT_MetaTileEntity_ComputerTerminal){
                ((GT_MetaTileEntity_ComputerTerminal) ((IGregTechTileEntity) tTileEntity).getMetaTileEntity()).saveRecipe(mResearchID,mStackID);
            }
        }
    }

    @Override
    public byte getPacketID() {
        return 10;
    }
}