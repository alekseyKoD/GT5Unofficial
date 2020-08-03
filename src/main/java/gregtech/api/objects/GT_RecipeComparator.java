package gregtech.api.objects;

import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Recipe;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.HashMap;

public class GT_RecipeComparator implements Comparator<GT_Recipe> {

    private static HashMap<OrePrefixes, Integer> mPrefixMap = new HashMap<>();

    static {
        mPrefixMap.put(OrePrefixes.ingot, 30);
        mPrefixMap.put(OrePrefixes.ingotHot, 29);
        mPrefixMap.put(OrePrefixes.plate, 28);

        mPrefixMap.put(OrePrefixes.gearGt, 25);
        mPrefixMap.put(OrePrefixes.gearGtSmall, 24);

        mPrefixMap.put(OrePrefixes.rod, 22);
        mPrefixMap.put(OrePrefixes.bolt, 21);
        mPrefixMap.put(OrePrefixes.screw, 20);
        mPrefixMap.put(OrePrefixes.wire, 19);
        mPrefixMap.put(OrePrefixes.wireFine, 18);


        mPrefixMap.put(OrePrefixes.dust, 15);
        mPrefixMap.put(OrePrefixes.dustSmall, 14);
        mPrefixMap.put(OrePrefixes.dustTiny, 13);


        mPrefixMap.put(OrePrefixes.nugget, 1);
    }

    @Override
    public int compare(GT_Recipe o1, GT_Recipe o2) {
        //first compare outputs
            //if has itemdata => lower
        ItemData d1 = GT_OreDictUnificator.getAssociation(o1.mOutputs[0]);
        ItemData d2 = GT_OreDictUnificator.getAssociation(o2.mOutputs[0]);
        if(d1 !=null && d2 == null)
            return 100;
        if(d1 == null && d2!=null)
            return -100;
        if(d1 == null)
            return o1.compareTo(o2);

        if(d1.mMaterial!=null && d2.mMaterial == null)
            return 50;
        if(d1.mMaterial == null && d2.mMaterial != null)
            return -50;
        if(d1.mMaterial == null)
            return o1.compareTo(o2);
        //then compare materials
        if(!d1.mMaterial.mMaterial.equals(d2.mMaterial.mMaterial))
            return d2.mMaterial.mMaterial.mBlastFurnaceTemp-d1.mMaterial.mMaterial.mBlastFurnaceTemp;
        //then compare prefixes
        Integer i1 = mPrefixMap.get(d1.mPrefix);
        if(i1 == null)
            i1 = 0;
        Integer i2 = mPrefixMap.get(d2.mPrefix);
        if(i2 == null)
            i2 = 0;
        if(!i1.equals(i2))
            return i2-i1;

        //same item, return zero
        return 0;
    }
}
