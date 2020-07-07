package at.swimmesberger.bo2.profile;

import picocli.CommandLine;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int exitCode = new CommandLine(new ProfileCommand()).execute(args);
        System.exit(exitCode);
    }
}
