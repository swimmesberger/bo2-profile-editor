package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.entity.ProfileDataHandler;
import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "get", mixinStandardHelpOptions = true,
        version = "get 1.0",
        description = "Gets a certain value in the profiles file.")
public class GetCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", arity = "1",
            description = "The input file to change.")
    private Path inputFile;
    @CommandLine.Parameters(index = "1", arity = "1",
            description = "The value type to change ((available: ${COMPLETION-CANDIDATES}).")
    private ProfileDataValueType valueType;

    @Override
    public Integer call() {
        ProfileDataHandler dataHandler = new ProfileDataHandler();
        try {
            dataHandler.getValue(this.inputFile, System.out, this.valueType);
            return 0;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return -1;
        }
    }
}
