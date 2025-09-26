package com;

import com.rebeca.ExecutionExceptionHandler;
import com.rebeca.Rebeca2nbaCommand;
import owl.automaton.Automaton;
import owl.command.OwlCommand;
import owl.thirdparty.picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

public class CommandRunner {

    interface Command{
        String REBECA_TO_NBA = "rebeca2nba";
    }

    public static List<Automaton<?, ?>> rebecaToNba(String rebecaFile, String propertyFile, Boolean print){
        String[] args = new String[]{Command.REBECA_TO_NBA,
                "-i", rebecaFile,
                "-i", propertyFile,
                "-p", print != null && print ? "true" : "false",
                "--run-in-non-native-mode"};
        OwlCommand owlCommand = new OwlCommand(args);
        CommandLine cmd = new CommandLine(owlCommand).addSubcommand(Rebeca2nbaCommand.class)
                .setExecutionExceptionHandler(new ExecutionExceptionHandler());

        int exitCode = cmd.execute(args);
        if (exitCode != 0){
            throw new RuntimeException();
        }
        CommandLine sub = cmd.getParseResult().subcommand().commandSpec().commandLine();
        List<Automaton<?, ?>> automatons;
        if (sub.getCommand() instanceof Rebeca2nbaCommand rebecaCmd) {
            automatons = rebecaCmd.getAutomatons();
        } else {
            throw new RuntimeException("Method is not rebeca");
        }
        return automatons;
    }
}
