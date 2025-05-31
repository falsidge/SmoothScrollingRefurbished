package me.wolfii;

//public class Config {
//    public static double scrollSpeed = 0.5;
//    public static double scrollbarDrag= 0.025;
//    public static double animationDuration = 1.0;
//    public static double pushBackStrength = 1.0;
//}


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

//
//public class Config {
//    public float speed = 25.0f;
//}


@EventBusSubscriber(modid = SmoothScrollingRefurbishedMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue SCROLL_SPEED_VALUE = BUILDER
            .comment("Speed of scrolling")
            .defineInRange("scrollSpeed", 0.5, 0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue SCROLL_BAR_DRAG = BUILDER
            .comment("Scrolling Drag")
            .defineInRange("scrollbarDrag", 0.025, 0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue ANIMATION_DURATION = BUILDER
            .comment("Animation Duration")
            .defineInRange("animationDuration", 1.0, 0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue PUSHBACK_STRENGTH = BUILDER
            .comment("Pushback Strength")
            .defineInRange("pushBackStrength", 1.0, 0, Double.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static double scrollSpeed;
    public static double scrollbarDrag;
    public static double animationDuration;
    public static double pushBackStrength;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        scrollSpeed = SCROLL_SPEED_VALUE.get();
        scrollbarDrag = SCROLL_BAR_DRAG.get();
        animationDuration = ANIMATION_DURATION.get();
        pushBackStrength = PUSHBACK_STRENGTH.get();
    }
}