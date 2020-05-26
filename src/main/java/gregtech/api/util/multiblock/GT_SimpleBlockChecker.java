package gregtech.api.util.multiblock;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;

public class GT_SimpleBlockChecker implements IBlockChecker {
    protected char c;
    protected Block mBlock;
    protected byte mMeta;
    protected int mMaxCaseCount, mMinCaseCount;
    protected int caseCount = 0;
    protected HashSet<Integer> mAllowedHatches;
    protected int mCasingID;

    @Override
    public void renderGuide(World aWorld, int aX, int aY, int aZ, boolean aBuild, IGuideRenderer aRenderer) {
        if (aBuild) {//x, y, z, TT_Container_Casings.sBlockCasingsTT, 14, 2
            aWorld.setBlock(aX, aY, aZ, mBlock, mMeta, 2);
        } else {
            Minecraft.getMinecraft().effectRenderer.addEffect(new GT_GuideRendererParticle(aWorld, aX, aY, aZ, mBlock, mMeta,aRenderer));//new BlockHint(world,x,y,z,block,meta)
        }

    }

    public GT_SimpleBlockChecker(char c, Block aBlock, byte aMeta, int aMinCaseCount, int aMaxCaseCount, List<Integer> aAllowedHatches, int aCasingID){
        mBlock = aBlock;
        mMeta = aMeta;
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

        if(aBaseMetaTileEntity.getBlockOffset(aXoff,aYoff,aZoff)==mBlock&&aBaseMetaTileEntity.getMetaIDOffset(aXoff,aYoff,aZoff)==mMeta){
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
