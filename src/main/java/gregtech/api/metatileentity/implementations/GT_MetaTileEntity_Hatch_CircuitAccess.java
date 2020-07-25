package gregtech.api.metatileentity.implementations;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Utility;
import gregtech.common.gui.GT_Container_HatchCircuitAccess;
import gregtech.common.gui.GT_GUIContainer_HatchCircuitAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GT_MetaTileEntity_Hatch_CircuitAccess extends GT_MetaTileEntity_Hatch {


    int calcPower = 0;
    int heat = 0;
    int mEnergy = 0;

    public boolean needUpdate = false;
    public GT_MetaTileEntity_Hatch_CircuitAccess(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 16, new String[]{
                "Data Access for Multiblocks",
                "Adds " + (aTier == 4 ? 4 : 16) + " extra slots for Data Sticks"});
    }

    public GT_MetaTileEntity_Hatch_CircuitAccess(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aTier == 4 ? 4 : 16, aDescription, aTextures);
    }

    public GT_MetaTileEntity_Hatch_CircuitAccess(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aTier == 4 ? 4 : 16, aDescription, aTextures);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_DATA_ACCESS)};
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_DATA_ACCESS)};
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
    public boolean isValidSlot(int aIndex) {
        return true;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Hatch_CircuitAccess(mName, mTier, mDescriptionArray, mTextures);
    }


    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        aBaseMetaTileEntity.openGUI(aPlayer);
        needUpdate = true;
        return true;
    }

    @Override
    public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        getBaseMetaTileEntity().openGUI(aPlayer);
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        switch (mTier) {
            default:
                return new GT_Container_HatchCircuitAccess(aPlayerInventory, aBaseMetaTileEntity);
        }
    }

    public void updateStats(){
        this.calcPower = this.mEnergy = this.heat = 0;
        System.out.println("calc power "+calcPower);
        System.out.println("inventory power "+mInventory.length);
        for(int i = 0; i < 5;i++){
             int[] a = getCalculationPowerFromCircuit(mInventory[i]);
             calcPower += a[0];
            System.out.println("calc power "+calcPower);
            System.out.println("calc up "+a[0]);
             heat += a[1];
             mEnergy += a[2];
        }


    }

    @Override
    public boolean onWireCutterRightClick(byte aSide, byte aWrenchingSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        updateStats();
        return super.onWireCutterRightClick(aSide, aWrenchingSide, aPlayer, aX, aY, aZ);
    }

    public double getCalculationPower(){
        if(needUpdate)
        {
            updateStats();
            needUpdate = false;
        }
        return calcPower;
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        switch (mTier) {
            default:
                return new GT_GUIContainer_HatchCircuitAccess(aPlayerInventory, aBaseMetaTileEntity);
        }
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
    public int getInventoryStackLimit() {
        return 1;
    }

    public int[] getCalculationPowerFromCircuit(ItemStack aCircuit){ //{mCalculations, mHeatValue, mEnergyConsumption}

        if(GT_Utility.areStacksEqual(aCircuit, GT_ModHandler.getIC2Item("electronicCircuit",1L))){//red
            return new int[] {3,4,5};
        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Good.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Basic.get(1L))){//orange

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Integrated_Good.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, GT_ModHandler.getIC2Item("advancedCircuit",1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Microprocessor.get(1L))){//yellow
            System.out.println("microprocessor ");
            return new int[] {13,1,1};
        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Processor.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Computer.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Data.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Elite.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Nanoprocessor.get(1L))){ //green

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Nanocomputer.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Elitenanocomputer.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Master.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Quantumprocessor.get(1L))){//light blue

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Quantumcomputer.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Masterquantumcomputer.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Quantummainframe.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Crystalprocessor.get(1L))){//dark blue
            return new int[]{1000,1,1};
        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Crystalcomputer.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Ultimatecrystalcomputer.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Crystalmainframe.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Neuroprocessor.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Wetwarecomputer.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Wetwaresupercomputer.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Wetwaremainframe.get(1L))){

        }
        else if(GT_Utility.areStacksEqual(aCircuit, ItemList.Circuit_Ultimate.get(1L))) {

        }
       return new int[]{0,0,0};

    }
}
