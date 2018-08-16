package com.vcsajen.fluidstatehighlighter;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.PostRenderListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.Input;
import com.vcsajen.fluidstatehighlighter.render.BaseRenderer;
import com.vcsajen.fluidstatehighlighter.render.FluidCubesRenderer;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;

public class LiteModFluidStateHighlighter implements JoinGameListener, PostRenderListener, Tickable {

    private static KeyBinding kbToggleLightLevels; // Toggle displaying it
    private static KeyBinding kbIncMaxLightLevel; // Increase max displayed light level (inclusive, max: 14)
    private static KeyBinding kbDecMaxLightLevel; // Decrease max displayed light level (inclusive, min: 0)

    private boolean visible;
    private int maxLightLvl = 11;
    private FluidCubesRenderer fluidRenderer;

    @Override
    public void onJoinGame(INetHandler netHandler, SPacketJoinGame joinGamePacket, ServerData serverData, RealmsServer realmsServer) {
        fluidRenderer = new FluidCubesRenderer(Minecraft.getMinecraft());
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        player.sendMessage(new TextComponentString("Light Levels 3D is successfully loaded!"));
    }

    @Override
    public String getVersion() {
        return LiteLoader.getInstance().getModMetaData(this, "version", "1.0");
    }

    @Override
    public void init(File configPath) {
        String keybindCategory = "Light Levels 3D";
        Input input = LiteLoader.getInput();
        kbToggleLightLevels = new KeyBinding("Toggle light levels", Keyboard.KEY_NUMPAD5, keybindCategory);
        kbIncMaxLightLevel = new KeyBinding("Increase max detected light level", Keyboard.KEY_NUMPAD8, keybindCategory);
        kbDecMaxLightLevel = new KeyBinding("Decrease max detected light level", Keyboard.KEY_NUMPAD2, keybindCategory);
        input.registerKeyBinding(kbToggleLightLevels);
        input.registerKeyBinding(kbIncMaxLightLevel);
        input.registerKeyBinding(kbDecMaxLightLevel);
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
    }

    @Override
    public String getName() {
        return LiteLoader.getInstance().getModMetaData(this, "displayName", this.getClass().getSimpleName());
    }

    @Override
    public void onPostRenderEntities(float partialTicks) {
        fluidRenderer.render(partialTicks);
    }

    @Override
    public void onPostRender(float partialTicks) {

    }

    private String getBlockStatePropertyNames(IBlockState bs)
    {
        String result = "";
        Collection<IProperty<?>> keys = bs.getPropertyKeys();
        for (IProperty<?> pk:
                keys) {
            result += pk.getName() + "; ";
        }

        return result;
    }

    private static final String[] lightLevelMobs = {"","","","","","","","(most hostile mob spawners)","","","","(blazes)","","","",""};

    private BlockPos findUnderEntityCoords(Entity entity) {
        double resultX = Math.floor(entity.posX);
        double resultY = Math.floor(entity.getEntityBoundingBox().minY)-1;
        double resultZ = Math.floor(entity.posZ);
        return new BlockPos(resultX, resultY, resultZ);
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (kbToggleLightLevels.isPressed()) {
            EntityPlayerSP player = minecraft.player;
            visible = !visible;
            fluidRenderer.setRenderFluidType(fluidRenderer.getRenderFluidType().getRotNext());
            player.sendMessage(new TextComponentString("Displaying fluids: "+fluidRenderer.getRenderFluidType()));
            //player.sendMessage(new TextComponentString("Displaying light levels '"+maxLightLvl+" and below' is "+(visible?"enabled":"disabled")));
            //IBlockState bs = player.getEntityWorld().getBlockState(findUnderEntityCoords(player));
            //Object lvlObj = getBlockStateProperty(bs,"level");
            //int lvl = (lvlObj instanceof Integer) ? (Integer)lvlObj : -1;
            //player.sendMessage(new TextComponentString(bs.toString()+": "+getBlockStatePropertyNames(bs)+" lvl:"+lvl));
            //player.sendMessage(new TextComponentString("vanillaFluid:" + isVanillaFluid(bs) + " forgeFluid:" + isForgeFluid(bs)));
        }
        if (kbIncMaxLightLevel.isPressed()) {
            EntityPlayerSP player = minecraft.player;
            maxLightLvl++;
            if (maxLightLvl>14) maxLightLvl=14;
            player.sendMessage(new TextComponentString("Displayed light level is set to "+maxLightLvl+" and below "+lightLevelMobs[maxLightLvl]));
        }
        if (kbDecMaxLightLevel.isPressed()) {
            EntityPlayerSP player = minecraft.player;
            maxLightLvl--;
            if (maxLightLvl<0) maxLightLvl=0;
            player.sendMessage(new TextComponentString("Displayed light level is set to "+maxLightLvl+" and below "+lightLevelMobs[maxLightLvl]));
        }
    }
}
