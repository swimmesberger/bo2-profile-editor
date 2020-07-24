package at.swimmesberger.bo2.profile.cli;

import picocli.CommandLine;

@CommandLine.Command(
        subcommands = {
                ConversionCommand.class,
                SetCommand.class
        }
)
public class MainProfileCommand {
}
