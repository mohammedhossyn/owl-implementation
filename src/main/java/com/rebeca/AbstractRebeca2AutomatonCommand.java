package com.rebeca;

import owl.automaton.Automaton;
import owl.automaton.acceptance.EmersonLeiAcceptance;
import owl.command.*;
import owl.ltl.LabelledFormula;
import owl.thirdparty.picocli.CommandLine;
import owl.thirdparty.picocli.CommandLine.Mixin;
import owl.thirdparty.picocli.CommandLine.Option;
import owl.translations.LtlTranslationRepository;
import owl.translations.LtlTranslationRepository.LtlTranslation;

import java.util.*;

import static owl.translations.LtlTranslationRepository.Option.*;

public abstract class AbstractRebeca2AutomatonCommand
        <L extends A, A extends EmersonLeiAcceptance> extends AbstractOwlSubcommand {

    protected static final String LIST_AVAILABLE_TRANSLATIONS = "The default translation is "
            + "${DEFAULT-VALUE} and the following translations are available: ${COMPLETION-CANDIDATES}.";

    @Mixin
    private FormulaReader formulaReader = null;

    @Mixin
    private AutomatonWriter automatonWriter = null;

    @Mixin
    private FormulaSimplifier formulaSimplifier = null;

    @Mixin
    private AcceptanceSimplifier acceptanceSimplifier = null;

    abstract LtlTranslation<L, A> translation();

    abstract Class<? extends A> acceptanceClass();

    Set<LtlTranslationRepository.Option> extraOptions() {
        return Set.of();
    }

    OptionalInt lookahead() {
        return OptionalInt.empty();
    }

    private List<Automaton<?, ?>> automatons;

    public List<Automaton<?, ?>> getAutomatons() {
        return automatons;
    }

    @Option(
            names = "--skip-translation-portfolio",
            description = "Bypass the portfolio of constructions from [S19, SE20] that directly "
                    + "translates 'simple' fragments of LTL to automata."
    )
    private boolean skipPortfolio = false;


    @Override
    protected int run() throws Exception {
        var translation = translation();
        var acceptanceClass = acceptanceClass();

        var basicOptions = EnumSet.noneOf(LtlTranslationRepository.Option.class);

        if (!formulaSimplifier.skipSimplifier) {
            basicOptions.add(SIMPLIFY_FORMULA);
        }

        if (!acceptanceSimplifier.skipAcceptanceSimplifier) {
            basicOptions.add(SIMPLIFY_AUTOMATON);
        }

        if (automatonWriter.complete) {
            basicOptions.add(COMPLETE);
        }

        if (!skipPortfolio) {
            basicOptions.add(USE_PORTFOLIO_FOR_SYNTACTIC_LTL_FRAGMENTS);
        }

        basicOptions.addAll(extraOptions());

        var subcommand = getClass().getAnnotation(CommandLine.Command.class).name();
        var translator = translation.translation(acceptanceClass, basicOptions, lookahead());


        try (var source = formulaReader.source();
             var sink = automatonWriter.sink(subcommand, rawArgs())) {

            Iterator<LabelledFormula> formulaIterator = source.iterator();
            List<Automaton<?, ?>> automatonList = new ArrayList<>();
            while (formulaIterator.hasNext()) {
                LabelledFormula formula = formulaIterator.next();
                automatonList.add(translator.apply(formula));
                sink.accept(translator.apply(formula), "Automaton for " + formula);
            }
            automatons = automatonList;
        }

        return 0;
    }
}