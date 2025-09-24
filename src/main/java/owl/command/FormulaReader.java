package owl.command;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import owl.ltl.LabelledFormula;
import owl.thirdparty.picocli.CommandLine.ArgGroup;
import owl.thirdparty.picocli.CommandLine.Option;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.rebeca.Rebeca.rebecaToLTL;


public final class FormulaReader {
    @Override
    public String toString() {
      return "FormulaReader{" +
              "source=" + source +
              '}';
    }

    @ArgGroup
    private Source source = null;

    public static final class Source {

      @Override
      public String toString() {
        return "Source{" +
                "formula=" + Arrays.toString(formula) +
                '}';
      }

      @Option(
        names = {"-f", "--formula"},
        description = "Use the argument of the option as the input formula. This option is "
          + "repeatable, but cannot be combined with '-i'."
      )
      String[] formula = null;

      @Option(
        names = {"-i", "--input-file"},
        description = "Input file (default: read from stdin). The file is read line-by-line and "
          + "it is assumed that each line contains a formula. Empty lines are skipped. If '-' is "
          + "specified, then the tool reads from stdin. This option is repeatable, but cannot be "
          + "combined with '-f'."
      )
      String[] formulaFile = null;

    }

    Stream<String> stringSource() throws IOException {
      // Default to stdin.
      if (source == null) {
        source = new Source();
        source.formulaFile = new String[]{ "-" };
      }

      Stream<String> stringStream;

      if (source.formulaFile == null) {
        assert source.formula != null;
        stringStream = Stream.of(source.formula);
      } else {
        List<Stream<String>> readerStreams = new ArrayList<>(source.formulaFile.length);

        for (String file : source.formulaFile) {
          BufferedReader reader = "-".equals(file)
            ? new BufferedReader(new InputStreamReader(System.in))
            : Files.newBufferedReader(Path.of(file));

          readerStreams.add(reader.lines().onClose(() -> {
            try {
              reader.close();
            } catch (IOException ex) {
              throw new UncheckedIOException(ex);
            }
          }));
        }

        // This workaround helps against getting stuck while reading from stdin.
        stringStream = readerStreams.size() == 1
          ? readerStreams.get(0)
          : readerStreams.stream().flatMap(Function.identity());
      }

      return stringStream.filter(Predicate.not(String::isBlank));
    }

    public Stream<LabelledFormula> source() {
        return rebecaToLTL(source.formulaFile[0], source.formulaFile[1], false);
    }
  }