package gregtech.api.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

import static gregtech.api.enums.GT_Values.RA;

public class GT_ResearchCreator {
    int mID = -1;
    int mEu = 0,mTime = 0,mComp = 0,mMinIt = 0,mMaxIt = 0;
    ItemStack[] mScans = null, mInputs = null;
    FluidStack[] mFInputs = null;
    int mPage = 0;
    int[] mCoods = null;
    String mName = "";
    ItemStack mDisplay = null;
    String[] mDescription = null;
    GT_Recipe[] mTargetRecipes = null;
    GT_Recipe.GT_Recipe_ResearchStation.GT_ResearchDescription[] mDependencies = null;

    public GT_ResearchCreator newResearch(int aID){
        return new GT_ResearchCreator(aID);
    }

    public GT_ResearchCreator(){
        //fake instance to short the name
    }

    protected GT_ResearchCreator(int aID){
        mID = aID;
    }

    public GT_ResearchCreator setEnergyStats(int aEU, int aDuration, int aComputation, int aMinIterationsCount, int aMaxIterationsCount){
        mEu = aEU;
        mTime = aDuration;
        mComp = aComputation;
        mMinIt = aMinIterationsCount;
        mMaxIt = aMaxIterationsCount;
        return this;
    }

    public GT_ResearchCreator setInputs(ItemStack[] aScanningInputs, ItemStack[] aInputsPerIteration, FluidStack[] aFluidInputsPerIteration){
        mScans = aScanningInputs;
        mInputs = aInputsPerIteration;
        mFInputs = aFluidInputsPerIteration;
        return this;
    }

    public GT_ResearchCreator setInputs(ItemStack aScanningInput, ItemStack... aInputsPerIteration){
        return setInputs(new ItemStack[]{aScanningInput},aInputsPerIteration,null);
    }

    public GT_ResearchCreator setDescriptions(String aName, int aPage, int[] aCoords, ItemStack aDisplayStack){
        mName = aName;
        mPage = aPage;
        mCoods = aCoords;
        mDisplay = aDisplayStack;
        return this;
    }

    public GT_ResearchCreator setDescriptions(String aName, int aPage, int aXCoord, int aYCoord, ItemStack aDisplayStack){
        return setDescriptions(aName,aPage,new int[]{aXCoord,aYCoord},aDisplayStack);
    }

    public GT_ResearchCreator setPageText(String aText){
        ArrayList<String> desc = new ArrayList<>();
        String[] ar = aText.split(" ");
        for(int i = 0; i < ar.length; i++){
            String s = ar[i];
            while ((i+1)<ar.length&&s.length()+ar[i+1].length()<27){
                i++;
                s+=" "+ar[i];
            }
            desc.add(s);

        }
        mDescription = desc.toArray(new String[desc.size()]);
        return this;
    }

    public GT_ResearchCreator setTargetRecipes(GT_Recipe... aRecipes){
        mTargetRecipes = aRecipes;
        return this;
    }

    public GT_ResearchCreator setDependencies(GT_Recipe.GT_Recipe_ResearchStation.GT_ResearchDescription... aDependencies){
        mDependencies = aDependencies;
        return this;
    }

    public GT_Recipe.GT_Recipe_ResearchStation.GT_ResearchDescription addResearch(){
        return RA.addElectricResearchStationRecipe(mID,mScans,mTime,mInputs,mFInputs,mComp,mEu,mTargetRecipes,mMinIt,mMaxIt,mPage,mDisplay,mName,mDescription,mCoods,mDependencies);
    }
}
