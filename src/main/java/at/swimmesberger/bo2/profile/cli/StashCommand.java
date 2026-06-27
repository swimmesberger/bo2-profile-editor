package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.EntriesContainerFormat;
import at.swimmesberger.bo2.profile.ProfileEntries;
import at.swimmesberger.bo2.profile.ProfileEntryDataHandler;
import at.swimmesberger.bo2.profile.stash.GibbedCodec;
import at.swimmesberger.bo2.profile.stash.StashConverter;
import at.swimmesberger.bo2.profile.stash.StashSlot;
import at.swimmesberger.bo2.profile.util.OutputStreamSupplier;
import at.swimmesberger.bo2.profile.util.ProfileHandlerUtil;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "stash",
        mixinStandardHelpOptions = true,
        description = "Manage items in Claptrap's Stash using Gibbed codes.",
        subcommands = {
                StashCommand.ListCommand.class,
                StashCommand.GetCommand.class,
                StashCommand.SetItemCommand.class,
                StashCommand.AddCommand.class,
                StashCommand.ClearCommand.class
        })
public class StashCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

    // -------------------------------------------------------------------------

    @CommandLine.Command(name = "list", mixinStandardHelpOptions = true,
            description = "List all stash slots and their Gibbed codes.")
    static class ListCommand implements Callable<Integer> {
        @CommandLine.Option(names = {"-f", "--file"},
                description = "Path to profile.bin. If omitted, auto-detected.")
        private Path inputFile;

        @Override
        public Integer call() {
            try {
                Path file = resolveFile(inputFile);
                if (file == null) return -1;

                ProfileEntries entries = ProfileDataCache.getOrLoadEntries(file);
                List<StashSlot> slots = new StashConverter().readStash(entries);
                GibbedCodec codec = new GibbedCodec();

                if (slots.isEmpty()) {
                    System.out.println("No stash slots found in profile.");
                    return 0;
                }

                int width = String.valueOf(slots.size()).length();
                for (StashSlot slot : slots) {
                    String label = String.format("Slot %" + width + "d", slot.getIndex() + 1);
                    if (slot.isEmpty()) {
                        System.out.printf("%s  [empty]%n", label);
                    } else {
                        System.out.printf("%s  %s%n", label, codec.encode(slot.getData()));
                    }
                }
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    // -------------------------------------------------------------------------

    @CommandLine.Command(name = "get", mixinStandardHelpOptions = true,
            description = "Print the Gibbed code for a stash slot (1-based).")
    static class GetCommand implements Callable<Integer> {
        @CommandLine.Option(names = {"-f", "--file"},
                description = "Path to profile.bin. If omitted, auto-detected.")
        private Path inputFile;

        @CommandLine.Parameters(index = "0", description = "Slot number (1-based).")
        private int slot;

        @Override
        public Integer call() {
            try {
                Path file = resolveFile(inputFile);
                if (file == null) return -1;

                ProfileEntries entries = ProfileDataCache.getOrLoadEntries(file);
                List<StashSlot> slots = new StashConverter().readStash(entries);

                if (!checkSlot(slot, slots.size())) return -1;
                StashSlot stashSlot = slots.get(slot - 1);

                if (stashSlot.isEmpty()) {
                    System.out.println("[empty]");
                } else {
                    System.out.println(new GibbedCodec().encode(stashSlot.getData()));
                }
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    // -------------------------------------------------------------------------

    @CommandLine.Command(name = "set", mixinStandardHelpOptions = true,
            description = "Set the item in a stash slot from a Gibbed code (1-based slot).")
    static class SetItemCommand implements Callable<Integer> {
        @CommandLine.Option(names = {"-f", "--file"},
                description = "Path to profile.bin. If omitted, auto-detected.")
        private Path inputFile;

        @CommandLine.Option(names = {"-o", "--out"},
                description = "Output file (default: overwrite input file).")
        private Path outputFile;

        @CommandLine.Parameters(index = "0", description = "Slot number (1-based).")
        private int slot;

        @CommandLine.Parameters(index = "1", description = "Gibbed code, e.g. BL2(...).")
        private String code;

        @Override
        public Integer call() {
            try {
                Path file = resolveFile(inputFile);
                if (file == null) return -1;
                Path out = outputFile != null ? outputFile : file;

                GibbedCodec codec = new GibbedCodec();
                if (!codec.isValid(code)) {
                    System.err.println("Error: Not a valid BL2 Gibbed code: " + code);
                    return -1;
                }

                ProfileEntries entries = ProfileDataCache.getOrLoadEntries(file);
                StashConverter converter = new StashConverter();
                List<StashSlot> slots = converter.readStash(entries);

                if (!checkSlot(slot, slots.size())) return -1;

                byte[] itemData = codec.decode(code);
                ProfileEntries modified = converter.writeSlot(entries, slot - 1, itemData);
                writeEntries(modified, out);
                ProfileDataCache.invalidate(out);

                System.out.printf("Slot %d  set to  %s%n", slot, codec.encode(itemData));
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                return -1;
            }
        }
    }

    // -------------------------------------------------------------------------

    @CommandLine.Command(name = "add", mixinStandardHelpOptions = true,
            description = "Add an item to the first empty stash slot.")
    static class AddCommand implements Callable<Integer> {
        @CommandLine.Option(names = {"-f", "--file"},
                description = "Path to profile.bin. If omitted, auto-detected.")
        private Path inputFile;

        @CommandLine.Option(names = {"-o", "--out"},
                description = "Output file (default: overwrite input file).")
        private Path outputFile;

        @CommandLine.Parameters(index = "0", description = "Gibbed code, e.g. BL2(...).")
        private String code;

        @Override
        public Integer call() {
            try {
                Path file = resolveFile(inputFile);
                if (file == null) return -1;
                Path out = outputFile != null ? outputFile : file;

                GibbedCodec codec = new GibbedCodec();
                if (!codec.isValid(code)) {
                    System.err.println("Error: Not a valid BL2 Gibbed code: " + code);
                    return -1;
                }

                ProfileEntries entries = ProfileDataCache.getOrLoadEntries(file);
                StashConverter converter = new StashConverter();
                List<StashSlot> slots = converter.readStash(entries);
                int emptyIndex = converter.findFirstEmptySlot(slots);

                if (emptyIndex == -1) {
                    System.err.println("Error: Stash is full (" + slots.size() + " slot(s), none empty).");
                    return -1;
                }

                byte[] itemData = codec.decode(code);
                ProfileEntries modified = converter.writeSlot(entries, emptyIndex, itemData);
                writeEntries(modified, out);
                ProfileDataCache.invalidate(out);

                System.out.printf("Slot %d  set to  %s%n", emptyIndex + 1, codec.encode(itemData));
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                return -1;
            }
        }
    }

    // -------------------------------------------------------------------------

    @CommandLine.Command(name = "clear", mixinStandardHelpOptions = true,
            description = "Clear a stash slot (removes the item, keeps the slot).")
    static class ClearCommand implements Callable<Integer> {
        @CommandLine.Option(names = {"-f", "--file"},
                description = "Path to profile.bin. If omitted, auto-detected.")
        private Path inputFile;

        @CommandLine.Option(names = {"-o", "--out"},
                description = "Output file (default: overwrite input file).")
        private Path outputFile;

        @CommandLine.Parameters(index = "0", description = "Slot number (1-based).")
        private int slot;

        @Override
        public Integer call() {
            try {
                Path file = resolveFile(inputFile);
                if (file == null) return -1;
                Path out = outputFile != null ? outputFile : file;

                ProfileEntries entries = ProfileDataCache.getOrLoadEntries(file);
                StashConverter converter = new StashConverter();
                List<StashSlot> slots = converter.readStash(entries);

                if (!checkSlot(slot, slots.size())) return -1;

                if (slots.get(slot - 1).isEmpty()) {
                    System.out.println("Slot " + slot + " is already empty.");
                    return 0;
                }

                ProfileEntries modified = converter.writeSlot(entries, slot - 1, StashConverter.EMPTY_SLOT_DATA);
                writeEntries(modified, out);
                ProfileDataCache.invalidate(out);

                System.out.println("Slot " + slot + " cleared.");
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                return -1;
            }
        }
    }

    // -------------------------------------------------------------------------

    private static Path resolveFile(Path explicit) {
        if (explicit != null) return explicit;
        return ProfileAutoDetector.detectProfilePath().orElseGet(() -> {
            System.err.println("Error: Could not auto-detect profile.bin. Specify the path with -f <file>.");
            return null;
        });
    }

    private static boolean checkSlot(int slot, int count) {
        if (count == 0) {
            System.err.println("Error: No stash slots found in profile.");
            return false;
        }
        if (slot < 1 || slot > count) {
            System.err.printf("Error: Slot %d out of range (stash has %d slot(s), use 1–%d).%n",
                    slot, count, count);
            return false;
        }
        return true;
    }

    private static void writeEntries(ProfileEntries entries, Path outputFile) throws IOException {
        EntriesContainerFormat format = ProfileHandlerUtil.detectEntriesFormat(outputFile);
        try (OutputStreamSupplier.CloseableOutputStreamSupplier out =
                     ProfileHandlerUtil.newFileOutputSupplier(outputFile)) {
            new ProfileEntryDataHandler().writeEntries(entries, out, format);
        }
    }
}
