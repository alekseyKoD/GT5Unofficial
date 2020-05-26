package gregtech.api.util.multiblock;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import net.minecraft.block.Block;

import java.util.HashMap;

public class GT_MultiBlockUtility {

    public char[][][] pattern;
    public byte[] contreollerCoords = new byte[3];
    private HashMap<Character, IBlockChecker> casingMap = new HashMap<>();
    boolean XYSymmetrical;
    boolean ZYSymmetrical;
    int[] casingCouter = null;
    byte mFlipped = 1;
    byte[] xzcoefs = new byte[]{0, 0};

    public GT_MultiBlockUtility(boolean aXYSymmetrical, boolean aZYSymmetrical, String[][] aPattern, IBlockChecker[] aMachineCasingMap) {
        XYSymmetrical = aXYSymmetrical;
        ZYSymmetrical = aZYSymmetrical;
        this.pattern = new char[aPattern.length][aPattern[0].length][aPattern[0][0].length()];
        for (byte i = 0; i < aPattern.length; i++) {
            for (byte j = 0; j < aPattern[0].length; j++) {
                this.pattern[i][j] = aPattern[i][j].toCharArray();
            }
        }
        for (IBlockChecker checker : aMachineCasingMap)
            casingMap.put(checker.getChar(), checker);
    }

    public void replacePattern(String[][] aPattern){ //useful for changing structure types
        this.pattern = new char[aPattern.length][aPattern[0].length][aPattern[0][0].length()];
        for (byte i = 0; i < aPattern.length; i++) {
            for (byte j = 0; j < aPattern[0].length; j++) {
                this.pattern[i][j] = aPattern[i][j].toCharArray();
            }
        }
    }

    public boolean checkStructure(IGregTechTileEntity aBaseMetaTileEntity, GT_MetaTileEntity_MultiBlockBase aController, byte controllerBackFacing) {
        boolean aResult = false;
        if (xzcoefs[0] != 0)
            aResult = checkStructure(aBaseMetaTileEntity, aController, controllerBackFacing, xzcoefs[0], xzcoefs[1]);
        if (aResult)
            return true;
        aResult = checkStructure(aBaseMetaTileEntity, aController, controllerBackFacing, (byte) 1, (byte) 1);
        xzcoefs = new byte[]{1, 1};
        if (!XYSymmetrical && !aResult) {
            aResult = checkStructure(aBaseMetaTileEntity, aController, controllerBackFacing, (byte) -1, (byte) 1);
            xzcoefs = new byte[]{-1, 1};
        }
        if (!ZYSymmetrical && !aResult) {
            aResult = checkStructure(aBaseMetaTileEntity, aController, controllerBackFacing, (byte) 1, (byte) -1);
            xzcoefs = new byte[]{1, -1};
        }
        if (!XYSymmetrical && !ZYSymmetrical && !aResult) {
            aResult = checkStructure(aBaseMetaTileEntity, aController, controllerBackFacing, (byte) -1, (byte) -1);
            xzcoefs = new byte[]{-1, -1};
        }
        return aResult;
    }

