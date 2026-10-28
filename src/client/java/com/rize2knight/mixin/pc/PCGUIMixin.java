package com.rize2knight.mixin.pc;

import com.cobblemon.mod.common.client.storage.ClientPC;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.client.keybind.CobblemonKeyBinds;
import com.cobblemon.mod.common.mixin.accessor.KeyBindingAccessor;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.client.gui.pc.StorageWidget;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility;
import com.rize2knight.CobblemonRizeTweaksClient;
import com.rize2knight.config.ModConfig;
import com.rize2knight.keybind.keybinds.JumpPCBoxKey;
import com.rize2knight.util.RIzeTweaksUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import org.lwjgl.glfw.GLFW;

@Mixin(value = PCGUI.class, priority = 1001)
public abstract class PCGUIMixin extends Screen {
    @Unique private static ModConfig config = CobblemonRizeTweaksClient.INSTANCE.getConfig();

    @Shadow(remap = false) private StorageWidget storageWidget;
    @Shadow public abstract void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta);
    @Shadow(remap = false) private Pokemon previewPokemon;
    @Shadow(remap = false) @Final private ClientPC pc;
    @Unique private boolean rizeTweaks$boxJumpActive;
    @Unique private String rizeTweaks$boxJumpInput = "";

    protected PCGUIMixin(Component component) {
        super(component);
        CobblemonRizeTweaksClient.LOGGER.info("Initializing PCGUIMixin with storageWidget: {}", storageWidget);
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/cobblemon/mod/common/client/render/RenderHelperKt;drawScaledText$default(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/network/chat/MutableComponent;Ljava/lang/Number;Ljava/lang/Number;FLjava/lang/Number;IIZZLjava/lang/Integer;Ljava/lang/Integer;ILjava/lang/Object;)V",
                    ordinal = 12
            ),
            index = 2
    )
    private MutableComponent overrideAbilityNameDisplay(MutableComponent text){
        //Replace Ability Text with HA highlighted text
        if(previewPokemon != null && config.HAHightlighter){
            if(hasHiddenAbility(previewPokemon)){
                return text.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700)));
            }
        }
        return text;
    }

    @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
    private void overrideKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir){
        if (handleBoxJumpInput(keyCode)) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }

        var summaryKey = (KeyBindingAccessor) CobblemonKeyBinds.INSTANCE.getSUMMARY();

        if (keyCode != summaryKey.boundKey().getValue()) return;
        // Prevent vanilla handling no matter what
        cir.setReturnValue(true);
        cir.cancel();

        if (RIzeTweaksUtil.summaryOpenedFromPC || previewPokemon == null) return;

        //Opens Summary from PC when Summary Key is pressed
        Screen previous = Minecraft.getInstance().screen;

        Summary.Companion.open(List.of(previewPokemon),true,0);

        Minecraft.getInstance().execute(() -> {
            if(Minecraft.getInstance().screen instanceof Summary && previous != null){
                RIzeTweaksUtil.summaryOpenedFromPC = true;
                RIzeTweaksUtil.pcGUI = (PCGUI) previous;
            }
        });
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderBoxJumpPrompt(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!rizeTweaks$boxJumpActive) return;
        String target = rizeTweaks$boxJumpInput.isEmpty() ? "_" : rizeTweaks$boxJumpInput;
        Component prompt = Component.translatable("text.cobblemonrizetweaks.boxJump.prompt", target);
        context.drawCenteredString(Minecraft.getInstance().font, prompt, this.width / 2, this.height / 2 + 92, 0xFFFFFF);
    }

    @Unique
    private boolean handleBoxJumpInput(int keyCode) {
        if (!config.pcBoxJump) return false;

        if (!rizeTweaks$boxJumpActive) {
            KeyBindingAccessor jumpKey = (KeyBindingAccessor) JumpPCBoxKey.INSTANCE.getMapping();
            if (keyCode != jumpKey.boundKey().getValue()) return false;
            rizeTweaks$boxJumpActive = true;
            rizeTweaks$boxJumpInput = "";
            return true;
        }

        if (keyCode == InputConstants.KEY_ESCAPE) {
            rizeTweaks$boxJumpInput = "";
            rizeTweaks$boxJumpActive = false;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            int boxNumber = -1;
            try {
                boxNumber = Integer.parseInt(rizeTweaks$boxJumpInput);
            } catch (NumberFormatException ignored) {
            }
            if (boxNumber >= 1 && boxNumber <= pc.getBoxes().size()) {
                storageWidget.setBox(boxNumber - 1);
            }
            rizeTweaks$boxJumpInput = "";
            rizeTweaks$boxJumpActive = false;
            return true;
        }
        if (keyCode == InputConstants.KEY_BACKSPACE) {
            if (!rizeTweaks$boxJumpInput.isEmpty()) {
                rizeTweaks$boxJumpInput = rizeTweaks$boxJumpInput.substring(0, rizeTweaks$boxJumpInput.length() - 1);
            }
            return true;
        }

        int digit = -1;
        if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
            digit = keyCode - GLFW.GLFW_KEY_0;
        } else if (keyCode >= GLFW.GLFW_KEY_KP_0 && keyCode <= GLFW.GLFW_KEY_KP_9) {
            digit = keyCode - GLFW.GLFW_KEY_KP_0;
        }
        if (digit >= 0 && rizeTweaks$boxJumpInput.length() < 3) {
            rizeTweaks$boxJumpInput += digit;
        }
        return true;
    }

    @Unique
    private boolean hasHiddenAbility(Pokemon pokemon) {
        for (var ability : pokemon.getForm().getAbilities()) {
            if (ability instanceof HiddenAbility hidden) {
                if (hidden.getTemplate().equals(pokemon.getAbility().getTemplate())) {
                    return true;
                }
            }
        }
        return false;
    }
}
