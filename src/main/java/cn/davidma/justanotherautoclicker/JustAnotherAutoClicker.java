package cn.davidma.justanotherautoclicker;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeHooks;
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
		clickLeftBinding = new KeyBinding("Click Left",
				49, "Just Another Auto Clicker");
		clickRightBinding = new KeyBinding("Click Right",
				50, "Just Another Auto Clicker");
		
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
		public static void keyPress(GuiScreenEvent.KeyboardInputEvent.Pre event) {
			int key = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
			
			if (key == clickLeftBinding.getKeyCode()) {
				clickLeft = false;
			}
			
			if (key == clickRightBinding.getKeyCode()) {
				clickRight = false;
			}
		}
		
		@SubscribeEvent
		public static void renderOverlay(RenderGameOverlayEvent event) {
			if (event.getType() != ElementType.TEXT) return;
			
			Minecraft game = Minecraft.getMinecraft();
			List<String> notes = new ArrayList<>();
			
			if (clickLeft) {
				notes.add(I18n.format("Left Clicking"));
			}
			
			if (clickRight) {
				notes.add(I18n.format("Right Clicking"));
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
		
		if (game == null) return;
		
		RayTraceResult result = game.objectMouseOver;
		
		switch (result.typeOfHit) {
		
		case ENTITY:
			game.playerController.attackEntity(game.player, result.entityHit);
			break;
			
		case BLOCK:
			BlockPos pos = game.objectMouseOver.getBlockPos();
			
			if (!game.world.isAirBlock(pos)) {
				game.playerController.clickBlock(pos, game.objectMouseOver.sideHit);
				break;
			}
			
		case MISS:
			ForgeHooks.onEmptyLeftClick(game.player);
		}
	}
	
	private static void rightClick() {
		Minecraft game = Minecraft.getMinecraft();
		
		if (game == null) return;
		
		RayTraceResult result = game.objectMouseOver;
		
		for (EnumHand hand : EnumHand.values()) {
			ItemStack stack = game.player.getHeldItem(hand);
			
			if (game.objectMouseOver != null) {
				switch (result.typeOfHit) {
					
				case ENTITY:
					if (game.playerController.interactWithEntity(game.player, result.entityHit, result, hand) == EnumActionResult.SUCCESS) {
						return;
					}
					
					if (game.playerController.interactWithEntity(game.player, result.entityHit, hand) == EnumActionResult.SUCCESS) {
						return;
					}
					
					break;
					
				case BLOCK:
					BlockPos pos = game.objectMouseOver.getBlockPos();
					
					if (game.world.getBlockState(pos).getMaterial() != Material.AIR) {
						int i = stack.getCount();
						EnumActionResult action = game.playerController.processRightClickBlock(game.player, game.world, pos, result.sideHit, result.hitVec, hand);
					
						if (action == EnumActionResult.SUCCESS) {
							game.player.swingArm(hand);
							
							if (!stack.isEmpty() && (stack.getCount() != i || game.playerController.isInCreativeMode())) {
								game.entityRenderer.itemRenderer.resetEquippedProgress(hand);
							}
							
							return;
						}
					}
				}
			}
			
			if (stack.isEmpty() && (game.objectMouseOver == null || game.objectMouseOver.typeOfHit == RayTraceResult.Type.MISS)) {
				ForgeHooks.onEmptyClick(game.player, hand);
			}
			
			if (!stack.isEmpty() && game.playerController.processRightClick(game.player, game.world, hand) == EnumActionResult.SUCCESS) {
				game.entityRenderer.itemRenderer.resetEquippedProgress(hand);
			}
		}
	}
}
