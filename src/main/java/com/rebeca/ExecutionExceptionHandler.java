package com.rebeca;

import com.google.common.util.concurrent.UncheckedExecutionException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import owl.thirdparty.jhoafparser.parser.generated.ParseException;
import owl.thirdparty.picocli.CommandLine;

import java.io.UncheckedIOException;
import java.nio.file.NoSuchFileException;

public class ExecutionExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    @Override
    public int handleExecutionException(
      Exception ex, CommandLine commandLine, CommandLine.ParseResult parseResult) {

      return handleExecutionException((Throwable) ex, commandLine, parseResult);
    }

    public int handleExecutionException(
      Throwable ex, CommandLine commandLine, CommandLine.ParseResult parseResult) {

      // Unpack unchecked exceptions.
      if (ex instanceof UncheckedIOException || ex instanceof UncheckedExecutionException) {
        return handleExecutionException(ex.getCause(), commandLine, parseResult);
      }

      if (ex instanceof NoSuchFileException noSuchFileException) {

        var file = noSuchFileException.getFile();
        var reason = noSuchFileException.getReason();

        if (reason == null) {
          System.err.printf("Could not access file \"%s\".", file);
        } else {
          System.err.printf(
            "Could not access file \"%s\", because of the following reason: %s", file, reason);
        }
      } else if (ex instanceof IllegalArgumentException) {
        if (ex.getCause() instanceof RecognitionException
          || ex.getCause() instanceof ParseCancellationException) {
          System.err.printf("Could not parse linear temporal logic formula: %s", ex.getMessage());
        } else {
          ex.printStackTrace(System.err);
        }
      } else if (ex instanceof ParseException) {
        System.err.printf(
          "Could not parse HOA automaton due to the following problem:%n%s",
          ex.getMessage());
      } else {
        ex.printStackTrace(System.err);
      }

      // Ensure that error messages are terminated by a new-line.
      System.err.println();
      return -1;
    }
  }