package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.entity.ProfileDataHandler;
import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "set", mixinStandardHelpOptions = true,
        version = "set 1.0",
        description = "Sets a certain value in the profiles file.")
public class SetCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", arity = "1",
            description = "The input file to change.")
    private Path inputFile;
    @CommandLine.Parameters(index = "1", arity = "1",
            description = "The value type to change ((available: ${COMPLETION-CANDIDATES}).")
    private ProfileDataValueType valueType;
    @CommandLine.Parameters(index = "2", arity = "1",
            description = "The value to set to.")
    private String value;
    @CommandLine.Option(names = {"-o", "--out"}, description = "Output file (default: write to input file)")
    private Path outputFile;

    @Override
    public Integer call() {
        ProfileDataHandler dataHandler = new ProfileDataHandler();
        try {
            Path outputFile = this.outputFile;
            if (outputFile == null) {
                outputFile = this.inputFile;
            }
            dataHandler.setValue(this.inputFile, outputFile, this.valueType, this.value);
            return 0;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return -1;
        }
    }
}
