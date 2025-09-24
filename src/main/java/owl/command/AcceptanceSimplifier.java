package owl.command;

import owl.thirdparty.picocli.CommandLine.Option;

public final class AcceptanceSimplifier {

    @Option(
      names = {"--skip-acceptance-simplifier"},
      description = "Bypass the automatic simplification of automata acceptance conditions."
    )
    public boolean skipAcceptanceSimplifier = false;

  }