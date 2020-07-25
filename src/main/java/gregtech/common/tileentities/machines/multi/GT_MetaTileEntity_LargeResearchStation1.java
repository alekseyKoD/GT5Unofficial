package gregtech.common.tileentities.machines.multi;

import gregtech.api.datasystem.GT_RequestBundle;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GT_RenderedTexture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GT_MetaTileEntity_LargeResearchStation1 extends GT_MetaTileEntity_LargeResearchStationBase {

    GT_RequestBundle rB = null;


    public GT_MetaTileEntity_LargeResearchStation1(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_LargeResearchStation1(String aName) {
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
        return new String[]{"Флекс Line",
                "Size: 3x(5-16)x4, variable length",
                "Bottom: Steel Machine Casing(or Maintenance or Input Hatch),",
                "Input Bus (Last Output Bus), Steel Machine Casing",
                "Middle: Reinforced Glass, Assembly Liапрврпne, Reinforced Glass",
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
        return addDataHatchToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,1,0),16)&&
                addMaintenanceToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,2,0),16)&&
                addScanningHatchToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,3,0),16)&&
                addInputToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,4,0),16)&&
                addInputToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,5,0),16)&&
                addInputToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,6,0),16)&&
                addEnergyInputToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(0,7,0),16);

    }

    @Override
    public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
       rB = new GT_RequestBundle(10,10,this);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_LargeResearchStation1(this.mName);
    }


}
