package cn.davidma.justanotherautoclicker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("justanotherautoclicker")
public class JustAnotherAutoClicker {

	public static final Logger LOGGER = LogManager.getLogger();
	
	private static KeyBinding clickLeftBinding;
	private static KeyBinding clickRightBinding;
	
	private static boolean clickLeft;
	private static boolean clickRight;
	
	public JustAnotherAutoClicker() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}
	
	public void clientSetup(FMLClientSetupEvent event) {
		clickLeftBinding = new KeyBinding("key.justanotherautoclicker.clickleft",
				GLFW.GLFW_KEY_P, "key.categories.justanotherautoclicker");
		clickRightBinding = new KeyBinding("key.justanotherautoclicker.clickright",
				GLFW.GLFW_KEY_O, "key.categories.justanotherautoclicker");
		
		ClientRegistry.registerKeyBinding(clickLeftBinding);
		ClientRegistry.registerKeyBinding(clickRightBinding);
	}
	
	@EventBusSubscriber(Dist.CLIENT)
	public static class EventListener {
		
		@SubscribeEvent
		public static void tick(ClientTickEvent event) {
			final Minecraft game = Minecraft.getInstance();
			
			// disable when paused
			if (game.isPaused()) return;
			
			if (clickLeft) leftClick();
			if (clickRight) rightClick();
		}
		
		@SubscribeEvent
		public static void keyPress(KeyInputEvent event) {
			if (clickLeftBinding.isDown()) {
				clickLeft = !clickLeft;
			}
			
			if (clickRightBinding.isDown()) {
				clickRight = !clickRight;
			}
		}
	}
	
	private static void leftClick() {
		final Minecraft game = Minecraft.getInstance();
		final RayTraceResult result = game.hitResult;
		
		final ClickInputEvent event = ForgeHooksClient.onClickInput(0, game.options.keyAttack, Hand.MAIN_HAND);
		
		switch (result.getType()) {
		case BLOCK:
			final BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
			final BlockPos pos = blockResult.getBlockPos();
			
			if (!game.level.isEmptyBlock(pos)) {
                game.gameMode.startDestroyBlock(pos, blockResult.getDirection());
                break;
             }
			break;
		case ENTITY:
			game.gameMode.attack(game.player, ((EntityRayTraceResult) result).getEntity());
            break;
		default:
			break;
		}
		
		if (event.shouldSwingHand()) game.player.swing(Hand.MAIN_HAND);
	}
	
	private static void rightClick() {
		
	}
}
