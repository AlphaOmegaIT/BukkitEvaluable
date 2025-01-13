package me.blvckbytes.bukkitevaluable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class ColorUtil {
	
	private static final Map<Character, String> LEGACY_TO_MINI_MAP = new HashMap<>();
	private static final Map<String, String> MINI_TO_LEGACY_MAP = new HashMap<>();
	private static final Pattern MINI_MESSAGE_TAG_PATTERN = Pattern.compile("<(/?)([a-z_]+)>");
	
	static {
		LEGACY_TO_MINI_MAP.put('0', "black");
		LEGACY_TO_MINI_MAP.put('1', "dark_blue");
		LEGACY_TO_MINI_MAP.put('2', "dark_green");
		LEGACY_TO_MINI_MAP.put('3', "dark_aqua");
		LEGACY_TO_MINI_MAP.put('4', "dark_red");
		LEGACY_TO_MINI_MAP.put('5', "dark_purple");
		LEGACY_TO_MINI_MAP.put('6', "gold");
		LEGACY_TO_MINI_MAP.put('7', "gray");
		LEGACY_TO_MINI_MAP.put('8', "dark_gray");
		LEGACY_TO_MINI_MAP.put('9', "blue");
		LEGACY_TO_MINI_MAP.put('a', "green");
		LEGACY_TO_MINI_MAP.put('b', "aqua");
		LEGACY_TO_MINI_MAP.put('c', "red");
		LEGACY_TO_MINI_MAP.put('d', "light_purple");
		LEGACY_TO_MINI_MAP.put('e', "yellow");
		LEGACY_TO_MINI_MAP.put('f', "white");
		LEGACY_TO_MINI_MAP.put('k', "obfuscated");
		LEGACY_TO_MINI_MAP.put('l', "bold");
		LEGACY_TO_MINI_MAP.put('m', "strikethrough");
		LEGACY_TO_MINI_MAP.put('n', "underlined");
		LEGACY_TO_MINI_MAP.put('o', "italic");
		LEGACY_TO_MINI_MAP.put('r', "reset");
		
		// Initialize mappings from MiniMessage tags to legacy color codes
		for (Map.Entry<Character, String> entry : LEGACY_TO_MINI_MAP.entrySet()) {
			MINI_TO_LEGACY_MAP.put(entry.getValue(), "&" + entry.getKey());
		}
	}
	
	/**
	 * Converts a message with legacy color codes to MiniMessage format.
	 * E.g., "&8Hello" becomes "<dark_gray>Hello"
	 *
	 * @param message The message containing legacy color codes.
	 * @return The message converted to MiniMessage format.
	 */
	public static @NotNull String convertLegacyColorsToMiniMessage(@NotNull String message) {
		// Translate '&' color codes to internal format '§'
		String coloredMessage = ChatColor.translateAlternateColorCodes('&', message);
		// Replace '§' color codes with MiniMessage tags
		StringBuilder miniMessage = new StringBuilder();
		char[] chars = coloredMessage.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == ChatColor.COLOR_CHAR && i + 1 < chars.length) {
				char code = chars[++i];
				String miniTag = LEGACY_TO_MINI_MAP.get(code);
				if (miniTag != null) {
					miniMessage.append('<').append(miniTag).append('>');
				}
			} else {
				miniMessage.append(chars[i]);
			}
		}
		return miniMessage.toString();
	}
	
	/**
	 * Converts a message with MiniMessage tags to legacy color codes.
	 * E.g., "<gray>Hello" becomes "&8Hello"
	 *
	 * @param message The message containing MiniMessage tags.
	 * @return The message converted to legacy color codes.
	 */
	public static @NotNull String convertMiniMessageToLegacyColors(@NotNull String message) {
		Matcher       matcher = MINI_MESSAGE_TAG_PATTERN.matcher(message);
		StringBuilder result  = new StringBuilder();
		while (matcher.find()) {
			String closingTag = matcher.group(1); // "/" if closing tag, empty if opening tag
			String tagName = matcher.group(2);
			String legacyCode = MINI_TO_LEGACY_MAP.get(tagName);
			if (legacyCode != null) {
				if (closingTag.isEmpty()) {
					// Opening tag: replace with legacy color code
					matcher.appendReplacement(result, legacyCode);
				} else {
					// Closing tag: reset formatting
					matcher.appendReplacement(result, "&r");
				}
			} else {
				// Unrecognized tag: remove it
				matcher.appendReplacement(result, "");
			}
		}
		matcher.appendTail(result);
		// Translate '&' color codes to internal format '§'
		return ChatColor.translateAlternateColorCodes('&', result.toString());
	}
}