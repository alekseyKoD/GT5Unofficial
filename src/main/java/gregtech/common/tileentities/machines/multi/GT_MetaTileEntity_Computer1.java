package gregtech.common.tileentities.machines.multi;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GT_RenderedTexture;
import net.minecraft.item.ItemStack;

public class GT_MetaTileEntity_Computer1 extends GT_MetaTileEntity_ComputerBase {

    public GT_MetaTileEntity_Computer1(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_Computer1(String aName) {
        super(aName);
    }

    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[16], new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)}; //todo update textures
        }
        return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[16]};
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Assembly Line",
                "Size: 3x(5-16)x4, variable length",
                "Bottom: Steel Machine Casing(or Maintenance or Input Hatch),",
                "Input Bus (Last Output Bus), Steel Machine Casing",
                "Middle: Reinforced Glass, Assembly Line, Reinforced Glass",
                "UpMiddle: Grate Machine Casing,",
                "    Assembler Machine Casing,",
                "    Grate Machine Casing (or Controller or Data Access Hatch)",
                "Top: Steel Casing(or Energy Hatch)",
                "Up to 16 repeating slices, last is Output Bus",
                "Optional 1x Data Access Hatch next to the Controller"};
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCircuitAccessHatches.clear();
       boolean b =  addCircuitAccessToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,1,0),16)&&
        addDataHatchToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,2,0),16)&&
                addDataAccessToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,3,0),16)&&
                addMaintenanceToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,4,0),16);
       super.checkMachine(aBaseMetaTileEntity,aStack);
       return b;

    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Computer1(this.mName);
    }




}
