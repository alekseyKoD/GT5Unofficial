package gregtech.common.tileentities.machines.basic;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.threads.GT_Runnable_MachineBlockUpdate;
import gregtech.api.util.multiblock.GT_GuideRendererParticle;
import gregtech.api.util.multiblock.IGuideRenderer;
import gregtech.common.gui.GT_Container_HolographicProjector;
import gregtech.common.gui.GT_GUIContainer_HolographicProjector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

public class GT_MetaTileEntity_HolographicProjector
        extends GT_MetaTileEntity_BasicMachine implements IGuideRenderer {

    public ArrayList<GT_GuideRendererParticle> mParticles = new ArrayList<>();

    public int param1 = 0;
    public int param2 = 0;
    public int param3 = 0;

    int mTimer = -1;


    public GT_MetaTileEntity_HolographicProjector(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 1, "Creates Holographic projection of any GregTech MultiBlock", 1, 1, "Scanner.png", "", new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_SIDE_SCANNER_ACTIVE), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_SIDE_SCANNER), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_FRONT_SCANNER_ACTIVE), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_FRONT_SCANNER), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_TOP_SCANNER_ACTIVE), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_TOP_SCANNER), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_BOTTOM_SCANNER_ACTIVE), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_BOTTOM_SCANNER)});
    }

    public GT_MetaTileEntity_HolographicProjector(String aName, int aTier, String aDescription, ITexture[][][] aTextures, String aGUIName, String aNEIName) {
        super(aName, aTier, 1, aDescription, aTextures, 0, 0, aGUIName, aNEIName);
    }

    public GT_MetaTileEntity_HolographicProjector(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures, String aGUIName, String aNEIName) {
        super(aName, aTier, 1, aDescription, aTextures, 0, 0, aGUIName, aNEIName);
    }

    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_HolographicProjector(this.mName, this.mTier, this.mDescriptionArray, this.mTextures, this.mGUIName, this.mNEIName);
    }

    public int checkRecipe() {
        mParticles.clear();
        TileEntity tTileEntity = getBaseMetaTileEntity().getTileEntityAtSide(getBaseMetaTileEntity().getFrontFacing());
        if((tTileEntity instanceof IGregTechTileEntity)&&(((IGregTechTileEntity) tTileEntity).getMetaTileEntity()instanceof GT_MetaTileEntity_MultiBlockBase) ){
            mEUt = 0;
            mMaxProgresstime = 40;
            ((GT_MetaTileEntity_MultiBlockBase)((IGregTechTileEntity)tTileEntity).getMetaTileEntity()).renderStructure(false,this, new int[]{param1,param2,param3});
            return 2;
        }
      return 0;
    }

    public String[] getParamNames() {
        TileEntity tTileEntity = getBaseMetaTileEntity().getTileEntityAtSide(getBaseMetaTileEntity().getFrontFacing());
        if((tTileEntity instanceof IGregTechTileEntity)&&(((IGregTechTileEntity) tTileEntity).getMetaTileEntity()instanceof GT_MetaTileEntity_MultiBlockBase) ){

            return ((GT_MetaTileEntity_MultiBlockBase)((IGregTechTileEntity)tTileEntity).getMetaTileEntity()).getParamNames();
        }
       return new String[]{"p1: ","p2: ","p3: "};
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_HolographicProjector(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_HolographicProjector(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public void startProcess() {
        super.startProcess();
    }

    @Override
    public void endProcess() {
      /*  super.endProcess();
        for(GT_GuideRendererParticle mParticle:mParticles){
            mParticle.setDead();
        }*/
    }

    public int getCapacity() {
        return 1000;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("param1",param1);
        aNBT.setInteger("param2",param2);
        aNBT.setInteger("param3",param3);
        super.saveNBTData(aNBT);

    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        param1 = aNBT.getInteger("param1");
        param2 = aNBT.getInteger("param2");
        param3 = aNBT.getInteger("param3");
        super.loadNBTData(aNBT);
    }

    @Override
    public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        super.onScrewdriverRightClick(aSide, aPlayer, aX, aY, aZ);
        if(aPlayer.capabilities.isCreativeMode&&aPlayer.isSneaking()){
            TileEntity tTileEntity = getBaseMetaTileEntity().getTileEntityAtSide(getBaseMetaTileEntity().getFrontFacing());
            if((tTileEntity instanceof IGregTechTileEntity)&&(((IGregTechTileEntity) tTileEntity).getMetaTileEntity()instanceof GT_MetaTileEntity_MultiBlockBase) ){
                mTimer = 150;
                GT_Runnable_MachineBlockUpdate.isProcessingAllowed = false;
                ((GT_MetaTileEntity_MultiBlockBase)((IGregTechTileEntity)tTileEntity).getMetaTileEntity()).renderStructure(true,this,new int[]{param1,param2,param3});
            }
        }

    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if(aBaseMetaTileEntity.isServerSide()){
            if(mTimer>=0)
                mTimer--;
            if(mTimer == 0)
                mParticles.clear();
        }
    }

    @Override
    public void addParticle(GT_GuideRendererParticle aParticle) {
        mParticles.add(aParticle);
    }

    @Override
    public int rechargerSlotStartIndex() {
        return 0;
    }

    @Override
    public int dechargerSlotStartIndex() {
        return 0;
    }


}