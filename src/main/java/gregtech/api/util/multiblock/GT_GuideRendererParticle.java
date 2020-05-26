package gregtech.api.util.multiblock;
//created by Technus; github:

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.objects.GT_RenderedTexture;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GT_GuideRendererParticle extends EntityFX {
    private IIcon[] icons = new IIcon[6];
    private short[] modulation = null;

    public GT_GuideRendererParticle(World world, int x, int y, int z, Block block, int meta, IGuideRenderer aRenderer) {
        super(world, x+.25, y+.5, z+.25);
        particleGravity = 0;
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        noClip = true;
        particleMaxAge = 40;
        for (int i = 0; i < 6; i++) {
            icons[i] = block.getIcon(i, meta);
        }
        if(aRenderer!=null)
            aRenderer.addParticle(this);
    }

    public GT_GuideRendererParticle(World world, int x, int y, int z, int tileID, IGuideRenderer aRenderer) {
        super(world, x+.25, y+.5, z+.25);
        particleGravity = 0;
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        noClip = true;
        particleMaxAge = 40;
        IMetaTileEntity tMetaTileEntity = GregTech_API.METATILEENTITIES[tileID];
        if (tMetaTileEntity == null) {
            return;
        }
        for (byte i = 0; i < 6; i++) {
            ITexture[]ar =tMetaTileEntity.getTexture(tMetaTileEntity.getBaseMetaTileEntity(),i,(byte) 9, (byte) -1,true,false) ;
            for(ITexture t : ar){
                if(t instanceof GT_RenderedTexture){
                    icons[i] = ((GT_RenderedTexture)t).getIconContainer().getIcon();
                    modulation = ((GT_RenderedTexture)t).getRGBA();
                }
            }
        }
        if(aRenderer!=null)
            aRenderer.addParticle(this);
    }

    @Override
    public void renderParticle(Tessellator tes, float subTickTime, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
        float size = .5f;
        float X = (float) (prevPosX + (posX - prevPosX) * (double) subTickTime - EntityFX.interpPosX);
        float Y = (float) (prevPosY + (posY - prevPosY) * (double) subTickTime - EntityFX.interpPosY) - size / 2;
        float Z = (float) (prevPosZ + (posZ - prevPosZ) * (double) subTickTime - EntityFX.interpPosZ);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        if(modulation==null)
            tes.setColorRGBA_F(.9F, .95F, 1F, .75f);
        else
            tes.setColorRGBA(modulation[0],modulation[1],modulation[2],191);
        //var8, var9 - X U
        //var 10, var 11 - Y V
        for(int i=0;i<6;i++){
            if(icons[i]==null) {
                continue;
            }
            double u=icons[i].getMinU();
            double U=icons[i].getMaxU();
            double v=icons[i].getMinV();
            double V=icons[i].getMaxV();
            switch (i){//{DOWN, UP, NORTH, SOUTH, WEST, EAST}
                case 0:
                    tes.addVertexWithUV(X, Y, Z + size, u, V);
                    tes.addVertexWithUV(X, Y, Z, u, v);
                    tes.addVertexWithUV(X + size, Y, Z, U, v);
                    tes.addVertexWithUV(X + size, Y, Z + size, U, V);
                    break;
                case 1:
                    tes.addVertexWithUV(X, Y + size, Z, u, v);
                    tes.addVertexWithUV(X, Y + size, Z + size, u, V);
                    tes.addVertexWithUV(X + size, Y + size, Z + size, U, V);
                    tes.addVertexWithUV(X + size, Y + size, Z, U, v);
                    break;
                case 2:
                    tes.addVertexWithUV(X, Y, Z, U, V);
                    tes.addVertexWithUV(X, Y + size, Z, U, v);
                    tes.addVertexWithUV(X + size, Y + size, Z, u, v);
                    tes.addVertexWithUV(X + size, Y, Z, u, V);
                    break;
                case 3:
                    tes.addVertexWithUV(X + size, Y, Z + size, U, V);
                    tes.addVertexWithUV(X + size, Y + size, Z + size, U, v);
                    tes.addVertexWithUV(X, Y + size, Z + size, u, v);
                    tes.addVertexWithUV(X, Y, Z + size, u, V);
                    break;
                case 4:
                    tes.addVertexWithUV(X, Y, Z + size, U, V);
                    tes.addVertexWithUV(X, Y + size, Z + size, U, v);
                    tes.addVertexWithUV(X, Y + size, Z, u, v);
                    tes.addVertexWithUV(X, Y, Z, u, V);
                    break;
                case 5:
                    tes.addVertexWithUV(X + size, Y, Z, U, V);
                    tes.addVertexWithUV(X + size, Y + size, Z, U, v);
                    tes.addVertexWithUV(X + size, Y + size, Z + size, u, v);
                    tes.addVertexWithUV(X + size, Y, Z + size, u, V);
                    break;
            }
        }
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(true);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass==2;
    }
}