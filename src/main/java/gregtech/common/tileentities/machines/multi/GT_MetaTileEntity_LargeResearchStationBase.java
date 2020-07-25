package gregtech.common.tileentities.machines.multi;

import gregtech.GT_Mod;
import gregtech.api.datasystem.GT_InformationBundle;
import gregtech.api.datasystem.GT_ResearchDoneBundle;
import gregtech.api.datasystem.IDataConsumer;
import gregtech.api.datasystem.IResearcher;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_InputBus;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Maintenance;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.util.GT_Recipe;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_DataSystemController;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

public abstract class GT_MetaTileEntity_LargeResearchStationBase extends GT_MetaTileEntity_DataWorkerBase implements IDataConsumer, IResearcher {

    int mPassedIterations = 0;
    int mTargetIterationsCount = 0;

    int mComputation = 0;
    int mStartUp;


    public GT_MetaTileEntity_Hatch_InputBus mScanningHatch = null;

    GT_Recipe.GT_Recipe_ResearchStation currentRecipe = null;
    GT_Recipe.GT_Recipe_ResearchStation prevRecipe = null;

    public GT_MetaTileEntity_LargeResearchStationBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_LargeResearchStationBase(String aName) {
        super(aName);
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "AssemblyLine.png");
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        if(currentRecipe==null)
            return false;
        if(currentRecipe!=prevRecipe){
            mPassedIterations = 0;
            mTargetIterationsCount = currentRecipe.mMinIterationsCount+getBaseMetaTileEntity().getRandomNumber(currentRecipe.mMaxIterationsCount-currentRecipe.mMinIterationsCount+1);
        }
        if (!GT_Recipe.GT_Recipe_ResearchStation.checkInputs(true, false, getStoredFluids(), getStoredInputs(), currentRecipe)) {
            return false;
        }
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = currentRecipe.mSingleResearchTime;
        mEUt = -currentRecipe.mEUt;
        mSystemController.onTempDataUpdate();
        return true;
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

    @Override
    protected void endProcess() {
        if(currentRecipe==null)
            return;
        mPassedIterations++;
        prevRecipe = currentRecipe;
        if(mPassedIterations>=mTargetIterationsCount){
            saveData(prevRecipe);
            mPassedIterations = 0;
            currentRecipe = null;

        }
        mComputation = 0;
        mSystemController.onTempDataUpdate();
    }

