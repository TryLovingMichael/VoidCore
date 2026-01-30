# VoidCore - Complete Guide

## 🎨 Team Color Placeholders

The plugin provides **3 different color format placeholders** to ensure compatibility with all chat plugins:

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

---

---

# ⚡ Pearl Cooldown System

## Overview
The pearl cooldown system prevents ender pearl spam by applying configurable cooldowns to pearl usage.

## 🎮 Pearl Cooldown Placeholders

### `%void_pearl_cooldown%` - Remaining Cooldown Time

**Returns:** Seconds remaining on cooldown (0 if no cooldown)

**Example Output:** `15` (seconds)

**Use in scoreboard/tab:**
```yaml
# Scoreboard example
- '&cPearl: %void_pearl_cooldown%s'
```

---

### `%void_pearl_ready%` - Pearl Ready Status

**Returns:** `true` if pearl is ready, `false` if on cooldown

**Example Output:** `true` or `false`

**Use for conditional display:**
```yaml
# Show different messages based on status
- '%void_pearl_ready% == true ? &aPearl Ready : &cOn Cooldown'
```

---

### `%void_pearl_cooldown_enabled%` - System Status

**Returns:** `true` if cooldown system is enabled, `false` if disabled

**Example Output:** `true` or `false`

---

## 📜 All Available Placeholders

### Team Placeholders
| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%void_team%` | Team name (no color) | `Dream Team` |
| `%void_team_colored%` | Team name with legacy hex color | `&x&F&F&5&5&5&5Dream Team` |
| `%void_team_colored_hex%` | Team name with &#RRGGBB format | `&#FF5555Dream Team` |
| `%void_team_colored_mini%` | Team name with MiniMessage format | `<color:#FF5555>Dream Team</color>` |
| `%void_team_color%` | Just the hex code | `#FF5555` |
| `%void_team_owner%` | Team owner's name | `Steve` |
| `%void_team_members%` | Number of team members | `5` |
| `%void_has_team%` | Whether player has a team | `true` or `false` |
| `%void_is_team_owner%` | Whether player is team owner | `true` or `false` |

### Pearl Cooldown Placeholders
| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%void_pearl_cooldown%` | Seconds remaining on cooldown | `15` |
| `%void_pearl_ready%` | Is pearl ready to use | `true` or `false` |
| `%void_pearl_cooldown_enabled%` | Is system enabled | `true` or `false` |

---

## ⚙️ Configuration

The pearl cooldown configuration is located in `config.yml`:

```yaml
pearl-cooldown:
  # Enable/disable pearl cooldown
  enabled: true
  
  # Cooldown time in seconds
  cooldown: 15
  
  # Message sent when pearl is on cooldown
  # Placeholders: {time} - remaining time
  cooldown-message: "&cYou must wait {time} seconds before using another ender pearl!"
  
  # Message sent when cooldown expires
  ready-message: "&aYour ender pearl is ready!"
  
  # Show actionbar countdown
  actionbar-enabled: true
  
  # Bypass permission
  bypass-permission: "voidcore.pearl.bypass"
