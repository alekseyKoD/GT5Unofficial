package gregtech.common.tileentities.machines.multi;

import gregtech.api.datasystem.GT_CalculationBundle;
import gregtech.api.datasystem.IDataHandler;
import gregtech.api.datasystem.IDataProducer;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_CircuitAccess;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_DataAccess;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_DataSystemController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;


public abstract class GT_MetaTileEntity_ComputerBase extends GT_MetaTileEntity_DataWorkerBase implements IDataProducer, IDataHandler<Integer> {


    int mStartUp = GT_MetaTileEntity_DataSystemController.mSystemLoadingTime+5;

    int mCalculationPower = 0;

    int mProduceCalcs = 0;

    public ArrayList<GT_MetaTileEntity_Hatch_CircuitAccess> mCircuitAccessHatches = new ArrayList<GT_MetaTileEntity_Hatch_CircuitAccess>();
    public ArrayList<GT_MetaTileEntity_Hatch_DataAccess> mDataAccessHatches = new ArrayList<>();

    private HashSet<Integer> cashedData;


    public GT_MetaTileEntity_ComputerBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_ComputerBase(String aName) {
        super(aName);
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "AssemblyLine.png");
    }

    public boolean addCircuitAccessToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_CircuitAccess) {
            ((GT_MetaTileEntity_Hatch_CircuitAccess) aMetaTileEntity).updateStats();
            if(mCircuitAccessHatches.contains(aMetaTileEntity))
                return true;
            ((GT_MetaTileEntity_Hatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
             mCircuitAccessHatches.add((GT_MetaTileEntity_Hatch_CircuitAccess) aMetaTileEntity);
            return true;
        }
        return false;
    }

    @Override
    public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        super.onScrewdriverRightClick(aSide, aPlayer, aX, aY, aZ);
        checkMachine(getBaseMetaTileEntity(),getStackInSlot(0));
    }

    public boolean addDataAccessToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex){
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_DataAccess) {
            ((GT_MetaTileEntity_Hatch_DataAccess) aMetaTileEntity).isComputerPart = true;
            if(mDataAccessHatches.contains(aMetaTileEntity))
                return true;
            ((GT_MetaTileEntity_Hatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            ((GT_MetaTileEntity_Hatch_DataAccess) aMetaTileEntity).mComputer = this;
            mDataAccessHatches.add((GT_MetaTileEntity_Hatch_DataAccess) aMetaTileEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCalculationPower = getCalculationPower();
        return false;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (mEfficiency < 0) mEfficiency = 0;
            if (--mUpdate == 0 || --mStartUpCheck == 0) {
                mInputHatches.clear();
                mInputBusses.clear();
                mOutputHatches.clear();
                mOutputBusses.clear();
                mDynamoHatches.clear();
                mEnergyHatches.clear();
                mMufflerHatches.clear();
                mMaintenanceHatches.clear();
                mMachine = checkMachine(aBaseMetaTileEntity, mInventory[1]);
            }
            if (mStartUpCheck < 0) {
                if (mMachine) {
                    mWrench = true;
                    mScrewdriver = true;
                    mSoftHammer = true;
                    mHardHammer = true;
                    mSolderingTool = true;
                    mCrowbar = true;
                    aBaseMetaTileEntity.setActive(mProduceCalcs > 0);
                    if (mProduceCalcs > 0) {
                        setEnergyConsumption();
                        if (onRunningTick(mInventory[1])) {

                            if(mSystemController!=null){
                                mSystemController.mSystem.sendInformation(getNode(),mSystemController.mSystem.getPathToController(getNode()),new GT_CalculationBundle(0,mProduceCalcs));

                            }
                            mProduceCalcs = 0;
                        }
                    }
                } else {
                    stopMachine();
                }
            }

        }

    }

    @Override
    public int[] setProducingPower(int[] aCalculations) {
        mProduceCalcs = Math.min(aCalculations[0],mCalculationPower);
        aCalculations[0] -= mProduceCalcs;
        return aCalculations;
    }

    public void setEnergyConsumption(){

    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
       return super.onRunningTick(aStack);
    }


    public int getCalculationPower(){
        int power = 0;
        for(GT_MetaTileEntity_Hatch_CircuitAccess aHatch: mCircuitAccessHatches){
            power+=aHatch.getCalculationPower();
        }
        return power;
    }



    @Override
    public void onServerStart() {
        mDataHatch = null;
        super.onServerStart();
    }

    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    public int getPollutionPerTick(ItemStack aStack) {
        return 0;
    }

    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

    public boolean saveRecipeData(Integer aTag){
        for(GT_MetaTileEntity_Hatch_DataAccess aHatch: mDataAccessHatches){
           if(aHatch.getFreeSpace()>0){
               aHatch.saveRecipeData(aTag);
               return true;
           }

        }
        return false;
    }

    @Override
    public void addAllDataToHashSet(HashSet<Integer> set) {
        for(GT_MetaTileEntity_Hatch_DataAccess aHatch: mDataAccessHatches){
            aHatch.addAllToHashSet(set);
        }
        cashedData = set;
    }


    public int getFreeSpace(){
        int tFreeSpace=0;
        for(GT_MetaTileEntity_Hatch_DataAccess aHatch: mDataAccessHatches){
            tFreeSpace+= aHatch.getFreeSpace();
        }
        return tFreeSpace;
    }

    @Override
    public void stopMachine() {

    }

    @Override
    public void onProcessAborted() {
        super.stopMachine();
    }

    @Override
    public void onPacketStuck() {
        stopMachine();
    }

    @Override
    public HashSet<Integer> getStoredData(int selector) {
        if(selector != 1)
            return null;
        HashSet<Integer> tOut = new HashSet<>();
        for(GT_MetaTileEntity_Hatch_DataAccess aHatch: mDataAccessHatches){
            aHatch.addAllToHashSet(tOut);
        }
        return tOut;
    }

    @Override
    public boolean saveData(Integer data) {
        saveRecipeData(data);
        return true;
    }


    @Override
    public boolean canStore(Integer item) {
        return getFreeSpace()>0;
    }

    public void onDataContainersUpdated(){
        HashSet<Integer> tOut = new HashSet<>();
        for(GT_MetaTileEntity_Hatch_DataAccess aHatch: mDataAccessHatches){
            aHatch.addAllToHashSet(tOut);
        }

        mSystemController.onDataUnpdated();

    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public String[] getInfoData() {
        return new String[]{"Max computation flow:", ""+mCalculationPower, "Produces computation: ", ""+mProduceCalcs};
    }
}