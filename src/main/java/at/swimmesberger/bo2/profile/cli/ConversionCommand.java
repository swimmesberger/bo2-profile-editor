package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.ContainerFormat;
import at.swimmesberger.bo2.profile.ProfileEntryDataHandler;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "convert", mixinStandardHelpOptions = true,
        version = "convert 1.0",
        description = "Converts the input data into the output file with the passed format.")
public class ConversionCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", arity = "1",
            description = "The input file to print.")
    private Path inputFile;
    @CommandLine.Option(names = {"-if", "--in-format"}, description = "Input format (available: ${COMPLETION-CANDIDATES} default: COMPRESSED_LZO)")
    private ContainerFormat inputFormat;
    @CommandLine.Option(names = {"-o", "--out"}, description = "Output file (default: print to console)")
    private Path outputFile;
    @CommandLine.Option(names = {"-of", "--out-format"}, description = "Output format (available: ${COMPLETION-CANDIDATES} default: TABLE)")
    private ContainerFormat outputFormat;

    @Override
    public Integer call() {
        ProfileEntryDataHandler profileEntryDataHandler = new ProfileEntryDataHandler();
        try {
            if (this.outputFile == null) {
                ContainerFormat outputFormat = this.outputFormat;
                if(outputFormat == null) {
                    outputFormat = ContainerFormat.TABLE;
                }
                profileEntryDataHandler.convertEntries(this.inputFile, this.inputFormat, System.out, outputFormat);
                System.out.flush();
            } else {
                profileEntryDataHandler.convertEntries(this.inputFile, this.inputFormat, this.outputFile, this.outputFormat);
            }
            return 0;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return -1;
        }
    }
}
