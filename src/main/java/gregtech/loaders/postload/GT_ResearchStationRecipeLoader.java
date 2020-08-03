package gregtech.loaders.postload;

import gregtech.api.enums.*;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import gregtech.api.util.GT_Recipe.GT_Recipe_ResearchStation.GT_ResearchDescription;
import gregtech.api.enums.CachedRecipes.SearchParams;

import java.util.*;

import static gregtech.api.enums.GT_Values.S;
import static gregtech.api.util.GT_Recipe.GT_Recipe_Map.sAlloySmelterRecipes;
import static gregtech.api.util.GT_Recipe.GT_Recipe_Map.sMaceratorRecipes;
import static gregtech.api.util.GT_Recipe.GT_Recipe_Map.sMixerRecipes;

public class GT_ResearchStationRecipeLoader
        implements Runnable {

    public void run() {
        casheRecipes();
        addIngotRecipes();
        addFieldGensRecipes();
    }

    private void casheRecipes(){
        CachedRecipes.Invar.findAndSet(
                new SearchParams(sAlloySmelterRecipes).setOutput(GT_OreDictUnificator.get(OrePrefixes.ingot,Materials.Invar,1))
                .addInput(Materials.Iron, SearchMode.AND).addInput(Materials.Nickel,SearchMode.AND),
                new SearchParams(sAlloySmelterRecipes).setOutput(GT_OreDictUnificator.get(OrePrefixes.ingot,Materials.Invar,1))
                        .addInput(Materials.WroughtIron, SearchMode.AND).addInput(Materials.Nickel,SearchMode.AND),
                new SearchParams(sMixerRecipes).setOutput(OrePrefixes.dust,Materials.Invar, true));
    }

    private void addIngotRecipes(){
        GT_ResearchDescription f1 = S.newResearch(23).setEnergyStats(20,40,5,5,7)
                .setInputs(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Osmium,4),GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Copper,4), GT_OreDictUnificator.get(OrePrefixes.dustTiny,Materials.Osmium,1))
                .setDescriptions("Simple Field Generator",0, 50,50, GT_OreDictUnificator.get(OrePrefixes.ingot,Materials.Invar,1)).setTargetRecipes(CachedRecipes.Invar.get())
                .setPageText("This expensive component is used to create various machines like replicator or organic replicator").addResearch();

    }



    private void addFieldGensRecipes(){
        GT_ResearchDescription f1 = S.newResearch(3).setEnergyStats(20,40,5,5,7)
                .setInputs(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Osmium,4),GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Copper,4), GT_OreDictUnificator.get(OrePrefixes.dustTiny,Materials.Osmium,1))
                .setDescriptions("Simple Field Generator",2, 50,50, ItemList.Field_Generator_LV.get(1)).setTargetRecipes(CachedRecipes.fieldGenLV.get())
                .setPageText("This expensive component is used to create various machines like replicator or organic replicator").addResearch();

        f1 = S.newResearch(4).setEnergyStats(20,40,5,5,7)
                .setInputs(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Osmium,4),GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Copper,4), GT_OreDictUnificator.get(OrePrefixes.dustTiny,Materials.Osmium,1))
                .setDescriptions("Advanced Field Generator",2, 100,50, ItemList.Field_Generator_MV.get(1)).setTargetRecipes(CachedRecipes.fieldGenMV.get())
                .setPageText("This expensive component is used to create various machines like replicator or organic replicator").setDependencies(f1).addResearch();


        f1 = S.newResearch(5).setEnergyStats(20,40,5,5,7)
                .setInputs(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Osmium,4),GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Copper,4), GT_OreDictUnificator.get(OrePrefixes.dustTiny,Materials.Osmium,1))
                .setDescriptions("Advanced Field Generator II",2, 150,50, ItemList.Field_Generator_HV.get(1)).setTargetRecipes(CachedRecipes.fieldGenHV.get())
                .setPageText("This expensive component is used to create various machines like replicator or organic replicator").setDependencies(f1).addResearch();

        f1 = S.newResearch(6).setEnergyStats(20,40,5,5,7)
                .setInputs(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Osmium,4),GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Copper,4), GT_OreDictUnificator.get(OrePrefixes.dustTiny,Materials.Osmium,1))
                .setDescriptions("Advanced Field Generator III-IV",2, 200,50, ItemList.Field_Generator_EV.get(1)).setTargetRecipes(CachedRecipes.fieldGenEV.get(), CachedRecipes.fieldGenIV.get())
                .setPageText("This expensive component is used to create various machines like replicator or organic replicator").setDependencies(f1).addResearch();

    /*    f1 = S.newResearch(7).setEnergyStats(20,40,5,5,7)
                .setInputs(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Osmium,4),GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Copper,4), GT_OreDictUnificator.get(OrePrefixes.dustTiny,Materials.Osmium,1))
                .setDescriptions("Advanced Field Generator IV",2,new int[]{250,50}, ItemList.Field_Generator_IV.get(1)).setTargetRecipes(CachedRecipes.fieldGenIV.get())
                .setPageText("This expensive component is used to create various machines like replicator or organic replicator").setDependencies(f1).addResearch();
*/

    }
}
