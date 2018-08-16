package com.vcsajen.fluidstatehighlighter.render;
import com.mumfrey.liteloader.gl.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import static com.mumfrey.liteloader.gl.GL.*;

/*I looked at code by Mumfrey, yetanotherx, digitalshadowshark, and based it on that*/

public class BaseRenderer {
    protected Minecraft minecraft;
    protected World world;
    protected double cx=0, cy=0, cz=0;

    public BaseRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    protected void internalRender(float partialTicks, boolean obstructed) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(GL_LINES, VF_POSITION);

        //if (!obstructed)
        //    glColor4f(0, 1, 1, 1);
        //else glColor4f(0.7f, 0.7f, 0.7f, 1);
        //glLineWidth(2);
        if (!obstructed)
            glLineWidth(3);
        else glLineWidth(1);
        buf.pos(-cx, -cy, -cz).endVertex();
        buf.pos(-cx, -cy+128, -cz).endVertex();
        tessellator.draw();
    }

    protected void beforeRender() {
    }

    public void render(float partialTicks) {
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

        boolean oldFog = glGetBoolean(GL_FOG);
        glDisableTexture2D();

        glPushMatrix();
        glDisableFog();

        glDisableBlend();/**/
        glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
        glLineWidth(2);

        //cx, cy, cz
        Entity renderViewEntity = this.minecraft.getRenderViewEntity();
        if (renderViewEntity!=null) {
            cx = renderViewEntity.prevPosX + ((renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks);
            cy = renderViewEntity.prevPosY + ((renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks);
            cz = renderViewEntity.prevPosZ + ((renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks);
            world = renderViewEntity.world;
        }
        beforeRender();

        internalRender(partialTicks, false);

        glEnableDepthTest();
        glDepthMask(false);
        glDepthFunc(GL_GREATER); //DONE: Fix obstructed and unobstructed intersecting. ???AlphaBlend???, Nah, used thickness
        internalRender(partialTicks, true);

        glDepthFunc(GL_LEQUAL);
        glPopMatrix();
        if (oldFog) glEnableFog(); else glDisableFog();

        glDepthMask(true);
        glEnableTexture2D();
        glEnableBlend();/**/

        RenderHelper.enableStandardItemLighting();
    }
}
