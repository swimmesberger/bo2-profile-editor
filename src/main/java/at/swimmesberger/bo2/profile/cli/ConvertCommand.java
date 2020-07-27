package at.swimmesberger.bo2.profile.cli;

import at.swimmesberger.bo2.profile.entity.CombinedContainerFormat;
import at.swimmesberger.bo2.profile.entity.ProfileDataHandler;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "convert", mixinStandardHelpOptions = true,
        version = "convert 1.0",
        description = "Converts the input data into the output file with the passed format.")
public class ConvertCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", arity = "1",
            description = "The input file to print.")
    private Path inputFile;
    @CommandLine.Option(names = {"-if", "--in-format"}, description = "Input format (available: ${COMPLETION-CANDIDATES} default: COMPRESSED_LZO)")
    private CombinedContainerFormat inputFormat;
    @CommandLine.Option(names = {"-o", "--out"}, description = "Output file (default: print to console)")
    private Path outputFile;
    @CommandLine.Option(names = {"-of", "--out-format"}, description = "Output format (available: ${COMPLETION-CANDIDATES} default: TABLE_DATA)")
    private CombinedContainerFormat outputFormat;

    @Override
    public Integer call() {
        ProfileDataHandler profileEntryDataHandler = new ProfileDataHandler();
        try {
            if (this.outputFile == null) {
                CombinedContainerFormat outputFormat = this.outputFormat;
                if(outputFormat == null) {
                    outputFormat = CombinedContainerFormat.TABLE_DATA;
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
