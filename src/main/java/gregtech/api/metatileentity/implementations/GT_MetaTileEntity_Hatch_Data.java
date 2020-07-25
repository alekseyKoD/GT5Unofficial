package gregtech.api.metatileentity.implementations;

import gregtech.api.datasystem.*;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IDataConnected;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_DataSystemController;
import gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_DataWorkerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class GT_MetaTileEntity_Hatch_Data extends GT_MetaTileEntity_Hatch implements IDataConnected, INodeContainer {

    public GT_MetaTileEntity_DataWorkerBase multiblock = null;
    public GT_DataNode mNode;
    public GT_MetaTileEntity_DataSystemController mController;

    public GT_MetaTileEntity_Hatch_Data(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 0, new String[]{"Calculation Injector for Multiblocks"});
    }

    public GT_MetaTileEntity_Hatch_Data(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
    }

    public GT_MetaTileEntity_Hatch_Data(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI[mTier]};
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI[mTier]};
    }

    @Override
    public boolean isSimpleMachine() {
        return true;
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return true;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public boolean isEnetInput() {
        return true;
    }

    @Override
    public boolean isInputFacing(byte aSide) {
        return aSide == getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return false;
    }

    @Override
    public long getMinimumStoredEU() {
        return 0;
    }

    @Override
    public long maxEUInput() {
        return 0;
    }

    @Override
    public long maxEUStore() {
        return 0;
    }

    @Override
    public long maxAmperesIn() {
        return 0;
    }

    @Override
    public boolean transfersDataAt(byte aSide) {
        //add machine to commutator
        return isInputFacing(aSide)||isOutputFacing(aSide);
    }

    @Override
    public void initConnections(GT_MetaTileEntity_DataSystemController aController, ArrayList<GT_MetaPipeEntity_DataCable> aCables, GT_DataNode aLastNode){
        mNode = new GT_DataNode(this);
        mController = aController;
        aController.mSystem.addConnection(new GT_NodeConnection(aCables, mNode,aLastNode));
        if(multiblock!=null)
            addMultiblockToSystem(multiblock);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Hatch_Data(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        super.onScrewdriverRightClick(aSide, aPlayer, aX, aY, aZ);
        try {
            mController.mSystem.sendInformation(mNode, mController.mSystem.getPathToController(mNode), new GT_InformationBundle(100));
        }catch (Exception e){
            int a = 0;
        }
    }

    @Override
    public void acceptBundle(GT_InformationBundle aBundle) {
        if(multiblock!=null)
            multiblock.onBundleAccepted(aBundle);
    }

    public void addMultiblockToSystem(GT_MetaTileEntity_DataWorkerBase aMutiblock){
        if(mController==null)
            return;
        aMutiblock.mSystemController = mController;
        if (aMutiblock instanceof IDataConsumer){
            mController.addConsumer((IDataConsumer)aMutiblock);
        }
        if (aMutiblock instanceof IResearcher){
            mController.addWorker((IResearcher)aMutiblock);
        }
        if (aMutiblock instanceof IDataProducer){
            mController.addProducer((IDataProducer)aMutiblock);
        }
        if(aMutiblock instanceof IDataHandler){
            mController.addHandler((IDataHandler)aMutiblock);
        }
    }

    @Override
    public GT_DataNode getNode() {
        return mNode;
    }

    @Override
    public void onPacketStuck() {
        if(multiblock!=null)
            multiblock.onPacketStuck();
    }
}