```

---

## 🎮 Pearl Cooldown Commands

### Player Commands
| Command | Description |
|---------|-------------|
| `/pearl check` | Check your pearl cooldown status |
| `/pearl info` | View pearl cooldown information |

### Admin Commands (Requires `voidcore.pearl.admin`)
| Command | Description |
|---------|-------------|
| `/pearl toggle` | Toggle pearl cooldown on/off |
| `/pearl setcooldown <seconds>` | Set cooldown time in seconds |
| `/pearl clear [player]` | Clear cooldown for a player or all players |

### VoidCore Commands (Requires `voidcore.admin`)
| Command | Description |
|---------|-------------|
| `/voidcore reload` | Reload configuration file |
| `/voidcore version` | Show plugin version |

**Command Aliases:**
- `/pearl` → `/ec`, `/enderpearl`
- `/voidcore` → `/vc`

---

## 🔐 Permissions

### Pearl Cooldown Permissions
| Permission | Description | Default |
|------------|-------------|---------|
| `voidcore.admin` | Access to all admin commands | OP |
| `voidcore.pearl.admin` | Access to pearl admin commands | OP |
| `voidcore.pearl.bypass` | Bypass pearl cooldown | None |

---

## 💡 Pearl Cooldown Usage Examples

### Example 1: Setting a 10-second cooldown
```
/pearl setcooldown 10
```

### Example 2: Disabling pearl cooldown temporarily
```
/pearl toggle
```

### Example 3: Clearing all cooldowns
```
/pearl clear
```

### Example 4: Clearing a specific player's cooldown
```
/pearl clear PlayerName
```

### Example 5: Checking your cooldown
```
/pearl check
```

### Example 6: Reloading config after changes
```
/voidcore reload
```

---

## 🎯 Pearl Cooldown Features

### ✅ Spam Protection
The cooldown system **prevents spam clicking bypass**. Players cannot bypass the cooldown by repeatedly clicking - the system only triggers when a pearl is actually thrown.

### ✅ Actionbar Countdown
When enabled, players see a live countdown on their action bar:
```
Pearl Cooldown: 15s
Pearl Cooldown: 14s
Pearl Cooldown: 13s
...
Pearl Ready!
```

### ✅ In-Game Management
- Change cooldown duration without file editing
- Toggle system on/off instantly
- Clear cooldowns for specific players
- Reload config without server restart

### ✅ Bypass Permission
Players with `voidcore.pearl.bypass` permission can use pearls without any cooldown restriction.

---

## 🔧 Pearl Cooldown Troubleshooting

### Problem: Players can spam pearls

**Solution:** This has been fixed! The system now listens to the actual pearl throw event, not just the click event.

### Problem: Cooldown not applying

**Solutions:**
1. Check if pearl cooldown is enabled: `/pearl info`
2. Make sure player doesn't have bypass permission
3. Reload the plugin: `/voidcore reload`

### Problem: Messages not showing

**Solutions:**
1. Check config.yml for message settings
2. Ensure messages aren't empty
3. Reload config: `/voidcore reload`

### Problem: Actionbar not showing

**Solutions:**
1. Check `actionbar-enabled: true` in config.yml
2. Make sure you're on Minecraft 1.11+
3. Some clients may not support action bars

---

## 📊 Scoreboard Example

Use pearl cooldown placeholders in your scoreboard:

```yaml
# Example scoreboard with DeluxeTab/AnimatedScoreboard
lines:
  - '&6&lYOUR SERVER'
  - ''
  - '&eTeam: %void_team_colored%'
  - '&eMembers: %void_team_members%'
  - ''
  - '&cPearl: %void_pearl_ready% == true ? &aReady : &c%void_pearl_cooldown%s'
  - ''
  - '&7play.yourserver.com'
```

---

## 🎪 TAB List Example

Show pearl status in TAB:

```yaml
# Example TAB format
header:
  - '&6&lYOUR SERVER'
  - '&7Team: %void_team_colored%'
  
footer:
  - '&ePearl: %void_pearl_ready% == true ? &aReady : &cCooldown %void_pearl_cooldown%s'
  - '&7Online: %server_online%'
```

---

## ✅ Quick Start Checklist

### For Pearl Cooldown:
- [ ] Plugin installed and server restarted
- [ ] Check config.yml for pearl cooldown settings
- [ ] Test with `/pearl check`
- [ ] Verify cooldown works by throwing a pearl
- [ ] Set desired cooldown time with `/pearl setcooldown <seconds>`
- [ ] Give bypass permission to admins if desired

### For Team Colors in Chat:
- [ ] PlaceholderAPI installed
- [ ] Team created with `/team create <name>`
- [ ] Team color set with `/team color <color>`
- [ ] Placeholder added to chat format: `%void_team_colored%`
- [ ] Chat plugin reloaded
- [ ] Test in chat to see colored team name

---

## 🔄 Config Reload

You can now reload the configuration without restarting:

```
/voidcore reload
```

This reloads:
- ✅ Pearl cooldown settings
- ✅ All config.yml values
- ✅ Message formats
- ✅ Cooldown duration

**Note:** Teams are saved/loaded from `teams.yml` and persist automatically.

---

**Need help?** 
- Test placeholders: `/papi parse me %void_team_colored%`
- Check pearl status: `/pearl info`
- Reload config: `/voidcore reload`
- View version: `/voidcore version`
