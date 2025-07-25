package cn.davidma.justanotherautoclicker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Method;
import java.util.List;

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
                try {
                    Class<?> playerDataClass = Class.forName("maninthehouse.epicfight.capabilities.entity.player.PlayerData");
                    Method getMethod = playerDataClass.getMethod("get", EntityPlayer.class);
                    Object playerData = getMethod.invoke(null, mc.player);

                    Method getCombatMethod = playerDataClass.getMethod("getCombat");
                    Object combat = getCombatMethod.invoke(playerData);

                    Method attackMethod = combat.getClass().getMethod("attack");
                    attackMethod.invoke(combat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        List<Entity> entities = mc.world.getEntitiesWithinAABBExcludingEntity(viewEntity, box);

        return !entities.isEmpty();
    }
}
