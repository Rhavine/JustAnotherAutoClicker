package cn.davidma.justanotherautoclicker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
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
	
	private KeyBinding holdRightBinding;
	private KeyBinding holdLeftBinding;
	private KeyBinding clickRightBinding;
	private KeyBinding clickLeftBinding;
	
	private boolean holdRight;
	private boolean holdLeft;
	private boolean clickRight;
	private boolean clickLeft;
	
	public JustAnotherAutoClicker() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}
	
	public void clientSetup(FMLClientSetupEvent event) {
		this.holdRightBinding = new KeyBinding("key.justanotherautoclicker.holdright",
				GLFW.GLFW_KEY_H, "key.categories.justanotherautoclicker");
		this.holdLeftBinding = new KeyBinding("key.justanotherautoclicker.holdright",
				GLFW.GLFW_KEY_H, "key.categories.justanotherautoclicker");
		this.clickRightBinding = new KeyBinding("key.justanotherautoclicker.holdright",
				GLFW.GLFW_KEY_H, "key.categories.justanotherautoclicker");
		this.clickLeftBinding = new KeyBinding("key.justanotherautoclicker.holdright",
				GLFW.GLFW_KEY_H, "key.categories.justanotherautoclicker");
		
		ClientRegistry.registerKeyBinding(this.holdRightBinding);
		ClientRegistry.registerKeyBinding(this.holdLeftBinding);
		ClientRegistry.registerKeyBinding(this.clickRightBinding);
		ClientRegistry.registerKeyBinding(this.clickLeftBinding);
	}
	
	@EventBusSubscriber(Dist.CLIENT)
	public static class EventListener {
		
		@SubscribeEvent
		public static void tick(ClientTickEvent event) {
			if (Minecraft.getInstance().isPaused()) return;
			
			
		}
	}
}
