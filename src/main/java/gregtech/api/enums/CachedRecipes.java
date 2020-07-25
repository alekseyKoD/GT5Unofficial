package gregtech.api.enums;

import gregtech.api.util.GT_Recipe;

public enum CachedRecipes {
    fieldGenLV, fieldGenMV, fieldGenHV, fieldGenEV, fieldGenIV;

    private GT_Recipe mRecipe;

    public GT_Recipe get(){
        if(mRecipe==null)
            throw new IllegalAccessError("The Enum '" + name() + "' has not been set to a Recipe at this time!");
        return mRecipe;
    }

    public void set(GT_Recipe aRecipe){
        mRecipe = aRecipe;
    }

    public void set(GT_Recipe.GT_Recipe_Map map){
        mRecipe = map.getLastRecipe();
    }
}