    private boolean checkStructure(IGregTechTileEntity aBaseMetaTileEntity, GT_MetaTileEntity_MultiBlockBase aController, byte controllerBackFacing, byte xCoef, byte zCoef) {
        for (IBlockChecker c : casingMap.values())
            c.reset();
        byte tFacing = controllerBackFacing;
        if (!patternProcessing())
            return false;
        IBlockChecker checker = null;
        Block currentBlock = GregTech_API.sBlockCasings1;
        byte currentCasingMeta = -1;
        int currentCasingIndex = -1;
        byte[] allowedHatches = null;
        int currentCasingNumber = 0;
        aController.mInputHatches.clear();
        aController.mOutputHatches.clear();
        aController.mInputBusses.clear();
        aController.mOutputBusses.clear();
        aController.mEnergyHatches.clear();
        aController.mDynamoHatches.clear();
        aController.mMaintenanceHatches.clear();
        aController.mMufflerHatches.clear();
        for (byte h = (byte) -contreollerCoords[0]; h < (pattern.length - contreollerCoords[0]); h++) {//y
            for (byte i = (byte) -contreollerCoords[1]; i < (pattern[0].length - contreollerCoords[1]); i++) {//x
                for (byte j = (byte) -contreollerCoords[2]; j < (pattern[0][0].length - contreollerCoords[2]); j++) {//z
                    if (h == 0 && i == 0 && j == 0) {
                        continue;
                    }

                    char c = pattern[h + contreollerCoords[0]][i + contreollerCoords[1]][j + contreollerCoords[2]];
                    if (!Character.isLowerCase(c)) {

                        if (checker == null || checker.getChar() != c) {
                            checker = casingMap.get(c);
                        }
                        if (checker == null)
                            return false;
                    } else {
                        char a = pattern[h + contreollerCoords[0]][i + contreollerCoords[1]][j + contreollerCoords[2]];
                        switch (a) {
                            case 'a':
                                if (aBaseMetaTileEntity.getAirOffset(tFacing == 5 ? -i : tFacing == 4 ? i : tFacing == 3 ? -j : j, -h * mFlipped, tFacing == 5 ? -j : tFacing == 4 ? j : tFacing == 3 ? -i : i))
                                    continue;
                                else
                                    return false;
                            case 's':
                                continue;
                            case 'd':
                                if (aController.addDynamoToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(tFacing == 5 ? -i : tFacing == 4 ? i : tFacing == 3 ? -j : j, -h * mFlipped, tFacing == 5 ? -j : tFacing == 4 ? j : tFacing == 3 ? -i : i), currentCasingIndex)) {
                                    continue;
                                } else
                                    return false;

                            default:
                                return false;
                        }
                    }


                    if (!checker.checkBlock(aBaseMetaTileEntity, aController, tFacing == 5 ? -i * xCoef : tFacing == 4 ? i * xCoef : tFacing == 3 ? -j * zCoef : j * zCoef, -h * mFlipped, tFacing == 5 ? -j * zCoef : tFacing == 4 ? j * zCoef : tFacing == 3 ? -i * xCoef : i * xCoef)) {
                        return false;
                    }

                }
            }
        }
        for(IBlockChecker tChecker : casingMap.values())
            if(!tChecker.onStructCreatedCheck())
                return false;
        return true;
    }


    private boolean patternProcessing() {
        for (byte i = 0; i < pattern.length; i++) {
            for (byte j = 0; j < pattern[0].length; j++) {
                for (byte k = 0; k < pattern[0][0].length; k++) {
                    if (pattern[i][j][k] == 'c') {
                        contreollerCoords[0] = i;
                        contreollerCoords[1] = j;
                        contreollerCoords[2] = k;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean renderGuide(IGregTechTileEntity aBaseMetaTileEntity, GT_MetaTileEntity_MultiBlockBase aController, byte controllerBackFacing, boolean aBuild, IGuideRenderer aRenderer) {
        byte tFacing = controllerBackFacing;
        if (!patternProcessing())
            return false;
        IBlockChecker checker = null;
        for (byte h = (byte) -contreollerCoords[0]; h < (pattern.length - contreollerCoords[0]); h++) {//y
            for (byte i = (byte) -contreollerCoords[1]; i < (pattern[0].length - contreollerCoords[1]); i++) {//x
                for (byte j = (byte) -contreollerCoords[2]; j < (pattern[0][0].length - contreollerCoords[2]); j++) {//z
                    if (h == 0 && i == 0 && j == 0) {
                        continue;
                    }

                    char c = pattern[h + contreollerCoords[0]][i + contreollerCoords[1]][j + contreollerCoords[2]];
                    if (!Character.isLowerCase(c)) {

                        if (checker == null || checker.getChar() != c) {
                            checker = casingMap.get(c);
                        }

                    } else {
                        continue;
                    }

                    if (checker == null)
                        return false;
                    checker.renderGuide(aBaseMetaTileEntity.getWorld(), aBaseMetaTileEntity.getXCoord()+(tFacing == 5 ? -i : tFacing == 4 ? i : tFacing == 3 ? -j : j), aBaseMetaTileEntity.getYCoord()+(-h * mFlipped),aBaseMetaTileEntity.getZCoord()+ (tFacing == 5 ? -j : tFacing == 4 ? j : tFacing == 3 ? -i : i), aBuild, aRenderer);

                }
            }
        }
        return true;
    }
}
