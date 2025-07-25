package cn.davidma.justanotherautoclicker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

import org.lwjgl.glfw.GLFW;

@Mod("justanotherautoclicker")
public class JustAnotherAutoClicker {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final KeyBinding aimKey = new KeyBinding("key.justanotherautoclicker.aim", GLFW.GLFW_KEY_P, "JustAnotherAutoClicker");
    private static boolean enabled = false;

    public JustAnotherAutoClicker() {
        ClientRegistry.registerKeyBinding(aimKey);
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class Events {
        @SubscribeEvent
        public static void onTick(TickEvent.ClientTickEvent event) {
            if (!enabled || mc.isPaused() || mc.player == null || mc.level == null) return;

            ClientPlayerEntity player = mc.player;

            double range = 5.0;
            Entity closest = null;
            double closestDist = Double.MAX_VALUE;

            for (Entity e : mc.level.getEntities(player, player.getBoundingBox().inflate(range))) {
                if (e instanceof LivingEntity && e instanceof IMob && e.isAlive()) {
                    double dist = e.distanceToSqr(player);
                    if (isInFront(player, e) && dist < closestDist) {
                        closestDist = dist;
                        closest = e;
                    }
                }
            }

            if (closest != null) {
                lookAt(player, closest);
                player.displayClientMessage(new net.minecraft.util.text.StringTextComponent("AIM → " + closest.getName().getString()), true);
            }
        }

        @SubscribeEvent
        public static void onKey(InputEvent.KeyInputEvent event) {
            if (aimKey.isDown()) {
                enabled = !enabled;
                String msg = enabled ? "§aON" : "§cOFF";
                mc.player.displayClientMessage(new net.minecraft.util.text.StringTextComponent("Aim Assist: " + msg), true);
            }
        }

        private static boolean isInFront(ClientPlayerEntity player, Entity target) {
            Vector3d look = player.getLookAngle().normalize();
            Vector3d dir = target.position().subtract(player.position()).normalize();
            return look.dot(dir) > 0.5; // sekitar 60 derajat ke depan
        }

        private static void lookAt(ClientPlayerEntity player, Entity target) {
            Vector3d eyes = player.getEyePosition(1.0f);
            Vector3d targetEyes = target.getEyePosition(1.0f);
            Vector3d delta = targetEyes.subtract(eyes);

            double dist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
            float yaw = (float)(Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0F);
            float pitch = (float)(-Math.toDegrees(Math.atan2(delta.y, dist)));

            player.yRot = yaw;
            player.xRot = pitch;
        }
    }
}
