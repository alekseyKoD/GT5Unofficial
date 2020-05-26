package gregtech.api.util.multiblock;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import net.minecraft.world.World;

public interface IBlockChecker {

    boolean checkBlock(IGregTechTileEntity aBaseTileEntity, GT_MetaTileEntity_MultiBlockBase aController, int aXoff, int aYoff, int aZoff);

    char getChar();

    boolean onStructCreatedCheck();

    void reset();

    void renderGuide(World aWorld, int aX, int aY, int aZ, boolean aBluid, IGuideRenderer aRenderer);

}
