package cn.davidma.justanotherautoclicker;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeHooks;
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
			
			// disable when paused
			if (Minecraft.getInstance().isPaused()) return;
			
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
		
		@SubscribeEvent
		public static void renderOverlay(RenderGameOverlayEvent event) {
			if (event.getType() != ElementType.TEXT) return;
			
			Minecraft game = Minecraft.getInstance();
			List<String> notes = new ArrayList<>();
			
			if (clickLeft) {
				notes.add(I18n.get("key.justanotherautoclicker.clickingleft"));
			}
			
			if (clickRight) {
				notes.add(I18n.get("key.justanotherautoclicker.clickingright"));
			}
			
			for (int i = 0; i <  notes.size(); i++) {
				String text = notes.get(i);
				int spacing = game.font.lineHeight + 2;
				
				game.font.draw(new MatrixStack(), text, 5, 5 + spacing * i, 0x00FF00);
			}
		}
	}
	
	private static void leftClick() {
		Minecraft game = Minecraft.getInstance();
		RayTraceResult result = game.hitResult;
		
		if (!game.player.isHandsBusy()) {
			ClickInputEvent event = ForgeHooksClient.onClickInput(
					0, game.options.keyAttack, Hand.MAIN_HAND);
			
			switch (result.getType()) {	
			case ENTITY:
				game.gameMode.attack(game.player, ((EntityRayTraceResult) result).getEntity());
	            break;
	            
			case BLOCK:
				BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
				BlockPos pos = blockResult.getBlockPos();
				
				if (!game.level.isEmptyBlock(pos)) {
	                game.gameMode.startDestroyBlock(pos, blockResult.getDirection());
	                break;
	            }
				
			case MISS:
				game.player.resetAttackStrengthTicker();
				ForgeHooks.onEmptyLeftClick(game.player);
			}
			
			if (event.shouldSwingHand()) game.player.swing(Hand.MAIN_HAND);
		}
	}
	
	private static void rightClick() {
		Minecraft game = Minecraft.getInstance();
		RayTraceResult result = game.hitResult;
		
		if (!game.gameMode.isDestroying() && !game.player.isHandsBusy()) {
			for (Hand hand: Hand.values()) {
				ClickInputEvent event = ForgeHooksClient.onClickInput(
						1, game.options.keyUse, hand);
				
				if (event.isCanceled()) {
					if (event.shouldSwingHand()) {
						game.player.swing(hand);
					}
					
					return;
				}
				
				ItemStack stack = game.player.getItemInHand(hand);
				if (result != null) {
					switch (result.getType()) {
					case ENTITY:
						EntityRayTraceResult entityResult = (EntityRayTraceResult) result;
						Entity entity = entityResult.getEntity();
						ActionResultType actionEntityResult = game.gameMode.interactAt(
								game.player, entity, entityResult, hand);
						
						if (!actionEntityResult.consumesAction()) {
							actionEntityResult = game.gameMode.interact(game.player, entity, hand);
						}
						
						if (actionEntityResult.consumesAction()) {
							if (actionEntityResult.shouldSwing()) {
								if (event.shouldSwingHand()) game.player.swing(hand);
							}
						}
						
						break;
					
					case BLOCK:
						BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
						int count = stack.getCount();
						ActionResultType actionBlockResult = game.gameMode.useItemOn(
								game.player, game.level, hand, blockResult);
						
						if (actionBlockResult.consumesAction()) {
							if (actionBlockResult.shouldSwing()) {
								if (event.shouldSwingHand()) game.player.swing(hand);
								
								if (!stack.isEmpty() && (stack.getCount() != count 
										|| game.gameMode.hasInfiniteItems())) {
									
		                        	game.gameRenderer.itemInHandRenderer.itemUsed(hand);
		                        }
							}
							
							return;
						}
						
						if (actionBlockResult == ActionResultType.FAIL) return;
						break;

					case MISS:
						break;
					}
				}
				
				if (stack.isEmpty() && (game.hitResult == null ||
						game.hitResult.getType() == RayTraceResult.Type.MISS)) {
					
					ForgeHooks.onEmptyClick(game.player, hand);
				}
				
				if (!stack.isEmpty()) {
					ActionResultType otherResult = game.gameMode.useItem(game.player, game.level, hand);
					
					if (otherResult.consumesAction()) {
						if (otherResult.shouldSwing()) {
							game.player.swing(hand);
	                    }
						
						game.gameRenderer.itemInHandRenderer.itemUsed(hand);
						return;
					}
				}
			}
		}
	}
}
