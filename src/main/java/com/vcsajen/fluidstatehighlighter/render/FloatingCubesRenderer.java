package com.vcsajen.fluidstatehighlighter.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.List;

import static com.mumfrey.liteloader.gl.GL.*;

public class FloatingCubesRenderer extends BaseRenderer {
    public FloatingCubesRenderer(Minecraft minecraft) {
        super(minecraft);
    }

    List<Vec3d> lines = new LinkedList<>();
    protected double cubeSize = 0.2;

    protected void clearCubes() {
        lines.clear();
    }

    private Vec3d getRelative(double x, double y, double z) {
        return new Vec3d(x-cx, y-cy, z-cz);
    }

    private Vec3d getBlockCenter(Vec3d v) {
        return new Vec3d(v.x+.5, v.y+.5, v.z+.5);
    }

    protected void addCube(int x, int y, int z) {
        double s = cubeSize/2;

        lines.add(getBlockCenter(getRelative(x-s,y-s,z-s)));
        lines.add(getBlockCenter(getRelative(x-s,y+s,z-s)));

        lines.add(getBlockCenter(getRelative(x-s,y-s,z+s)));
        lines.add(getBlockCenter(getRelative(x-s,y+s,z+s)));

        lines.add(getBlockCenter(getRelative(x+s,y-s,z-s)));
        lines.add(getBlockCenter(getRelative(x+s,y+s,z-s)));

        lines.add(getBlockCenter(getRelative(x+s,y-s,z+s)));
        lines.add(getBlockCenter(getRelative(x+s,y+s,z+s)));

        lines.add(getBlockCenter(getRelative(x-s,y-s,z-s)));
        lines.add(getBlockCenter(getRelative(x-s,y-s,z+s)));

        lines.add(getBlockCenter(getRelative(x-s,y-s,z+s)));
        lines.add(getBlockCenter(getRelative(x+s,y-s,z+s)));

        lines.add(getBlockCenter(getRelative(x+s,y-s,z+s)));
        lines.add(getBlockCenter(getRelative(x+s,y-s,z-s)));

        lines.add(getBlockCenter(getRelative(x+s,y-s,z-s)));
        lines.add(getBlockCenter(getRelative(x-s,y-s,z-s)));

        lines.add(getBlockCenter(getRelative(x-s,y+s,z-s)));
        lines.add(getBlockCenter(getRelative(x-s,y+s,z+s)));

        lines.add(getBlockCenter(getRelative(x-s,y+s,z+s)));
        lines.add(getBlockCenter(getRelative(x+s,y+s,z+s)));

        lines.add(getBlockCenter(getRelative(x+s,y+s,z+s)));
        lines.add(getBlockCenter(getRelative(x+s,y+s,z-s)));

        lines.add(getBlockCenter(getRelative(x+s,y+s,z-s)));
        lines.add(getBlockCenter(getRelative(x-s,y+s,z-s)));

    }

    @Override
    protected void internalRender(float partialTicks, boolean obstructed) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL_LINES, VF_POSITION);
        //if (!obstructed)
        //    glColor4f(1, 0, 0, 1);
        //else glColor4f(0.7f, 0.7f, 0.7f, 1);
        //glLineWidth(2);
        if (!obstructed)
            glLineWidth(3);
        else glLineWidth(1);
        glColor4f(0, 1, 1, 1);

        addCube(0,64,0);

        for (Vec3d line: lines) {
            buf.pos(line.x, line.y, line.z).endVertex();
        }
        /*buf.pos(-cx, -cy, -cz).endVertex();
        buf.pos(-cx, -cy+128, -cz).endVertex();*/
        tessellator.draw();
        clearCubes();
    }
}
