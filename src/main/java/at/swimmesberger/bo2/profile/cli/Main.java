package at.swimmesberger.bo2.profile.cli;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new MainProfileCommand()).execute(args);
        System.exit(exitCode);
    }
}
