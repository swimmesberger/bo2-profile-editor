package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.ContainerFormat;
import at.swimmesberger.bo2.profile.ProfileDataHandler;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "write", mixinStandardHelpOptions = true,
        version = "write 1.0",
        description = "Writes and converts the input data into the output file with the passed format.")
public class WriteCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", arity = "1",
            description = "The input file to print.")
    private Path inputFile;
    @CommandLine.Option(names = { "-if", "--in-format" }, description = "Input format (available: ${COMPLETION-CANDIDATES} default: COMPRESSED_LZO)", defaultValue = "COMPRESSED_LZO")
    private ContainerFormat inputFormat;
    @CommandLine.Option(names = { "-o", "--out" }, description = "Output file (default: print to console)")
    private Path outputFile;
    @CommandLine.Option(names = { "-of", "--out-format" }, description = "Output format (available: ${COMPLETION-CANDIDATES} default: TABLE)", defaultValue = "TABLE")
    private ContainerFormat outputFormat;

    @Override
    public Integer call() throws Exception {
        ProfileDataHandler profileDataHandler = new ProfileDataHandler();
        try {
            if (this.outputFile == null) {
                profileDataHandler.printEntries(this.inputFile, this.inputFormat, System.out, this.outputFormat);
                System.out.flush();
            } else {
                profileDataHandler.printEntries(this.inputFile, this.inputFormat, this.outputFile, this.outputFormat);
            }
            return 0;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return -1;
        }
    }
}
