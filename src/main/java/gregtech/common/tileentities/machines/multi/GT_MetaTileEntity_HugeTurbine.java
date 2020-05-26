package gregtech.common.tileentities.machines.multi;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.GT_MetaGenerated_Tool;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.api.util.multiblock.GT_MultiBlockUtility;
import gregtech.api.util.multiblock.GT_SimpleBlockChecker;
import gregtech.common.gui.GT_Container_HugeTurbine;
import gregtech.common.gui.GT_GUIContainer_HugeTurbine;
import gregtech.common.items.GT_MetaGenerated_Tool_01;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.ArrayList;

public abstract class GT_MetaTileEntity_HugeTurbine extends GT_MetaTileEntity_MultiBlockBase{

    protected int baseEff = 0;
    protected int optFlow = 0;
    protected double realOptFlow = 0;
    protected int storedFluid = 0;
    protected int counter = 0;


    public static String[][] mStructure = new String[][]{
            {
                    "sCCCs",
                    "sCCCs",
                    "CCCCC",
                    "CCCCC",
                    "CCCCC",
                    "sCCCs",
                    "sCCCs"},
            {
                    "CCCCC",
                    "CaaaC",
                    "CaaaC",
                    "CaaaC",
                    "CaaaC",
                    "CaaaC",
                    "CNNNC"},
            {
                    "CCDCC",
                    "CaaaC",
                    "CaaaC",
                    "CaaaC",
                    "CaaaC",
                    "CaaaC",
                    "CNcNC"},
            {
                    "CCCCC",
                    "CaaaC",
                    "CaaaC",
                    "CaaaC",
                    "CaaaC",
                    "CaaaC",
                    "CNNNC"},
            {
                    "sCCCs",
                    "sCCCs",
                    "CCCCC",
                    "CCCCC",
                    "CCCCC",
                    "sCCCs",
                    "sCCCs"}
    };


