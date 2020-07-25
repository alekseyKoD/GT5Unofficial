package gregtech.loaders.postload;

import gregtech.api.enums.CachedRecipes;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Recipe.GT_Recipe_ResearchStation.GT_ResearchDescription;

import static gregtech.api.enums.GT_Values.S;

public class GT_ResearchStationRecipeLoader
        implements Runnable {
    public void run() {
        addFieldGensRecipes();
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
