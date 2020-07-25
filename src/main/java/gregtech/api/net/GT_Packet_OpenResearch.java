package gregtech.api.net;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_ComputerTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * Used to transfer Block Events in a much better fashion
 */
public class GT_Packet_OpenResearch extends GT_Packet {
   private int mDim, mPlayer, mResearch, mX,mY,mZ;

    public GT_Packet_OpenResearch() {
        super(true);
    }

    public GT_Packet_OpenResearch(int aDimID, int aPlayerID, int aResearchID, int aX, int aY, int aZ) {
        super(false);
       mDim = aDimID;
       mPlayer = aPlayerID;
       mResearch = aResearchID;
       mX = aX;
       mY = aY;
       mZ = aZ;
    }

    public GT_Packet_OpenResearch(EntityPlayer aPlayer, int aResearchID, int aX, int aY, int aZ){
        super(false);
        this.mDim = aPlayer.worldObj.provider.dimensionId;
        this.mPlayer =aPlayer.getEntityId();
        mResearch = aResearchID;
        mX = aX;
        mY = aY;
        mZ = aZ;
    }

    @Override
    public byte[] encode() {
        ByteArrayDataOutput tOut = ByteStreams.newDataOutput(30);
       tOut.writeInt(mDim);
       tOut.writeInt(mPlayer);
       tOut.writeInt(mResearch);
        tOut.writeInt(mX);
        tOut.writeInt(mY);
        tOut.writeInt(mZ);
        return tOut.toByteArray();
    }

    @Override
    public GT_Packet decode(ByteArrayDataInput aData) {
        return new GT_Packet_OpenResearch(aData.readInt(), aData.readInt(), aData.readInt(),aData.readInt(),aData.readInt(),aData.readInt());
    }

    @Override
    public void process(IBlockAccess aWorld) {
        World w = DimensionManager.getWorld(mDim);
        EntityPlayer aPlayer;
        if (w != null &&  w.getEntityByID(mPlayer) instanceof EntityPlayer) {
            aPlayer =  (EntityPlayer)w.getEntityByID(mPlayer);
            TileEntity tTile = aWorld.getTileEntity(mX,mY,mZ);
            if(tTile instanceof IGregTechTileEntity){
                if( !(((IGregTechTileEntity)tTile).getMetaTileEntity()instanceof GT_MetaTileEntity_ComputerTerminal))
                    return;
                ((GT_MetaTileEntity_ComputerTerminal)((IGregTechTileEntity)tTile).getMetaTileEntity()).mRecipeID = mResearch;
                ((GT_MetaTileEntity_ComputerTerminal)((IGregTechTileEntity)tTile).getMetaTileEntity()).mSetNewID = true;
                ((IGregTechTileEntity)tTile).openGUI(aPlayer);
            }
        }
    }

    @Override
    public byte getPacketID() {
        return 7;
    }
}