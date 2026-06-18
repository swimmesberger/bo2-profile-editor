# Borderlands 2 Profile Editor

![CI Build](https://github.com/swimmesberger/bo2-profile-editor/workflows/CI%20Build/badge.svg)

Edit your Borderlands 2 profile — Golden Keys, Badass Rank, combat stats, all customizations, and more. Your save is automatically backed up before every change.

---

## Getting Started

### Step 1 — Download the project

Download and unzip this project to any folder on your computer (for example, your Desktop).

### Step 2 — Run the launcher

Double-click the launcher file for your platform:

| Platform | File | Notes |
|---|---|---|
| **macOS** | `bl2.command` | Right-click → Open if macOS blocks it the first time |
| **Windows** | `bl2.bat` | Click **Yes** if Windows asks for administrator permission |

**That's it.** The launcher handles everything automatically:
- Installs Java if it isn't already installed
- Builds the editor
- Finds your Borderlands 2 save file
- Opens with your current profile stats displayed

Setup only runs once. Every launch after that opens instantly.

---

## What You'll See

When the launcher opens, it shows your current profile:

```
============================================
   Borderlands 2 Profile Editor
============================================

GOLDEN_KEYS             = 255
BADASS_RANK             = 36150155
BADASS_TOKENS           = 5
MAXIMUM_HEALTH          = 9975792.3
SHIELD_CAPACITY         = 9975792.3
...
ALL_CUSTOMIZATIONS      = true

--------------------------------------------
  Ready. Example commands:
    $get
    $set max
    $set GOLDEN_KEYS max BADASS_RANK max
    $cd
  Type 'exit' to quit.
--------------------------------------------
```

---

## Commands

### Read your profile

**macOS / Linux:**
```bash
$get
```
**Windows:**
```cmd
%get%
```

Read specific values:

**macOS / Linux:**
```bash
$get GOLDEN_KEYS BADASS_RANK
```
**Windows:**
```cmd
%get% GOLDEN_KEYS BADASS_RANK
```

---

### Set values

Set a single value:

**macOS / Linux:**
```bash
$set GOLDEN_KEYS 255
$set GOLDEN_KEYS max
```
**Windows:**
```cmd
%set% GOLDEN_KEYS 255
%set% GOLDEN_KEYS max
```

Set multiple values at once (one backup, one write):

**macOS / Linux:**
```bash
$set GOLDEN_KEYS max BADASS_RANK max BADASS_TOKENS max
```
**Windows:**
```cmd
%set% GOLDEN_KEYS max BADASS_RANK max BADASS_TOKENS max
```

Set **everything** to max:

**macOS / Linux:**
```bash
$set max
```
**Windows:**
```cmd
%set% max
```

---

### Switch save folder

If you have multiple Steam accounts or want to point the editor at a different save:

**macOS / Linux:**
```bash
$cd
```
**Windows:**
```cmd
%cd%
```

Switch directly by Steam ID:

**macOS / Linux:**
```bash
$cd 76561199125000821
```
**Windows:**
```cmd
%cd% 76561199125000821
```

Your choice is saved to `bl2.config` and remembered on every future launch.

---

## All Values You Can Get or Set

### Keys, Rank, and Customizations

| Value Type | What It Does | Type | `max` value |
|---|---|---|---|
| `GOLDEN_KEYS` | Golden Keys in your Shift menu | number | `255` |
| `BADASS_RANK` | Total Badass Rank on your profile | number | `2000000000` |
| `BADASS_TOKENS` | Unspent Badass Tokens | number | `500` |
| `ALL_CUSTOMIZATIONS` | Unlock every skin, head, and customization | `true` / `false` | `true` |

### Badass Stat Bonuses

| Value Type | What It Does | `max` value |
|---|---|---|
| `MAXIMUM_HEALTH` | Bonus health | `9975792.3` |
| `SHIELD_CAPACITY` | Bonus shield capacity | `9975792.3` |
| `SHIELD_RECHARGE_DELAY` | Reduced shield recharge delay | `9975792.3` |
| `SHIELD_RECHARGE_RATE` | Bonus shield recharge rate | `9975792.3` |
| `MELEE_DAMAGE` | Bonus melee damage | `9975792.3` |
| `GRENADE_DAMAGE` | Bonus grenade damage | `9975792.3` |
| `GUN_ACCURACY` | Bonus gun accuracy | `9975792.3` |
| `GUN_DAMAGE` | Bonus gun damage | `9975792.3` |
| `FIRE_RATE` | Bonus fire rate | `9975792.3` |
| `RECOIL_REDUCTION` | Reduced recoil | `9975792.3` |
| `RELOAD_SPEED` | Bonus reload speed | `9975792.3` |
| `ELEMENTAL_EFFECT_CHANCE` | Bonus elemental effect chance | `9975792.3` |
| `ELEMENTAL_EFFECT_DAMAGE` | Bonus elemental effect damage | `9975792.3` |
| `CRITICAL_HIT_DAMAGE` | Bonus critical hit damage | `9975792.3` |

---

## Backups

Every `set` command automatically creates a timestamped backup of your save before writing:

```
Backup saved: profile.bin.1718650000000
```

Backups are saved next to `profile.bin` and named `profile.bin.<number>`. To undo any change, rename the most recent backup back to `profile.bin`.

---

## Where Is My profile.bin?

| Platform | Path |
|---|---|
| **macOS** | `~/Library/Application Support/Borderlands 2/WillowGame/SaveData/<SteamID>/profile.bin` |
| **Windows** | `%USERPROFILE%\Documents\My Games\Borderlands 2\WillowGame\SaveData\<SteamID>\profile.bin` |
| **Linux (Steam/Proton)** | `~/.local/share/Steam/steamapps/compatdata/49520/pfx/.../SaveData/<SteamID>/profile.bin` |

`<SteamID>` is the long number in your save folder name (e.g. `76561199125000821`).

---

---

## Troubleshooting

### The launcher doesn't open

- **macOS:** Right-click `bl2.command` → Open → Open (macOS may block it on first launch because it was downloaded from the internet)
- **Windows:** Right-click `bl2.bat` → Run as administrator

### I need to redo setup

Delete the file `bl2.setup` in the project folder, then launch again.

### I want to set up manually instead of using the launcher

**macOS — paste into Terminal (opened inside this folder):**

Step 1 of 2 — Install Homebrew (skip if already installed, you will be asked for your Mac password):
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

Step 2 of 2 — Install Java and build:
```bash
echo >> ~/.zprofile
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
eval "$(/opt/homebrew/bin/brew shellenv)"
brew install openjdk@11
export PATH="/opt/homebrew/opt/openjdk@11/bin:$PATH"
chmod +x gradlew bl2.command
./gradlew shadowJar
setopt SH_WORD_SPLIT
export get="java -jar bl2.jar get"
export set="java -jar bl2.jar set"
export cd="java -jar bl2.jar change-folder"
$get MAXIMUM_HEALTH
```

**Windows — paste into Command Prompt (opened inside this folder):**
```cmd
winget install --id EclipseAdoptium.Temurin.11.JDK -e --silent --accept-package-agreements --accept-source-agreements
set "PATH=%PATH%;%ProgramFiles%\Eclipse Adoptium\jdk-11"
gradlew.bat shadowJar
set get=java -jar bl2.jar get
set set=java -jar bl2.jar set
set cd=java -jar bl2.jar change-folder
%get% MAXIMUM_HEALTH
```

### Advanced: Convert profile to JSON

**macOS / Linux:**
```bash
# Export
java -jar bl2.jar convert profile.bin -of JSON_DATA -o profile.json

# Import back
java -jar bl2.jar convert profile.json -if JSON_DATA -of COMPRESSED_LZO -o profile.bin
```
**Windows:**
```cmd
:: Export
java -jar bl2.jar convert profile.bin -of JSON_DATA -o profile.json

:: Import back
java -jar bl2.jar convert profile.json -if JSON_DATA -of COMPRESSED_LZO -o profile.bin
```
