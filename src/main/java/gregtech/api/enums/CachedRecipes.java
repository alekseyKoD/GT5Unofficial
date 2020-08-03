package gregtech.api.enums;

import gregtech.api.objects.ItemData;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;

import java.util.*;

public enum CachedRecipes {
    fieldGenLV, fieldGenMV, fieldGenHV, fieldGenEV, fieldGenIV,
    Invar;

    private ArrayList<GT_Recipe> mRecipes = new ArrayList<>();

    public GT_Recipe[] get(){
        if(mRecipes.size() == 0)
            throw new IllegalAccessError("The Enum '" + name() + "' has not been set to a Recipe at this time!");
        return mRecipes.toArray(new GT_Recipe[mRecipes.size()]);
    }

    public void set(GT_Recipe... aRecipes){
        mRecipes.addAll(Arrays.asList(aRecipes));
    }

    public void set(GT_Recipe.GT_Recipe_Map map){
        mRecipes.add(map.getLastRecipe());
    }

    public void findAndSet(SearchParams... aParams){
        for(SearchParams aParam : aParams)
            findAndSet(aParam);
    }

    public void findAndSet(SearchParams aParams){
        HashSet<GT_Recipe> tResult = new HashSet<>();
        ArrayList<ItemStack> tOutputs = new ArrayList<>();
        if(aParams.mOutput == null){
            for(OrePrefixes tPrefix : aParams.mOutputPrefixes){
                ItemStack q = GT_OreDictUnificator.get(tPrefix,aParams.mOutputMaterial,1);
                if(q!=null)
                    tOutputs.add(q);
            }
        }
        else
            tOutputs.add(aParams.mOutput);
        for(GT_Recipe tRecipe : aParams.mMap.mRecipeList){
            if(tRecipe.mOutputs==null || tRecipe.mOutputs.length == 0 || tRecipe.mOutputs[0]== null)
                continue;
            boolean tOutputEquals = false;
            for(ItemStack t : tOutputs){
                if(GT_Utility.areStacksEqual(tRecipe.mOutputs[0],t)){
                    tOutputEquals = true;
                    break;
                }
            }
            if(!tOutputEquals)
                continue;

            if(aParams.mInputParams.size() == 0){
                tResult.add(tRecipe);
                continue;
            }


            boolean foundOR = false;
            for(int i = 0; i < aParams.mInputParams.size(); i+=3){
                ItemStack aInput = null;
                ArrayList<OrePrefixes> aAllowedPrefixes = null;
                Materials aMaterial = null;
                SearchMode aMode = (SearchMode) aParams.mInputParams.get(i + 2);
                Object aParameter = aParams.mInputParams.get(i);
                if(aParameter instanceof ItemStack)
                    aInput = (ItemStack) aParameter;
                else {
                    if (aParams.mInputParams.get(i) != null)
                        aAllowedPrefixes = (ArrayList<OrePrefixes>) aParams.mInputParams.get(i);
                    aMaterial = (Materials)aParams.mInputParams.get(i+1);

                }

                boolean foundAND = false;
                for(ItemStack tInput : tRecipe.mInputs){
                    if(aInput !=null) {
                        if (GT_Utility.areStacksEqual(tInput, aInput)) {
                            foundAND = true;
                            break;
                        }
                    }
                    else {
                        ItemData aData = GT_OreDictUnificator.getAssociation(tInput);
                        if(aData == null)
                            continue;
                        if(aAllowedPrefixes!=null)
                            if(!aAllowedPrefixes.contains(aData.mPrefix))
                                continue;
                        if(aData.mMaterial.mMaterial.equals(aMaterial)){
                            foundAND = true;
                            break;
                        }

                    }

                }

                if(!foundAND && aMode == SearchMode.AND)
                    break;

                if(foundAND)
                    foundOR = true;
            }
            if(!foundOR)
                continue;

            tResult.add(tRecipe);
        }
        mRecipes.addAll(tResult);
    }

    public static class SearchParams{

        GT_Recipe.GT_Recipe_Map mMap;
        Materials mOutputMaterial;
        ArrayList<OrePrefixes> mOutputPrefixes = new ArrayList<>();
        ItemStack mOutput = null;
        ArrayList<Object> mInputParams = new ArrayList<>();

        public static HashMap<GT_Recipe.GT_Recipe_Map, List<GT_Recipe>> mSortedMaps = new HashMap<>(30);

        public SearchParams(GT_Recipe.GT_Recipe_Map aMap){
            mMap = aMap;
        }

        public SearchParams setOutput(OrePrefixes aPrefix, Materials aMaterial, boolean aAllowFamiliar){
            mOutputPrefixes.add(aPrefix);
            if(aAllowFamiliar)
                mOutputPrefixes.addAll(aPrefix.mFamiliarPrefixes);
            mOutputMaterial = aMaterial;
            return this;
        }

        public SearchParams setOutput(Collection<OrePrefixes> aPrefixes, Materials aMaterial){
            mOutputPrefixes.addAll(aPrefixes);
            mOutputMaterial = aMaterial;
            return this;
        }

        public SearchParams setOutput(OrePrefixes aPrefix, Materials aMaterial){
           return setOutput(aPrefix,aMaterial,false);
        }

        public SearchParams setOutput(ItemStack aStack){
            mOutput = aStack;
            return this;
        }

        public SearchParams addInput(ItemStack aStack, SearchMode aMode){
            mInputParams.add(aStack);
            mInputParams.add(null);
            mInputParams.add(aMode);
            return this;
        }

        public SearchParams addInput(ArrayList<OrePrefixes> aAllowedPrefixes, Materials aMaterial, SearchMode aMode){
            mInputParams.add(aAllowedPrefixes);
            mInputParams.add(aMaterial);
            mInputParams.add(aMode);
            return this;
        }

        public SearchParams addInput(Materials aMaterial, SearchMode aMode){
            return addInput(null,aMaterial,aMode);
        }


    }

}
