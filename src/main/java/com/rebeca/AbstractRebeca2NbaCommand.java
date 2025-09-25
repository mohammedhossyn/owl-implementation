package com.rebeca;

import owl.automaton.acceptance.BuchiAcceptance;
import owl.automaton.acceptance.GeneralizedBuchiAcceptance;
import owl.thirdparty.picocli.CommandLine;
import owl.thirdparty.picocli.CommandLine.Option;
import owl.translations.LtlTranslationRepository;
import owl.translations.LtlTranslationRepository.LtlToNbaTranslation;

public abstract class AbstractRebeca2NbaCommand
      extends AbstractRebeca2AutomatonCommand<BuchiAcceptance, GeneralizedBuchiAcceptance> {

    @Option(
        names = {"-t", "--translation"},
        description = {
            LIST_AVAILABLE_TRANSLATIONS,
            "EKS20: " + LtlTranslationRepository.LtlToNbaTranslation.EKS20_DESCRIPTION
        },
        defaultValue = "EKS20",
        showDefaultValue = CommandLine.Help.Visibility.NEVER
    )
    private LtlToNbaTranslation translation = LtlToNbaTranslation.DEFAULT;

    @Override
    protected final LtlToNbaTranslation translation() {
      return translation;
    }
  }