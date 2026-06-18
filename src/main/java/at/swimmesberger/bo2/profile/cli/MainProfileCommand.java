package at.swimmesberger.bo2.profile.cli;

import picocli.CommandLine;

@CommandLine.Command(
        subcommands = {
                ConvertCommand.class,
                SetCommand.class,
                GetCommand.class,
                ChangeFolderCommand.class
        }
)
public class MainProfileCommand {
}
