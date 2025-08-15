package cn.davidma.justanotherautoclicker;

import java.lang.reflect.Method; import java.lang.reflect.InvocationTargetException; import net.minecraft.client.Minecraft; import net.minecraft.client.entity.player.ClientPlayerEntity; import net.minecraft.entity.Entity; import net.minecraft.entity.passive.TameableEntity; import net.minecraft.util.Hand; import net.minecraft.util.math.EntityRayTraceResult; import net.minecraft.client.settings.KeyBinding; import org.lwjgl.glfw.GLFW; import net.minecraftforge.api.distmarker.Dist; import net.minecraftforge.event.TickEvent.ClientTickEvent; import net.minecraftforge.eventbus.api.SubscribeEvent; import net.minecraftforge.fml.common.Mod; import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext; import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent; import net.minecraftforge.fml.client.registry.ClientRegistry; import net.minecraftforge.client.ForgeHooksClient;

@Mod("justanotherautoclicker") public class JustAnotherAutoClicker {

private static KeyBinding autoAttackKey;

public JustAnotherAutoClicker() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
}

public void clientSetup(FMLClientSetupEvent event) {
    // register keybind untuk AutoAttack
    autoAttackKey = new KeyBinding("key.justanotherautoclicker.autoattack", 
                                   GLFW.GLFW_KEY_K, 
                                   "key.categories.justanotherautoclicker");
    ClientRegistry.registerKeyBinding(autoAttackKey);
}

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "justanotherautoclicker")
public static class AutoAttack {
    private static final Minecraft minecraft = Minecraft.func_71410_x();
    static Method method;
    private static int ticksSinceLeftClick = 0;
    private static final int RIGHT_CLICK_DELAY = 5; // 25% cooldown dari 20 tick

    static {
        try {
            method = Minecraft.class.getDeclaredMethod("func_147116_af");
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent event) {
        if (!autoAttackKey.isDown()) return;

        ClientPlayerEntity player = minecraft.field_71439_g;
        if (player == null) return;

        // Klik kiri (main hand) pakai refleksi
        if (player.func_184825_o(0.0F) >= 1.0F) {
            try {
                method.invoke(minecraft);
            } catch (InvocationTargetException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            ticksSinceLeftClick = 0;
        }

        // Klik kanan (off-hand) setelah delay 5 tick
        if (ticksSinceLeftClick == RIGHT_CLICK_DELAY) {
            for (Hand hand : Hand.values()) {
                ForgeHooksClient.onClickInput(1, minecraft.options.keyUse, hand);
            }
        }

        ticksSinceLeftClick++;
    }
}

}

