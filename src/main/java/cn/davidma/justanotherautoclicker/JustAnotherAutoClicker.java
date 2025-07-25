package cn.davidma.justanotherautoclicker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "justanotherautoclicker", name = "Just Another Auto Clicker", version = "1.0", clientSideOnly = true)
public class JustAnotherAutoClicker {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static KeyBinding holdKey;
    private static int clickDelayCounter = 0;
    private static final int CLICK_DELAY_TICKS = 2;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        holdKey = new KeyBinding("Hold Auto Clicker", Keyboard.KEY_N, "Just Another Auto Clicker");
        ClientRegistry.registerKeyBinding(holdKey);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.world == null || mc.player == null) return;

        boolean keyHeld = holdKey.isKeyDown();

        if (keyHeld && hasEntityInFront()) {
            if (clickDelayCounter <= 0) {
                mc.clickMouse(); // ini yang bener buat serang entity
                clickDelayCounter = CLICK_DELAY_TICKS;
            } else {
                clickDelayCounter--;
            }
        } else {
            clickDelayCounter = 0;
        }
    }

    private boolean hasEntityInFront() {
        double reach = 3.0;
        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null) return false;

        Vec3d look = viewEntity.getLookVec().scale(reach);
        AxisAlignedBB box = viewEntity.getEntityBoundingBox()
            .expand(look.x, look.y, look.z)
            .grow(1.0D);

        return !mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, box).isEmpty();
    }
}
