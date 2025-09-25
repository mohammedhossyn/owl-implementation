package com.rebeca;

import owl.automaton.acceptance.BuchiAcceptance;
import owl.automaton.acceptance.GeneralizedBuchiAcceptance;
import owl.command.MiscCommands;
import owl.thirdparty.picocli.CommandLine;
import owl.thirdparty.picocli.CommandLine.Command;

@Command(
        name = "rebeca2nba",
        description = {
                "Translate a rebeca language and property into a.",
                "Usage Examples:",
                "  owl rebeca2nba ",
                "  owl rebeca2nba -i model-input-file -i property-input-file -p true",
                MiscCommands.BibliographyCommand.HOW_TO_USE
        }
)
public final class Rebeca2nbaCommand extends AbstractRebeca2NbaCommand {

    @CommandLine.Option(
            names = {"-p", "--print"},
            description = "Input file name."
    )
    private String print = null;

    @Override
    protected Class<? extends GeneralizedBuchiAcceptance> acceptanceClass() {
        return BuchiAcceptance.class;
    }
}