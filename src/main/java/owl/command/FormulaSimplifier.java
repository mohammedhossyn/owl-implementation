package owl.command;

import owl.thirdparty.picocli.CommandLine.Option;

public final class FormulaSimplifier {

    @Option(
      names = {"--skip-formula-simplifier"},
      description = "Bypass the automatic simplification of formulas."
    )
    public boolean skipSimplifier = false;

  }