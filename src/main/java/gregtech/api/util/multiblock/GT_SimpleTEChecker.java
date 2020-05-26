package gregtech.api.util.multiblock;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.metatileentity.IConnectable;
import gregtech.api.interfaces.tileentity.IGearEnergyTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.util.GT_ItsNotMyFaultException;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;

public class GT_SimpleTEChecker implements IBlockChecker {

    protected char c;
    protected Block mBlock;
    protected int mID;
    protected int mMaxCaseCount, mMinCaseCount;
    protected int caseCount = 0;
    protected HashSet<Integer> mAllowedHatches;
    protected int mCasingID;

    @Override
    public void renderGuide(World aWorld, int aX, int aY, int aZ, boolean aBuild, IGuideRenderer aRenderer) {
        //region b
        if (aBuild) {//x, y, z, TT_Container_Casings.sBlockCasingsTT, 14, 2
            if (GregTech_API.METATILEENTITIES[mID] == null) {
                return;
            }
            int tMetaData = GregTech_API.METATILEENTITIES[mID].getTileEntityBaseType();
            if (!aWorld.setBlock(aX, aY, aZ, GregTech_API.sBlockMachines, tMetaData, 2)) {
                return;
            }
            if (aWorld.getBlock(aX, aY, aZ) != GregTech_API.sBlockMachines) {
                throw new GT_ItsNotMyFaultException("Failed to place Block even though World.setBlock returned true. It COULD be MCPC/Bukkit causing that. In case you really have that installed, don't report this Bug to me, I don't know how to fix it.");
            }
            if (aWorld.getBlockMetadata(aX, aY, aZ) != tMetaData) {
                throw new GT_ItsNotMyFaultException("Failed to set the MetaValue of the Block even though World.setBlock returned true. It COULD be MCPC/Bukkit causing that. In case you really have that installed, don't report this Bug to me, I don't know how to fix it.");
            }
            IGregTechTileEntity tTileEntity = (IGregTechTileEntity) aWorld.getTileEntity(aX, aY, aZ);
            if (tTileEntity != null) {
                tTileEntity.setInitialValuesAsNBT(new NBTTagCompound(), (short)mID);
            }
        }
        //endregion
        else {
            Minecraft.getMinecraft().effectRenderer.addEffect(new GT_GuideRendererParticle(aWorld, aX, aY, aZ, mID,aRenderer));//new BlockHint(world,x,y,z,block,meta)
        }

    }

    public GT_SimpleTEChecker(char c, int teID, int aMinCaseCount, int aMaxCaseCount, List<Integer> aAllowedHatches, int aCasingID){
        mID = teID;
        mMaxCaseCount = aMaxCaseCount;
        mMinCaseCount = aMinCaseCount;
        mAllowedHatches = new HashSet<>();
        mAllowedHatches.addAll(aAllowedHatches);
        mCasingID = aCasingID;
        this.c = c;
    }

    @Override
    public char getChar() {
        return c;
    }

    @Override
    public boolean onStructCreatedCheck() {
        return caseCount>=mMinCaseCount;
    }

    @Override
    public boolean checkBlock(IGregTechTileEntity aBaseMetaTileEntity, GT_MetaTileEntity_MultiBlockBase aController, int aXoff, int aYoff, int aZoff) {
        TileEntity te = aBaseMetaTileEntity.getTileEntityOffset(aXoff,aYoff,aZoff);
        if(te instanceof IGregTechTileEntity &&((IGregTechTileEntity)te).getMetaTileID() == mID){
            caseCount++;
            if(caseCount>mMaxCaseCount)
                return false;
            return true;
        } else if (mAllowedHatches!=null&&mAllowedHatches.size()>0) {
            int n = aController.addHatchToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(aXoff,aYoff,aZoff), mCasingID);
            return mAllowedHatches.contains(n);
        }else {
            return false;
        }
    }

    @Override
    public void reset() {
        caseCount = 0;
    }
}
