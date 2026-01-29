# Team Color Placeholder Guide

## 🎨 Available Color Placeholders

The plugin now provides **3 different color format placeholders** to ensure compatibility with all chat plugins:

### 1. `%void_team_colored%` - Legacy Hex Format (RECOMMENDED)

**Format:** `&x&R&R&G&G&B&B` (Bukkit/Spigot hex color format)

**Example Output:** `&x&F&F&5&5&5&5Dream Team`

**Best for:**
- ✅ Paper/Spigot servers 1.16+
- ✅ Most chat plugins (LuckPerms, DeluxeChat, ChatControl, etc.)
- ✅ Works with legacy color code parsers
- ✅ **USE THIS ONE FIRST** - Most compatible!

**How to use:**
```yaml
# LuckPerms Chat
format: '&7[%void_team_colored%&7] %player_displayname%: %message%'

# DeluxeChat
format: '&7[%void_team_colored%&7] {player}: {message}'

# EssentialsX Chat
format: '&7[%void_team_colored%&7] {DISPLAYNAME}: {MESSAGE}'
```

---

### 2. `%void_team_colored_hex%` - Simple Hex Format

**Format:** `&#RRGGBB` (Simplified hex format)

**Example Output:** `&#FF5555Dream Team`

**Best for:**
- Some chat plugins that support `&#` hex format
- Simpler to read in config files

**How to use:**
```yaml
format: '&7[%void_team_colored_hex%&7] %player_displayname%: %message%'
```

---

### 3. `%void_team_colored_mini%` - MiniMessage Format

**Format:** `<color:#RRGGBB>text</color>` (MiniMessage)

**Example Output:** `<color:#FF5555>Dream Team</color>`

**Best for:**
- ✅ Plugins with MiniMessage support
- ✅ Paper servers with MiniMessage API
- ✅ Modern chat plugins

**How to use:**
```yaml
format: '<gray>[%void_team_colored_mini%<gray>]</gray> %player_displayname%: %message%'
```

---

### 4. `%void_team_color%` - Just the Hex Code

**Format:** Plain hex code

**Example Output:** `#FF5555`

**Use case:** Manual color formatting

**How to use:**
```yaml
format: '&7[<#%void_team_color%>%void_team%&7] %player_displayname%: %message%'
```

---

### 5. `%void_team%` - No Color

**Format:** Plain text

**Example Output:** `Dream Team`

**Use case:** When you don't want colors

---

## 🔍 Which One Should You Use?

### Start Here: `%void_team_colored%` (Legacy Hex Format)

This is the **most compatible** format and should work with 99% of chat plugins.

**Test it:**
```
/papi parse me %void_team_colored%
```

**Expected output:**
```
&x&F&F&5&5&5&5Dream Team
```

### If That Doesn't Work, Try: `%void_team_colored_hex%`

Some plugins prefer the simpler `&#RRGGBB` format.

**Test it:**
```
/papi parse me %void_team_colored_hex%
```

**Expected output:**
```
&#FF5555Dream Team
```

### For Modern Servers: `%void_team_colored_mini%`

If you're using Paper 1.18+ with MiniMessage-enabled plugins.

**Test it:**
```
/papi parse me %void_team_colored_mini%
```

**Expected output:**
```
<color:#FF5555>Dream Team</color>
```

---

## 📋 Chat Plugin Examples

### LuckPerms Chat (Most Common)

```yaml
# In your LuckPerms chat config or via command
format: '&7[%void_team_colored%&7] %player_displayname%&7: %message%'
```

### DeluxeChat

```yaml
format: '&7[%void_team_colored%&7] {player}&7: {message}'
```

### VentureChat

```yaml
format: '&7[%void_team_colored%&7] {player}&7: {message}'
```

### ChatControl Red/Pro

```yaml
format: '&7[%void_team_colored%&7] {player}&7: {message}'
```

### EssentialsX Chat

