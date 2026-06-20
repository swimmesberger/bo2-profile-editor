package at.swimmesberger.bo2.profile.cli;

import picocli.CommandLine;

@CommandLine.Command(
        subcommands = {
                BackupCommand.class,
                UndoCommand.class,
                ConvertCommand.class,
                SetCommand.class,
                GetCommand.class,
                ChangeProfileCommand.class
        }
)
public class MainProfileCommand {
}