    public GT_MetaTileEntity_HugeTurbine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional,6);
    }

    public GT_MetaTileEntity_HugeTurbine(String aName) {
        super(aName,6);
        mUtility = new GT_MultiBlockUtility(true,true, mStructure,new GT_SimpleBlockChecker[]{
                new GT_SimpleBlockChecker('C',getCasingBlock(),getCasingMeta(),100, 1000, Arrays.asList(new Integer[]{1,3,7}),getCasingTextureIndex()),
                new GT_SimpleBlockChecker('N',getCasingBlock(),getCasingMeta(),8,1000,Arrays.asList(new Integer[]{}), getCasingTextureIndex()),
                new GT_SimpleBlockChecker('D',getCasingBlock(),getCasingMeta(),0,1,Arrays.asList(new Integer[]{6}), getCasingTextureIndex())

        });
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return getMaxEfficiency(aStack) > 0;
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_HugeTurbine(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public boolean onWrenchRightClick(byte aSide, byte aWrenchingSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        return checkMachine(getBaseMetaTileEntity(),getRealInventory()[1]);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return mUtility.checkStructure(getBaseMetaTileEntity(),this, this.getBaseMetaTileEntity().getBackFacing());
    }

    public abstract Block getCasingBlock();

    public abstract byte getCasingMeta();

    public abstract byte getCasingTextureIndex();

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_HugeTurbine(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public boolean onWireCutterRightClick(byte aSide, byte aWrenchingSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        getServerGUI(1,aPlayer.inventory,getBaseMetaTileEntity());
        return false;
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        for(int j = 1;j<6;j++){
            ItemStack tStack = mInventory[j];
            if(tStack==null || !(tStack.getItem() instanceof GT_MetaGenerated_Tool)  || tStack.getItemDamage() < 170 || tStack.getItemDamage() >178)return false;
            if(!GT_Utility.areStacksEqual(aStack, tStack,true)) return false;
        }
        ArrayList<FluidStack> tFluids = getStoredFluids();
        if (tFluids.size() > 0) {
            if (baseEff == 0 || optFlow == 0 || counter >= 1000 || this.getBaseMetaTileEntity().hasWorkJustBeenEnabled()
                    || this.getBaseMetaTileEntity().hasInventoryBeenModified()) {
                counter = 0;
                baseEff = (int) ((50.0F
                        + (10.0F * ((GT_MetaGenerated_Tool) aStack.getItem()).getToolCombatDamage(aStack))) * 100);
                optFlow = 5* (int) Math.max(Float.MIN_NORMAL,
                        ((GT_MetaGenerated_Tool) aStack.getItem()).getToolStats(aStack).getSpeedMultiplier()
                                * ((GT_MetaGenerated_Tool) aStack.getItem()).getPrimaryMaterial(aStack).mToolSpeed
                                * 50);
            } else {
                counter++;
            }
        }

        int newPower = fluidIntoPower(tFluids, optFlow, baseEff);  // How much the turbine should be producing with this flow
        System.out.println("opt flow "+optFlow+" baseEff "+baseEff+" new power "+newPower);
        int difference = newPower - this.mEUt; // difference between current output and new output

        // Magic numbers: can always change by at least 10 eu/t, but otherwise by at most 1 percent of the difference in power level (per tick)
        // This is how much the turbine can actually change during this tick
        int maxChangeAllowed = Math.max(10, (int) Math.ceil(Math.abs(difference) * 0.01));

        if (Math.abs(difference) > maxChangeAllowed) { // If this difference is too big, use the maximum allowed change
            int change = maxChangeAllowed * (difference > 0 ? 1 : -1); // Make the change positive or negative.
            this.mEUt += change; // Apply the change
        } else
            this.mEUt = newPower;
        System.out.println("EU/t "+mEUt);
        if (mEUt <= 0) {

//	            this.mEfficiencyIncrease = (-10);
            this.mEfficiency = 0;
            //stopMachine();
            return false;
        } else {
            this.mMaxProgresstime = 1;
            this.mEfficiencyIncrease = (10);
            if(this.mDynamoHatches.size()>0){
                if(this.mDynamoHatches.get(0).maxEUOutput() < (int)((long)mEUt * (long)mEfficiency / 10000L)){
                    explodeMultiblock();}
            }
            return true;
        }
    }

    abstract int fluidIntoPower(ArrayList<FluidStack> aFluids, int aOptFlow, int aBaseEff);

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 1;
    }

    public int getMaxEfficiency(ItemStack aStack) {
        if (GT_Utility.isStackInvalid(aStack)) {
            return 0;
        }
        if (aStack.getItem() instanceof GT_MetaGenerated_Tool_01) {
            return 10000;
        }
        return 0;
    }
    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return true;
    }

    @Override
    public String[] getInfoData() {
        String tRunning = mMaxProgresstime>0 ? "Turbine running":"Turbine stopped";
        String tMaintainance = getIdealStatus() == getRepairStatus() ? "No Maintainance issues" : "Needs Maintainance" ;
        int tDura = 0;

        if (mInventory[1] != null && mInventory[1].getItem() instanceof GT_MetaGenerated_Tool_01) {
            tDura = (int) ((100.0f / GT_MetaGenerated_Tool.getToolMaxDamage(mInventory[1]) * (GT_MetaGenerated_Tool.getToolDamage(mInventory[1]))+1));
        }

        return new String[]{
                "Huge Turbine",
                tRunning,
                "Current Output: "+mEUt+" EU/t",
                "Optimal Flow: "+(int)realOptFlow+" L/t",
                "Fuel Remaining: "+storedFluid+"L",
                "Current Speed: "+(mEfficiency/100)+"%",
                "Turbine Damage: "+tDura+"%",
                tMaintainance};
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public boolean doRandomMaintenanceDamage() {
        if (!isCorrectMachinePart(mInventory[1]) || getRepairStatus() == 0) {
            stopMachine();
            return false;
        }
        if (mRuntime++ > 1000) {
            mRuntime = 0;
            if (getBaseMetaTileEntity().getRandomNumber(6000) == 0) {
                switch (getBaseMetaTileEntity().getRandomNumber(6)) {
                    case 0:
                        mWrench = false;
                        break;
                    case 1:
                        mScrewdriver = false;
                        break;
                    case 2:
                        mSoftHammer = false;
                        break;
                    case 3:
                        mHardHammer = false;
                        break;
                    case 4:
                        mSolderingTool = false;
                        break;
                    case 5:
                        mCrowbar = false;
                        break;
                }
            }
            for(int q = 1; q <6; q++)
            if (mInventory[q] != null && getBaseMetaTileEntity().getRandomNumber(2) == 0 && !mInventory[q].getUnlocalizedName().startsWith("gt.blockmachines.basicmachine.")) {
                if (mInventory[q].getItem() instanceof GT_MetaGenerated_Tool_01) {
                    NBTTagCompound tNBT = mInventory[q].getTagCompound();
                    if (tNBT != null) {
                        NBTTagCompound tNBT2 = tNBT.getCompoundTag("GT.CraftingComponents");//tNBT2 dont use out if
                        if (!tNBT.getBoolean("mDis")) {
                            tNBT2 = new NBTTagCompound();
                            Materials tMaterial = GT_MetaGenerated_Tool.getPrimaryMaterial(mInventory[q]);
                            ItemStack tTurbine = GT_OreDictUnificator.get(OrePrefixes.turbineBlade, tMaterial, 1);
                            int i = mInventory[q].getItemDamage();
                            if (i == 170) {
                                ItemStack tStack = GT_Utility.copyAmount(1, tTurbine);
                                tNBT2.setTag("Ingredient.0", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.1", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.2", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.3", tStack.writeToNBT(new NBTTagCompound()));
                                tStack = GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.Magnalium, 1);
                                tNBT2.setTag("Ingredient.4", tStack.writeToNBT(new NBTTagCompound()));
                            } else if (i == 172) {
                                ItemStack tStack = GT_Utility.copyAmount(1, tTurbine);
                                tNBT2.setTag("Ingredient.0", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.1", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.2", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.3", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.5", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.6", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.7", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.8", tStack.writeToNBT(new NBTTagCompound()));
                                tStack = GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.Titanium, 1);
                                tNBT2.setTag("Ingredient.4", tStack.writeToNBT(new NBTTagCompound()));
                            } else if (i == 174) {
                                ItemStack tStack = GT_Utility.copyAmount(2, tTurbine);
                                tNBT2.setTag("Ingredient.0", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.1", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.2", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.3", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.5", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.6", tStack.writeToNBT(new NBTTagCompound()));
                                tStack = GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.TungstenSteel, 1);
                                tNBT2.setTag("Ingredient.4", tStack.writeToNBT(new NBTTagCompound()));
                            } else if (i == 176) {
                                ItemStack tStack = GT_Utility.copyAmount(2, tTurbine);
                                tNBT2.setTag("Ingredient.0", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.1", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.2", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.3", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.5", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.6", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.7", tStack.writeToNBT(new NBTTagCompound()));
                                tNBT2.setTag("Ingredient.8", tStack.writeToNBT(new NBTTagCompound()));
                                tStack = GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.Americium, 1);
                                tNBT2.setTag("Ingredient.4", tStack.writeToNBT(new NBTTagCompound()));
                            }
                            tNBT.setTag("GT.CraftingComponents", tNBT2);
                            tNBT.setBoolean("mDis", true);
                            mInventory[q].setTagCompound(tNBT);

                        }
                    }
                    ((GT_MetaGenerated_Tool) mInventory[q].getItem()).doDamage(mInventory[q], (long) Math.min(mEUt / this.damageFactorLow, Math.pow(mEUt, this.damageFactorHigh)));
                    if (mInventory[q].stackSize == 0) mInventory[q] = null;
                }
            }
        }
        return true;
    }


}