```yaml
format: '&7[%void_team_colored%&7] {DISPLAYNAME}&7: {MESSAGE}'
```

### ChatEx

```yaml
format: '&7[%void_team_colored%&7] %player_displayname%&7: %message%'
```

---

## 🧪 Testing Steps

### 1. Reload PlaceholderAPI
```
/papi reload
```

### 2. Test the Placeholder
```
/papi parse me %void_team_colored%
```

### 3. Check Your Chat Plugin
```
# Reload your chat plugin (example for LuckPerms)
/lp reloadconfig
```

### 4. Send a Test Message
Just type in chat - your team name should appear colored!

---

## ❓ Troubleshooting

### Problem: Color shows as text (like "&x&F&F&5&5&5&5")

**Solution:** Your chat plugin doesn't parse color codes properly.
- Make sure you're using a modern chat plugin
- Update your chat plugin to the latest version
- Try a different placeholder format

### Problem: Color doesn't appear at all

**Solutions:**
1. Check PlaceholderAPI is installed: `/papi version`
2. Test placeholder: `/papi parse me %void_team_colored%`
3. Make sure the placeholder is in your chat format
4. Reload chat plugin config
5. Check you're on Minecraft 1.16+ for hex colors

### Problem: Shows `<color:#FF5555>` as text

**Solution:** You're using `%void_team_colored_mini%` but your chat plugin doesn't support MiniMessage.
- Switch to `%void_team_colored%` instead

### Problem: No color on older Minecraft versions (< 1.16)

**Solution:** Hex colors require Minecraft 1.16+
- Upgrade your server
- Or use only the 16 legacy colors (red, blue, green, etc.)

---

## 💡 Pro Tips

### Conditional Display (Only show if player has team)

Use PlaceholderAPI's relational placeholders:

```yaml
# Only show team tag if player is in a team
format: '%vault_prefix%%conditional_void_has_team% &7[%void_team_colored%&7]%% %player_displayname%&7: %message%'
```

### Reset Color to White

```
/team color white
```

### Preview Your Team Color

```
/team info
```

Shows a colored preview of your team name!

---

## 📊 Format Comparison

| Placeholder | Format | Example | Compatibility |
|-------------|--------|---------|---------------|
| `%void_team_colored%` | `&x&R&R&G&G&B&B` | `&x&F&F&5&5&5&5Dream Team` | ⭐⭐⭐⭐⭐ Best |
| `%void_team_colored_hex%` | `&#RRGGBB` | `&#FF5555Dream Team` | ⭐⭐⭐⭐ Good |
| `%void_team_colored_mini%` | `<color:#RRGGBB>` | `<color:#FF5555>Dream Team</color>` | ⭐⭐⭐ Modern |
| `%void_team_color%` | `#RRGGBB` | `#FF5555` | ⭐ Manual |
| `%void_team%` | Plain | `Dream Team` | ⭐⭐⭐⭐⭐ Always |

---

## ✅ Quick Fix Checklist

If colors aren't working:

- [ ] PlaceholderAPI is installed
- [ ] Tested with `/papi parse me %void_team_colored%`
- [ ] Using the correct placeholder in chat config
- [ ] Chat plugin is modern (supports hex colors)
- [ ] Server is Minecraft 1.16+
- [ ] Reloaded chat plugin after config change
- [ ] Team has a color set (`/team color red`)

---

## 🎯 Recommended Setup

**For 99% of servers, use this:**

```yaml
format: '&7[%void_team_colored%&7] %player_displayname%&7: %message%'
```

This uses the legacy hex format which is supported by virtually all modern chat plugins and provides full RGB color support!

---

**Need help?** Check that:
1. You're using `%void_team_colored%` (not the other variants)
2. Your chat plugin is up to date
3. You've reloaded both PlaceholderAPI and your chat plugin

The legacy hex format (`&x&R&R&G&G&B&B`) is the most reliable format for Bukkit/Spigot/Paper servers! 🎨