    public boolean addScanningHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_InputBus) {
            ((GT_MetaTileEntity_Hatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            mScanningHatch = (GT_MetaTileEntity_Hatch_InputBus) aMetaTileEntity;
            return true;
        }
        return false;
    }

    public boolean saveData(GT_Recipe.GT_Recipe_ResearchStation aResearch){
        System.out.println("Successed at "+mPassedIterations);
        mSystemController.mSystem.sendInformation(getNode(),mSystemController.mSystem.getPathToController(getNode()),new GT_ResearchDoneBundle(aResearch));
        return true;
    }



    @Override
    public int[] requestComputation() {
        if(currentRecipe == null)
            return new int[]{0};
        return new int[]{currentRecipe.mComputation*50-mComputation};
    }

    @Override
    public boolean setNextResearch(int aID) {
        if(isProcessing()||!getBaseMetaTileEntity().isAllowedToWork())
            return false;
        GT_Recipe.GT_Recipe_ResearchStation aRecipe = GT_Recipe.GT_Recipe_ResearchStation.mIDtoRecipeMap.get(aID);
        ArrayList<ItemStack> tInputs = new ArrayList<>();
        for (int i = mScanningHatch.getBaseMetaTileEntity().getSizeInventory() - 1; i >= 0; i--) {
            if (mScanningHatch.getBaseMetaTileEntity().getStackInSlot(i) != null)
                tInputs.add(mScanningHatch.getBaseMetaTileEntity().getStackInSlot(i));
        }
        if(!aRecipe.checkResearchHatches(tInputs,false))
            return false;
        currentRecipe = aRecipe;
        return true;
    }



    @Override
    public void onBundleAccepted(GT_InformationBundle aBundle) {
        if (currentRecipe!= null && aBundle.mDataFlow >= 1)
            mComputation+=aBundle.mDataFlow;
    }



    @Override
    public void stopMachine() {
        super.stopMachine();
        if(mSystemController!=null)
            mSystemController.onTempDataUpdate();

    }

    @Override
    public void onProcessAborted() {
        super.stopMachine();
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
                    for (GT_MetaTileEntity_Hatch_Maintenance tHatch : mMaintenanceHatches) {
                        if (isValidMetaTileEntity(tHatch)) {
                            if (!GT_MetaTileEntity_MultiBlockBase.disableMaintenance) {
                                if (tHatch.mAuto && (!mWrench || !mScrewdriver || !mSoftHammer || !mHardHammer || !mSolderingTool || !mCrowbar))
                                    tHatch.autoMaintainance();
                                if (tHatch.mWrench) mWrench = true;
                                if (tHatch.mScrewdriver) mScrewdriver = true;
                                if (tHatch.mSoftHammer) mSoftHammer = true;
                                if (tHatch.mHardHammer) mHardHammer = true;
                                if (tHatch.mSolderingTool) mSolderingTool = true;
                                if (tHatch.mCrowbar) mCrowbar = true;
                            } else {
                                mWrench = true;
                                mScrewdriver = true;
                                mSoftHammer = true;
                                mHardHammer = true;
                                mSolderingTool = true;
                                mCrowbar = true;
                            }

                            tHatch.mWrench = false;
                            tHatch.mScrewdriver = false;
                            tHatch.mSoftHammer = false;
                            tHatch.mHardHammer = false;
                            tHatch.mSolderingTool = false;
                            tHatch.mCrowbar = false;
                        }
                    }
                    if (getRepairStatus() > 0) {
                        if (mMaxProgresstime > 0 && doRandomMaintenanceDamage()) {
                            if (onRunningTick(mInventory[1])) {
                                if (mMaxProgresstime > 0 && ++mProgresstime >= mMaxProgresstime) {
                                    mEfficiency = Math.max(0, Math.min(mEfficiency + mEfficiencyIncrease, getMaxEfficiency(mInventory[1]) - ((getIdealStatus() - getRepairStatus()) * 1000)));
                                    mOutputItems = null;
                                    mProgresstime = 0;
                                    mMaxProgresstime = 0;
                                    mEfficiencyIncrease = 0;
                                    endProcess();
                                    if (aBaseMetaTileEntity.isAllowedToWork())
                                        checkRecipe(mInventory[1]);

                                }
                            }
                        } else {
                            if (currentRecipe!=null) {
                                if (aBaseMetaTileEntity.isAllowedToWork()) {
                                    checkRecipe(mInventory[1]);
                                }
                                if (mMaxProgresstime <= 0) mEfficiency = Math.max(0, mEfficiency - 1000);
                            }
                        }
                    } else {
                        stopMachine();
                    }
                } else {
                    stopMachine();
                }
            }
            aBaseMetaTileEntity.setErrorDisplayID((aBaseMetaTileEntity.getErrorDisplayID() & ~127) | (mWrench ? 0 : 1) | (mScrewdriver ? 0 : 2) | (mSoftHammer ? 0 : 4) | (mHardHammer ? 0 : 8) | (mSolderingTool ? 0 : 16) | (mCrowbar ? 0 : 32) | (mMachine ? 0 : 64));
            aBaseMetaTileEntity.setActive(mMaxProgresstime > 0);
        }
        if(getBaseMetaTileEntity().isClientSide())
            return;
        if((mComputation<0 && getBaseMetaTileEntity().isActive()))
            stopMachine();
        if(mComputation>= 0 && getBaseMetaTileEntity().isActive() && currentRecipe != null)
            mComputation -= currentRecipe.mComputation;


    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        mStartUp = GT_MetaTileEntity_DataSystemController.mSystemLoadingTime+15;
    }

    @Override
    public Object getProcessing() {
        return currentRecipe == null ? -1:currentRecipe.mID;
    }

    @Override
    public boolean isProcessing() {
        return getBaseMetaTileEntity().isActive();
    }


    @Override
    public int getProgress() {
        int a = mMaxProgresstime>0?128:0;
        return (int)((double)mPassedIterations/(double)mTargetIterationsCount*100f)|a;
    }


    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if(currentRecipe!=null){
            aNBT.setInteger("rID",currentRecipe.mID);
            aNBT.setInteger("pIter",mPassedIterations);
            aNBT.setInteger("tIter", mTargetIterationsCount);
            aNBT.setInteger("calc", mComputation);
        }
        else{
            aNBT.setInteger("rID", -1);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if((currentRecipe = GT_Recipe.GT_Recipe_ResearchStation.mIDtoRecipeMap.get(aNBT.getInteger("rID")))!=null){
            mPassedIterations = aNBT.getInteger("pIter");
            mTargetIterationsCount = aNBT.getInteger("tIter");
            mComputation = aNBT.getInteger("calc")+70*currentRecipe.mComputation;
        }
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public String[] getInfoData() {
        return new String[]{"Progress:", (mProgresstime / 20) + "secs", (mMaxProgresstime / 20) + "secs", "Passed iterations", ""+mPassedIterations,"Efficiency:", (mEfficiency / 100.0F) + "%", "Problems:", String.valueOf((getIdealStatus() - getRepairStatus()))};
    }
}
