package com.rebeca;

import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.propertycompiler.PropertyCompiler;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.Definition;
import org.rebecalang.compiler.propertycompiler.generalrebeca.objectmodel.PropertyModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import owl.ltl.LabelledFormula;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Rebeca {

    public static Stream<LabelledFormula> rebecaToLTL(String rebeceFilePath, String propertyFilePath, Boolean print) {
        try (var ctx = new AnnotationConfigApplicationContext(CompilerConfig.class)) {
            RebecaModelCompiler modelCompiler = ctx.getBean(RebecaModelCompiler.class);
            PropertyCompiler propertyCompiler = ctx.getBean(PropertyCompiler.class);
            ExceptionContainer exceptions = ctx.getBean(ExceptionContainer.class);


            File model = new File(rebeceFilePath);
            File property = new File(propertyFilePath);

            Set<CompilerExtension> extension = new HashSet<>();
            Pair<RebecaModel, SymbolTable> modelCompilationResult = modelCompiler.compileRebecaFile(model, extension, CoreVersion.CORE_2_0);

            PropertyModel propertyModel = propertyCompiler.compilePropertyFile(property, modelCompilationResult.getFirst(), extension);

            if(print)
                RebecaPropertyPrinter.printDetailedPropertyModelInformation(propertyModel);

            // Reset atomic propositions for fresh conversion
            RebecaExpressionConverter.resetAtomicPropositions();

            // Convert PropertyModel definitions directly to LabelledFormulas
            List<LabelledFormula> labelledFormulas = new ArrayList<>();

            if (propertyModel.getDefinitions() != null) {
                for (Definition definition : propertyModel.getDefinitions()) {
                    try {
                        LabelledFormula labelledFormula = RebecaExpressionConverter.convertDefinitionToLabelledFormula(definition);
                        labelledFormulas.add(labelledFormula);
                        // System.out.println("Converted definition '" + definition.getName() + "' to formula: " + labelledFormula.formula());
                    } catch (Exception e) {
                        System.err.println("Error converting definition '" + definition.getName() + "': " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }


            return labelledFormulas.stream();

        } catch (Exception e) {
            System.err.println("Error in rebecaToLTL: " + e.getMessage());
            e.printStackTrace();
            return Stream.empty();
        }
    }

}
