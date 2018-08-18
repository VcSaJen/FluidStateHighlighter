package com.vcsajen.fluidstatehighlighter.render;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Collection;

import static com.mumfrey.liteloader.gl.GL.*;

public class FluidCubesRenderer extends FloatingCubesRenderer implements LimitedRender  {

    public enum RenderFluidType {
        NONE,
        SOURCE,
        FLOWING;
        public RenderFluidType getRotNext() {
            return values()[(ordinal() + 1) % values().length];
        }

        @Override
        public String toString() {
            switch (this) {
                case NONE:
                    return "none";
                case SOURCE:
                    return "source";
                case FLOWING:
                    return "flowing";
                default:
                    return "unknown";
            }
        }
    }

    public RenderFluidType getRenderFluidType() {
        return renderFluidType;
    }

    public void setRenderFluidType(RenderFluidType renderFluidType) {
        this.renderFluidType = renderFluidType;
    }

    private RenderFluidType renderFluidType = RenderFluidType.NONE;

    private int radius;

    public FluidCubesRenderer(Minecraft minecraft) {
        super(minecraft);
        radius = 8;
    }

    @Nullable
    private static Object getBlockStateProperty(IBlockState bs, String propertyName)
    {
        Collection<IProperty<?>> keys = bs.getPropertyKeys();
        for (IProperty<?> pk:
                keys) {
            if (pk.getName().equals(propertyName)) {
                return bs.getValue(pk);
            }
        }
        return null;
    }

    /*private boolean isForgeFluid(IBlockState bs) {//False always?
        Class<?> iFluidBlockClass;
        try {
            iFluidBlockClass = Class.forName ("net.minecraftforge.fluids.IFluidBlock", false, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            return false;
        }

        return iFluidBlockClass.isInstance(bs);
    }*/

    private static boolean isFluid(IBlockState bs) {//Also true for Forge liquids?
        return bs.getMaterial().isLiquid();
    }

    private void prepareCubes() {
        if (renderFluidType == RenderFluidType.NONE) return;
        boolean wantSources = renderFluidType == RenderFluidType.SOURCE;
        int x0 = MathHelper.floor(cx)-radius;
        int y0 = MathHelper.floor(cy)-radius;
        int z0 = MathHelper.floor(cz)-radius;
        int sizeX = radius*2+1;
        int sizeY = radius*2+1;
        int sizeZ = radius*2+1;
        for (int rx = 0; rx<sizeX; rx++)
            for (int ry = 0; ry<sizeY; ry++)
                for (int rz = 0; rz<sizeZ; rz++) {
                    int x = x0+rx;
                    int y = y0+ry;
                    int z = z0+rz;
                    if (y<0) continue;
                    if (y>=world.getHeight()) continue;
                    IBlockState bs = world.getBlockState(new BlockPos(x,y,z));
                    if (isFluid(bs)) {
                        Object lvlObj = getBlockStateProperty(bs,"level");
                        int lvl = (lvlObj instanceof Integer) ? (Integer)lvlObj : -1;
                        if (lvl>=0) {
                            if ((lvl==0)==wantSources)
                                addCube(x,y,z);
                        }

                    }

                }
    }

    @Override
    protected void internalRender(float partialTicks, boolean obstructed) {
        if (renderFluidType == RenderFluidType.NONE) return;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL_LINES, VF_POSITION);
        if (!obstructed)
            glLineWidth(3);
        else glLineWidth(1);
        if (renderFluidType == RenderFluidType.SOURCE)
            glColor4f(0, 1, 1, 1);
        else glColor4f(1, 1, 0, 1);
        for (Vec3d line: lines) {
            buf.pos(line.x, line.y, line.z).endVertex();
        }
        tessellator.draw();
    }

    @Override
    protected void beforeRender() {
        clearCubes();
        prepareCubes();
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public void setRadius(int radius) {
        this.radius=radius;
    }
}
