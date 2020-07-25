package gregtech.common.tileentities.machines.multi;

import gregtech.GT_Mod;
import gregtech.api.datasystem.GT_DataNode;
import gregtech.api.datasystem.GT_InformationBundle;
import gregtech.api.datasystem.IDataDevice;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Data;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Maintenance;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_DataSystemController;
import net.minecraft.item.ItemStack;

public abstract class GT_MetaTileEntity_DataWorkerBase extends GT_MetaTileEntity_MultiBlockBase implements IDataDevice {


    public GT_MetaTileEntity_Hatch_Data mDataHatch = null;
    public GT_MetaTileEntity_DataSystemController mSystemController;

    private boolean startWorking = false;

    public GT_MetaTileEntity_DataWorkerBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_DataWorkerBase(String aName) {
        super(aName);
    }

    public boolean addDataHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Data) {
            GT_MetaTileEntity_Hatch_Data aHatch = ((GT_MetaTileEntity_Hatch_Data) aMetaTileEntity);
            aHatch.multiblock = this;
            aHatch.updateTexture(aBaseCasingIndex);
            if(mDataHatch == null){
                mDataHatch = aHatch;
                aHatch.addMultiblockToSystem(this);
                return true;
            }
            else if (mDataHatch == aHatch)
                aHatch.addMultiblockToSystem(this);
                return true;
        }
        return false;
    }

    public void onBundleAccepted(GT_InformationBundle aBundle){

    }

    public void startProcessing(){
        startWorking = true;
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
                                if (!polluteEnvironment(getPollutionPerTick(mInventory[1]))) {
                                    stopMachine();
                                }
                                if (mMaxProgresstime > 0 && ++mProgresstime >= mMaxProgresstime) {
                                    if (mOutputItems != null) for (ItemStack tStack : mOutputItems)
                                        if (tStack != null) {
                                            try {
                                                GT_Mod.achievements.issueAchivementHatch(aBaseMetaTileEntity.getWorld().getPlayerEntityByName(aBaseMetaTileEntity.getOwnerName()), tStack);
                                            } catch (Exception ignored) {
                                            }
                                            addOutput(tStack);
                                        }
                                    if (mOutputFluids != null) {
                                        addFluidOutputs(mOutputFluids);
                                    }
                                    mEfficiency = Math.max(0, Math.min(mEfficiency + mEfficiencyIncrease, getMaxEfficiency(mInventory[1]) - ((getIdealStatus() - getRepairStatus()) * 1000)));
                                    mOutputItems = null;
                                    mProgresstime = 0;
                                    mMaxProgresstime = 0;
                                    mEfficiencyIncrease = 0;
                                    endProcess();
                                    if (aBaseMetaTileEntity.isAllowedToWork()) checkRecipe(mInventory[1]);
                                    if (mOutputFluids != null && mOutputFluids.length > 0) {
                                        if (mOutputFluids.length > 1) {
                                            try {
                                                GT_Mod.achievements.issueAchievement(aBaseMetaTileEntity.getWorld().getPlayerEntityByName(aBaseMetaTileEntity.getOwnerName()), "oilplant");
                                            } catch (Exception ignored) {
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (aTick % 100 == 0 || aBaseMetaTileEntity.hasWorkJustBeenEnabled() || aBaseMetaTileEntity.hasInventoryBeenModified() || startWorking) {
                                startWorking = false;
                                if (aBaseMetaTileEntity.isAllowedToWork()||true) {
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
    }

    protected void endProcess(){

    }

    @Override
    public GT_DataNode getNode() {
        if(mDataHatch == null)
            return null;
        else
            return mDataHatch.getNode();
    }

    public void onPacketStuck(){

    }

    @Override
    public void onDisconnected() {
        stopMachine();
    }

    @Override
    public IGregTechTileEntity getBaseTile() {
        return getBaseMetaTileEntity();
    }
}
