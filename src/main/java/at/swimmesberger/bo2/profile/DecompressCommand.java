package at.swimmesberger.bo2.profile;

import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "decompress", mixinStandardHelpOptions = true,
        version = "decompress 1.0",
        description = "Decompresses a compressed BO2 profile file.")
public class DecompressCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", arity = "1",
            description = "The input file to decompress.")
    private Path inputFile;
    @CommandLine.Option(names = {"-o", "--out"}, description = "Output file (default: print to console)")
    private Path outputFile;

    @Override
    public Integer call() throws Exception {
        ProfileDataHandler profileDataHandler = new ProfileDataHandler();
        try {
            if (this.outputFile == null) {
                profileDataHandler.decompress(this.inputFile, System.out);
            } else {
                profileDataHandler.decompress(this.inputFile, this.outputFile);
            }
            return 0;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return -1;
        }
    }
}
