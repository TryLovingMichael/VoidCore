package dev.voidlessdev.voidCore.team;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    private static final Map<String, String> NAMED_COLORS = new HashMap<>();

    static {
        // Minecraft named colors mapped to hex
        NAMED_COLORS.put("black", "#000000");
        NAMED_COLORS.put("dark_blue", "#0000AA");
        NAMED_COLORS.put("dark_green", "#00AA00");
        NAMED_COLORS.put("dark_aqua", "#00AAAA");
        NAMED_COLORS.put("dark_red", "#AA0000");
        NAMED_COLORS.put("dark_purple", "#AA00AA");
        NAMED_COLORS.put("gold", "#FFAA00");
        NAMED_COLORS.put("gray", "#AAAAAA");
        NAMED_COLORS.put("dark_gray", "#555555");
        NAMED_COLORS.put("blue", "#5555FF");
        NAMED_COLORS.put("green", "#55FF55");
        NAMED_COLORS.put("aqua", "#55FFFF");
        NAMED_COLORS.put("red", "#FF5555");
        NAMED_COLORS.put("light_purple", "#FF55FF");
        NAMED_COLORS.put("yellow", "#FFFF55");
        NAMED_COLORS.put("white", "#FFFFFF");

        // Common aliases
        NAMED_COLORS.put("purple", "#AA00AA");
        NAMED_COLORS.put("pink", "#FF55FF");
        NAMED_COLORS.put("orange", "#FFAA00");
        NAMED_COLORS.put("cyan", "#55FFFF");
        NAMED_COLORS.put("lime", "#55FF55");
    }

    /**
     * Validates and normalizes a color code
     * @param color The color input (hex or named)
     * @return The normalized hex color code, or null if invalid
     */
    public static String parseColor(String color) {
        if (color == null) {
            return null;
        }

        String normalized = color.toLowerCase().trim();

        // Check if it's a hex color
        if (HEX_PATTERN.matcher(color).matches()) {
            return color.toUpperCase();
        }

        // Check if it's a named color
        if (NAMED_COLORS.containsKey(normalized)) {
            return NAMED_COLORS.get(normalized);
        }

        // Try with # prefix
        String withHash = "#" + normalized;
        if (HEX_PATTERN.matcher(withHash).matches()) {
            return withHash.toUpperCase();
        }

        return null;
    }

    /**
     * Converts hex color to TextColor for Adventure API
     * @param hexColor The hex color code
     * @return TextColor instance
     */
    public static TextColor hexToTextColor(String hexColor) {
        if (hexColor == null || !HEX_PATTERN.matcher(hexColor).matches()) {
            return NamedTextColor.WHITE;
        }

        try {
            return TextColor.fromHexString(hexColor);
        } catch (Exception e) {
            return NamedTextColor.WHITE;
        }
    }

    /**
     * Gets all available named colors
     * @return Map of color names to hex codes
     */
    public static Map<String, String> getNamedColors() {
        return new HashMap<>(NAMED_COLORS);
    }

    /**
     * Check if a color is valid
     * @param color The color to check
     * @return true if valid
     */
    public static boolean isValidColor(String color) {
        return parseColor(color) != null;
    }
}

