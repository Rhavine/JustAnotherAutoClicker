package cn.davidma.justanotherautoclicker;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = JustAnotherAutoClicker.MODID, name = JustAnotherAutoClicker.NAME,
	version = "1.0.0", clientSideOnly = true)
public class JustAnotherAutoClicker {
	
	public static final String MODID = "justanotherautoclicker";
	public static final String NAME = "Just Another Auto Clicker";
	public static final Logger LOGGER = LogManager.getLogger();
	
	private static KeyBinding clickLeftBinding;
	private static KeyBinding clickRightBinding;
	
	private static boolean clickLeft;
	private static boolean clickRight;
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		clickLeftBinding = new KeyBinding("key.justanotherautoclicker.clickleft",
				49, "key.categories.justanotherautoclicker");
		clickRightBinding = new KeyBinding("key.justanotherautoclicker.clickright",
				50, "key.categories.justanotherautoclicker");
		
		ClientRegistry.registerKeyBinding(clickLeftBinding);
		ClientRegistry.registerKeyBinding(clickRightBinding);
	}
	
	@EventBusSubscriber(Side.CLIENT)
	public static class EventListener {
		
		@SubscribeEvent
		public static void tick(ClientTickEvent event) {
			
			// disable when paused
			if (Minecraft.getMinecraft().isGamePaused()) return;
			
			if (clickLeft) leftClick();
			if (clickRight) rightClick();
		}
		
		@SubscribeEvent
		public static void keyPress(KeyInputEvent event) {
			if (clickLeftBinding.isPressed()) {
				clickLeft = !clickLeft;
			}
			
			if (clickRightBinding.isPressed()) {
				clickRight = !clickRight;
			}
		}
		
		@SubscribeEvent
		public static void renderOverlay(RenderGameOverlayEvent event) {
			if (event.getType() != ElementType.TEXT) return;
			
			Minecraft game = Minecraft.getMinecraft();
			List<String> notes = new ArrayList<>();
			
			if (clickLeft) {
				notes.add(I18n.format("key.justanotherautoclicker.clickingleft"));
			}
			
			if (clickRight) {
				notes.add(I18n.format("key.justanotherautoclicker.clickingright"));
			}
			
			for (int i = 0; i <  notes.size(); i++) {
				String text = notes.get(i);
				int spacing = game.fontRenderer.FONT_HEIGHT + 2;
				
				game.fontRenderer.drawString(text, 5, 5 + spacing * i, 0x00FF00);
			}
		}
	}
	
	private static void leftClick() {
		Minecraft game = Minecraft.getMinecraft();
		RayTraceResult result = game.objectMouseOver;
		
		
	}
	
	private static void rightClick() {
		Minecraft game = Minecraft.getMinecraft();
		RayTraceResult result = game.objectMouseOver;
		
		
	}
}
