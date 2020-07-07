package at.swimmesberger.bo2.profile;

import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "compress", mixinStandardHelpOptions = true,
        version = "compress 1.0",
        description = "Compresses a decompressed BO2 profile file.")
public class CompressCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", arity = "1",
            description = "The input file to compress.")
    private Path inputFile;
    @CommandLine.Option(names = { "-o", "--out" }, description = "Output file (default: print to console)")
    private Path outputFile;

    @Override
    public Integer call() throws Exception {
        ProfileDataHandler profileDataHandler = new ProfileDataHandler();
        try {
            if (this.outputFile == null) {
                profileDataHandler.compress(this.inputFile, System.out);
            } else {
                profileDataHandler.compress(this.inputFile, this.outputFile);
            }
            return 0;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return -1;
        }
    }
}
