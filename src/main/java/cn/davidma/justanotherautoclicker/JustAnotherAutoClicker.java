package cn.davidma.justanotherautoclicker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
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
    private static final int HOLD_DURATION_TICKS = 2;

    private static KeyBinding toggleKey;
    private static boolean isClicking = false;
    private static int holdTickCounter = 0;
    private static boolean mousePressed = false;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        toggleKey = new KeyBinding("Toggle Auto Clicker", Keyboard.KEY_N, "Just Another Auto Clicker");
        ClientRegistry.registerKeyBinding(toggleKey);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (toggleKey.isPressed()) {
            isClicking = !isClicking;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.world == null || mc.player == null) return;

        if (isClicking && hasEntityInFront()) {
            if (!mousePressed) {
                mc.gameSettings.keyBindAttack.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
                mousePressed = true;
                holdTickCounter = HOLD_DURATION_TICKS;
            } else {
                holdTickCounter--;
                if (holdTickCounter <= 0) {
                    mc.gameSettings.keyBindAttack.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                    mousePressed = false;
                }
            }
        } else if (mousePressed) {
            mc.gameSettings.keyBindAttack.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
            mousePressed = false;
        }
    }

    private boolean hasEntityInFront() {
        double reach = 3.0;
        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null) return false;

        AxisAlignedBB box = viewEntity.getEntityBoundingBox()
                .expand(viewEntity.getLookVec().scale(reach))
                .grow(1.0D);
        return !mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, box).isEmpty();
    }
}
