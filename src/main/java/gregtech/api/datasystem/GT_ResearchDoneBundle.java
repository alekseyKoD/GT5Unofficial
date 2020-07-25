package gregtech.api.datasystem;

import gregtech.api.util.GT_Recipe;

public class GT_ResearchDoneBundle extends GT_InformationBundle {

    public GT_Recipe.GT_Recipe_ResearchStation mResearch;
    public GT_ResearchDoneBundle(GT_Recipe.GT_Recipe_ResearchStation aResearch){
        super(1);
        mResearch = aResearch;
    }
}
