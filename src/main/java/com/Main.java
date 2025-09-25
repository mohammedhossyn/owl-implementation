package com;

import com.rebeca.ExecutionExceptionHandler;
import com.rebeca.Rebeca2nbaCommand;
import owl.automaton.Automaton;
import owl.command.OwlCommand;
import owl.thirdparty.picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        args = new String[]{"rebeca2nba",
                "-i", "DiningPhilosophers.rebeca",
                "-i", "DiningPhilosophers.property",
                "-p", "false",
                "--run-in-non-native-mode"};
        OwlCommand owlCommand = new OwlCommand(args);
        CommandLine cmd = new CommandLine(owlCommand).addSubcommand(Rebeca2nbaCommand.class)
                .setExecutionExceptionHandler(new ExecutionExceptionHandler());

        int exitCode = cmd.execute(args);

        CommandLine sub = cmd.getParseResult().subcommand().commandSpec().commandLine();
        List<Automaton<?, ?>> automatons = new ArrayList<>();
        if (sub.getCommand() instanceof Rebeca2nbaCommand rebecaCmd) {
            automatons = rebecaCmd.getAutomatons();
        }
    }
}