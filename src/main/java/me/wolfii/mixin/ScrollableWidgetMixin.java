package me.wolfii.mixin;

import me.wolfii.Config;
import me.wolfii.ScrollMath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractScrollWidget.class)
public abstract class ScrollableWidgetMixin {
    @Shadow
    private double scrollAmount;

    @Shadow
    public abstract int getMaxScrollAmount();

    @Invoker("setScrollAmount")
    public abstract void invokeSetScrollAmount(double scrollAmount);

    @Unique
    private double animationTimer = 0;
    @Unique
    private double scrollStartVelocity = 0;
    @Unique
    private boolean renderSmooth = false;

    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void manipulateScrollAmount(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderSmooth = true;
        checkOutOfBounds(delta);

        if (Math.abs(ScrollMath.scrollbarVelocity(animationTimer, scrollStartVelocity)) < 1.0) return;
        applyMotion(delta);
    }

    @Unique
    private void applyMotion(float delta) {
        scrollAmount += ScrollMath.scrollbarVelocity(animationTimer, scrollStartVelocity) * delta;
        animationTimer += delta * 10 / Config.animationDuration;
    }

    @Unique
    private void checkOutOfBounds(float delta) {
        if (scrollAmount < 0) {
            scrollAmount += ScrollMath.pushBackStrength(Math.abs(scrollAmount), delta);
            if (scrollAmount > -0.2) scrollAmount = 0;
        }
        if (scrollAmount > getMaxScrollAmount()) {
            scrollAmount -= ScrollMath.pushBackStrength(scrollAmount - getMaxScrollAmount(), delta);
            if (scrollAmount < getMaxScrollAmount() + 0.2) scrollAmount = getMaxScrollAmount();
        }
    }

    @Redirect(method = "mouseScrolled", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractScrollWidget;setScrollAmount(D)V"))
    private void setVelocity(AbstractScrollWidget instance, double scrollY) {
        if (!renderSmooth) {
            ((ScrollableWidgetMixin) (Object) instance).invokeSetScrollAmount(scrollY);
            return;
        }
        double diff = scrollY - this.scrollAmount;
        diff = Math.signum(diff) * Math.min(Math.abs(diff), 10);
        diff *= Config.scrollSpeed;
        if (Math.signum(diff) != Math.signum(scrollStartVelocity)) diff *= 2.5d;
        animationTimer *= 0.5;
        scrollStartVelocity = ScrollMath.scrollbarVelocity(animationTimer, scrollStartVelocity) + diff;
        animationTimer = 0;
    }

    @Redirect(method = "renderScrollBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    private void modifyScrollbar(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height) {
        if (!renderSmooth) {
            instance.blitSprite(sprite, x, y, width, height);
            return;
        }
        if (scrollAmount < 0) {
            height -= ScrollMath.dampenSquish(Math.abs(scrollAmount), height);
        }
        int bottom = ((AbstractScrollWidget) (Object) this).getBottom();
        if (y + height > bottom) {
            y = bottom - height;
        }
        if (scrollAmount > getMaxScrollAmount()) {
            int squish = ScrollMath.dampenSquish(scrollAmount - getMaxScrollAmount(), height);
            y += squish;
            height -= squish;
        }
        instance.blitSprite(sprite, x, y, width, height);
    }
}