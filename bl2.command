#!/bin/bash
# Borderlands 2 Profile Editor — macOS Launcher
# Double-click this file in Finder to open the editor.

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

SETUP_FLAG="$SCRIPT_DIR/bl2.setup"

# ─────────────────────────────────────────────
# First-time setup (skipped after first run)
# ─────────────────────────────────────────────
if [ ! -f "$SETUP_FLAG" ]; then
    echo "============================================"
    echo "   First-Time Setup"
    echo "   (This only runs once)"
    echo "============================================"
    echo ""

    # ── Homebrew ──────────────────────────────────
    if [ -f /opt/homebrew/bin/brew ] || command -v brew &>/dev/null; then
        echo "[✓] Homebrew already installed."
        eval "$(/opt/homebrew/bin/brew shellenv)" 2>/dev/null || true
    else
        echo "[→] Installing Homebrew..."
        echo "    You will be asked for your Mac password once — this is normal."
        echo ""
        /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
        # Add brew to shell profile so it persists across sessions
        echo >> ~/.zprofile
        echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
        eval "$(/opt/homebrew/bin/brew shellenv)"
        echo "[✓] Homebrew installed."
    fi

    # ── Java 11 ───────────────────────────────────
    if [ -x /opt/homebrew/opt/openjdk@11/bin/java ]; then
        echo "[✓] Java 11 already installed."
    else
        echo "[→] Installing Java 11 (no password needed)..."
        brew install openjdk@11
        echo "[✓] Java 11 installed."
    fi

    # ── Build bl2.jar ─────────────────────────────
    if [ -f "$SCRIPT_DIR/bl2.jar" ]; then
        echo "[✓] bl2.jar already built."
    else
        echo "[→] Building bl2.jar..."
        chmod +x gradlew
        PATH="/opt/homebrew/opt/openjdk@11/bin:$PATH" ./gradlew shadowJar --quiet
        echo "[✓] bl2.jar built."
    fi

    touch "$SETUP_FLAG"
    echo ""
    echo "Setup complete! Future launches will skip this step."
    echo ""
    sleep 2
fi

# ─────────────────────────────────────────────
# Launch
# ─────────────────────────────────────────────
JAVA_HOME="/opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home"
PATH="$JAVA_HOME/bin:$PATH"
get="java -jar bl2.jar get"
set="java -jar bl2.jar set"
cd="java -jar bl2.jar change-folder"

clear
echo "============================================"
echo "   Borderlands 2 Profile Editor"
echo "============================================"
echo ""
$get
echo ""
echo "--------------------------------------------"
echo "  Ready. Example commands:"
echo '    $get'
echo '    $set max'
echo '    $set GOLDEN_KEYS max BADASS_RANK max'
echo '    $cd'
echo "  Type 'exit' to quit."
echo "--------------------------------------------"
echo ""

# Inject our settings into the spawned shell so $get/$set/$cd work.
# ZDOTDIR redirects zsh's startup to a temp dir where we copy the
# user's existing rc files and append our additions.
ZSH_INIT=$(mktemp -d)
[ -f ~/.zshrc    ] && cp ~/.zshrc    "$ZSH_INIT/.zshrc"    2>/dev/null || true
[ -f ~/.zprofile ] && cp ~/.zprofile "$ZSH_INIT/.zprofile" 2>/dev/null || true

cat >> "$ZSH_INIT/.zshrc" << ZSHEOF

# --- Borderlands 2 Profile Editor ---
setopt SH_WORD_SPLIT   # word-split \$get/\$set/\$cd like bash does
export JAVA_HOME="$JAVA_HOME"
export PATH="\$JAVA_HOME/bin:\$PATH"
export get="java -jar bl2.jar get"
export set="java -jar bl2.jar set"
export cd="java -jar bl2.jar change-folder"
builtin cd '$SCRIPT_DIR'
ZSHEOF

ZDOTDIR="$ZSH_INIT" exec zsh -i
