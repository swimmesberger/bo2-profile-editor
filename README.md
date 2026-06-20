# Borderlands 2 Profile Editor

![CI Build](https://github.com/swimmesberger/bo2-profile-editor/workflows/CI%20Build/badge.svg)

Edit your Borderlands 2 profile — Golden Keys, Badass Rank, combat stats, all customizations, and more.

---

## Getting Started

### macOS

1. Download `BL2ProfileEditor-macos-arm64.zip` (Apple Silicon) or `BL2ProfileEditor-macos-x64.zip` (Intel) from the [Releases](../../releases) page.
2. Unzip it and double-click `BL2ProfileEditor.app`.
3. **First time only:** if macOS says the app can't be opened, right-click it → **Open** → **Open**.

A Terminal window opens showing your current profile stats. Type commands directly in that window.

### Windows

1. Download `bl2-windows-x64.exe` from the [Releases](../../releases) page.
2. Open a Command Prompt in the same folder and run:
   ```cmd
   bl2-windows-x64.exe get
   ```
   Optionally rename it to `bl2.exe` for shorter commands.

### Linux

1. Download `bl2-linux-x64-bin` from the [Releases](../../releases) page.
2. Make it executable and run it:
   ```bash
   chmod +x bl2-linux-x64-bin
   ./bl2-linux-x64-bin get
   ```

---

## What You'll See

When the app opens (or you run `bl2 get`), it shows your active profile:

```
============================================
   Borderlands 2 Profile Editor
============================================

Profile: 76561199125000821

GOLDEN_KEYS             = 52
BADASS_RANK             = 1,315
BADASS_TOKENS           = 10
MAXIMUM_HEALTH          = 0.0
SHIELD_CAPACITY         = 0.0
...
ALL_CUSTOMIZATIONS      = false

--------------------------------------------
Commands:
  bl2 backup
  bl2 undo
  bl2 get
  bl2 set all max
  bl2 set GOLDEN_KEYS max BADASS_RANK max
  bl2 change-profile
Type 'exit' to close this window.
--------------------------------------------

Always run bl2 backup before editing.
```

`bl2 change-profile` only appears when more than one profile is detected.

---

## Commands

All examples use `bl2`. On macOS this is already on your PATH inside the Terminal window that opens from the app.

---

### Backup and restore

**Always create a backup before editing:**

```bash
bl2 backup
```

Creates a timestamped copy of your profile next to `profile.bin` (e.g. `profile.bin.1718650000000`) and prints the full path.

**Undo the last change:**

```bash
bl2 undo
```

Restores the most recent backup created by `bl2 backup`. Prints the date the backup was made so you know exactly what you're restoring.

---

### Read your profile

```bash
# Print all values for the active profile
bl2 get

# Print specific values only
bl2 get GOLDEN_KEYS BADASS_RANK
```

---

### Set values

`set` shows a before → after diff for every value that changed. Only changed values are printed; if nothing changed it says so.

Set a single stat:

```bash
bl2 set GOLDEN_KEYS 255
bl2 set GOLDEN_KEYS max
# Output:
# GOLDEN_KEYS  52 → 255
```

Set multiple stats in one write:

```bash
bl2 set GOLDEN_KEYS max BADASS_RANK max BADASS_TOKENS max
```

Set **everything** to the maximum safe value:

```bash
bl2 set all max
```

---

### Switch active profile

When you have multiple Steam accounts the editor will ask you to choose at first launch and remember your choice. The selection prompt shows Golden Keys and Badass Rank for each account so you can tell them apart:

```
  [1] 76561199125000821  —  Keys: 201  Rank: 36,150,155
  [2] 76561199125000822  —  Keys: 0    Rank: 1,200
```

To switch at any time:

```bash
# List all detected profiles and choose interactively
bl2 change-profile

# Switch by Steam ID
bl2 change-profile 76561199125000821

# Switch by providing a folder path directly
bl2 change-profile "/path/to/SaveData/76561199125000821"

# Switch by providing a direct path to profile.bin
bl2 change-profile "/path/to/profile.bin"
```

After switching, the editor immediately displays the new profile's stats.

---

### Point to a specific profile.bin

Use `-f` to bypass auto-detection entirely:

```bash
bl2 get -f /path/to/profile.bin
bl2 set -f /path/to/profile.bin GOLDEN_KEYS max
bl2 backup -f /path/to/profile.bin
```

---

## All Values You Can Get or Set

### Keys, Rank, and Customizations

| Value Type | What It Does | Type | `max` value |
|---|---|---|---|
| `GOLDEN_KEYS` | Golden Keys in your Shift menu | number | `255` |
| `BADASS_RANK` | Total Badass Rank on your profile | number | `2,000,000,000` |
| `BADASS_TOKENS` | Unspent Badass Tokens | number | `500` |
| `ALL_CUSTOMIZATIONS` | Unlock every skin, head, and customization | `true` / `false` | `true` |

### Badass Stat Bonuses

| Value Type | What It Does | `max` value |
|---|---|---|
| `MAXIMUM_HEALTH` | Bonus health | `9,975,792.3` |
| `SHIELD_CAPACITY` | Bonus shield capacity | `9,975,792.3` |
| `SHIELD_RECHARGE_DELAY` | Reduced shield recharge delay | `9,975,792.3` |
| `SHIELD_RECHARGE_RATE` | Bonus shield recharge rate | `9,975,792.3` |
| `MELEE_DAMAGE` | Bonus melee damage | `9,975,792.3` |
| `GRENADE_DAMAGE` | Bonus grenade damage | `9,975,792.3` |
| `GUN_ACCURACY` | Bonus gun accuracy | `9,975,792.3` |
| `GUN_DAMAGE` | Bonus gun damage | `9,975,792.3` |
| `FIRE_RATE` | Bonus fire rate | `9,975,792.3` |
| `RECOIL_REDUCTION` | Reduced recoil | `9,975,792.3` |
| `RELOAD_SPEED` | Bonus reload speed | `9,975,792.3` |
| `ELEMENTAL_EFFECT_CHANCE` | Bonus elemental effect chance | `9,975,792.3` |
| `ELEMENTAL_EFFECT_DAMAGE` | Bonus elemental effect damage | `9,975,792.3` |
| `CRITICAL_HIT_DAMAGE` | Bonus critical hit damage | `9,975,792.3` |

---

## Where Is My profile.bin?

The editor searches all of the following locations automatically. You only need to use `-f` or `change-profile` if your save is somewhere non-standard.

### macOS

| Launcher | Path |
|---|---|
| Steam | `~/Library/Application Support/Borderlands 2/WillowGame/SaveData/<SteamID>/profile.bin` |

### Windows

Steam, Epic Games, and GOG all write saves to the same Documents path.

| Launcher | Path |
|---|---|
| Steam / Epic / GOG | `%USERPROFILE%\Documents\My Games\Borderlands 2\WillowGame\SaveData\<SteamID>\profile.bin` |
| OneDrive-redirected Documents | `%OneDrive%\Documents\My Games\Borderlands 2\WillowGame\SaveData\<SteamID>\profile.bin` |

### Linux (Steam/Proton)

BL2 has no native Linux build; it runs via Proton. The editor checks all common Steam installation methods:

| Installation | Path |
|---|---|
| Standard Steam | `~/.local/share/Steam/steamapps/compatdata/49520/pfx/.../SaveData/<SteamID>/profile.bin` |
| Flatpak Steam | `~/.var/app/com.valvesoftware.Steam/.local/share/Steam/steamapps/compatdata/49520/pfx/.../SaveData/<SteamID>/profile.bin` |
| Snap Steam | `~/snap/steam/common/.local/share/Steam/steamapps/compatdata/49520/pfx/.../SaveData/<SteamID>/profile.bin` |

`<SteamID>` is the long number in your save folder name (e.g. `76561199125000821`).

---

## Settings file

The editor saves your active profile choice to a settings file so it's remembered across runs:

| Platform | Location |
|---|---|
| macOS | `~/Library/Application Support/bl2-profile-editor/bl2.config` |
| Windows | `%APPDATA%\bl2-profile-editor\bl2.config` |
| Linux | `~/.config/bl2-profile-editor/bl2.config` |

Run `bl2 change-profile` to update it.

---

## Troubleshooting

### macOS: "app cannot be opened because it is from an unidentified developer"

Right-click `BL2ProfileEditor.app` → **Open** → **Open**. You only need to do this once.

### The editor can't find my profile.bin

If auto-detection fails, point directly at the file:

```bash
bl2 get -f "/path/to/SaveData/76561199125000821/profile.bin"
```

Or run `bl2 change-profile` to pick from all detected folders.

### Advanced: Convert profile to JSON

```bash
# Export to JSON
bl2 convert profile.bin -of JSON_DATA -o profile.json

# Import back from JSON
bl2 convert profile.json -if JSON_DATA -of COMPRESSED_LZO -o profile.bin
```

---

## Changes in v1.1.0

- **Native binary distribution** — no Java install required, near-instant startup
- **macOS:** distributed as `BL2ProfileEditor.app` (drag to Applications, double-click to launch)
- **`backup` command** — create a timestamped checkpoint before editing; `undo` restores the most recent one
- **`set` shows a before → after diff** — only changed values are printed
- **`get` shows which profile is active** — the Steam ID appears above the stats
- **Formatted numbers** — large values display with commas (`36,150,155` not `36150155`)
- **Profile selection shows quick stats** — Golden Keys and Badass Rank shown per account when multiple profiles are detected
- **Multi-launcher detection** — auto-detects saves from Steam, Epic, GOG on Windows; standard, Flatpak, and Snap Steam on Linux
- **`change-profile`** replaces `change-folder`; accepts a Steam ID, folder path, or direct path to `profile.bin`
- **`get`** with no arguments prints all stats; one or more type names prints only those
- **`set`** supports multiple `TYPE value` pairs in one write; use `set all max` to set everything at once
- **Breaking:** `-f`/`--file` flag replaces the old positional file argument on `get` and `set`
