package at.swimmesberger.bo2.profile.cli;

import picocli.CommandLine;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "set", mixinStandardHelpOptions = true,
        version = "set 1.0",
        description = "Sets a certain value in the profiles file.")
public class SetCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", arity = "1",
            description = "The input file to print.")
    private Path inputFile;
    @CommandLine.Option(names = {"-o", "--out"}, description = "Output file (default: print to console)")
    private Path outputFile;

    @Override
    public Integer call() {
        return 0;
    }
}
