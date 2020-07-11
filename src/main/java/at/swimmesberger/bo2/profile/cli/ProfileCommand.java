package at.swimmesberger.bo2.profile.cli;

import picocli.CommandLine;

@CommandLine.Command(
        subcommands = {
                CompressCommand.class,
                DecompressCommand.class,
                WriteCommand.class
        }
)
public class ProfileCommand {
}
