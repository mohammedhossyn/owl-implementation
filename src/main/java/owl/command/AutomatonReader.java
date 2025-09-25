package owl.command;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.UncheckedExecutionException;
import owl.automaton.Automaton;
import owl.automaton.acceptance.EmersonLeiAcceptance;
import owl.automaton.acceptance.OmegaAcceptanceCast;
import owl.automaton.hoa.HoaReader;
import owl.bdd.FactorySupplier;
import owl.thirdparty.jhoafparser.parser.generated.ParseException;
import owl.thirdparty.picocli.CommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class AutomatonReader {

    @CommandLine.Option(
      names = { "-i", "--input-file" },
      description = "Input file (default: read from stdin). If '-' is specified, then the tool "
        + "reads from stdin. This option is repeatable."
    )
    private String[] automatonFile = { "-" };

    <A extends EmersonLeiAcceptance> Stream<Automaton<Integer, ? extends A>>
      source(Class<A> acceptanceClass) {

      return Stream.of(automatonFile).flatMap(file -> {
        try (var reader = "-".equals(file)
          ? new BufferedReader(new InputStreamReader(System.in))
          : Files.newBufferedReader(Path.of(file))) {

          List<Automaton<Integer, ? extends A>> automata = new ArrayList<>();

          // Warning: the 'readStream'-method reads until the reader is exhausted and thus this
          // method blocks in while reading from stdin.
          HoaReader.readStream(reader,
            FactorySupplier.defaultSupplier()::getBddSetFactory,
            null,
            automaton -> {
              Preconditions.checkArgument(
                OmegaAcceptanceCast.isInstanceOf(automaton.acceptance().getClass(),
                  acceptanceClass),
                String.format("Expected %s, but got %s.", acceptanceClass, automaton.acceptance()));
              automata.add(OmegaAcceptanceCast.cast(automaton, acceptanceClass));
            });

          return automata.stream();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        } catch (ParseException e) {
          throw new UncheckedExecutionException(e);
        }
      });
    }
  }