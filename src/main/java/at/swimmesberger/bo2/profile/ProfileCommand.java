package at.swimmesberger.bo2.profile;

import picocli.CommandLine;

@CommandLine.Command(
        subcommands = {
                CompressCommand.class,
                DecompressCommand.class
        }
)
public class ProfileCommand {
}
