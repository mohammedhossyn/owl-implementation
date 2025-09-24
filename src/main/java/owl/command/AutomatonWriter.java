package owl.command;

import owl.automaton.Automaton;
import owl.automaton.Views;
import owl.automaton.hoa.HoaWriter;
import owl.thirdparty.jhoafparser.consumer.HOAConsumerException;
import owl.thirdparty.jhoafparser.consumer.HOAIntermediateStoreAndManipulate;
import owl.thirdparty.jhoafparser.owl.extensions.HOAConsumerPrintFixed;
import owl.thirdparty.jhoafparser.owl.extensions.ToStateAcceptanceFixed;
import owl.thirdparty.picocli.CommandLine.Option;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class AutomatonWriter {

    @Option(
      names = { "-o", "--output-file" },
      description = "Output file (default: write to stdout). If '-' is specified, then the tool "
        + "writes to stdout."
    )
    private String automatonFile = null;

    @Option(
      names = {"--complete"},
      description = "Output an automaton with a complete transition relation."
    )
    public boolean complete = false;

    @Option(
      names = {"--dry-run"},
      description = "Do not output resulting automaton."
    )
    private boolean dryRun = false;

    @Option(
      names = {"--state-acceptance"},
      description = "Output an automaton with a state-based acceptance condition instead of one "
        + "with a transition-based acceptance condition. For this the acceptance marks of edges "
        + "are pushed onto the successor states. However, this simple procedure might yield "
        + "suboptimal results."
    )
    private boolean stateAcceptance = false;

    @Option(
      names = {"--state-labels"},
      description = "Annotate each state of the automaton with the 'toString()' method."
    )
    private boolean stateLabels = false;

    public class Sink implements AutoCloseable {

      private final BufferedWriter writer;
      private final String subcommand;
      private final List<String> subcommandArgs;

      private Sink(String subcommand, List<String> subcommandArgs) throws IOException {
        // Normalise for '-' representing output to stdout.
        if ("-".equals(automatonFile)) {
          automatonFile = null;
        }

        if (automatonFile == null) {
          writer = new BufferedWriter(new OutputStreamWriter(System.out));
        } else {
          writer = Files.newBufferedWriter(Path.of(automatonFile));
        }

        this.subcommand = subcommand;
        this.subcommandArgs = List.copyOf(subcommandArgs);
      }

      @SuppressWarnings("PMD.AvoidReassigningParameters")
      public void accept(Automaton<?, ?> automaton, String automatonName)
        throws HOAConsumerException, IOException {

        if (dryRun) {
          return;
        }

        if (complete && !automaton.is(Automaton.Property.COMPLETE)) {
          automaton = Views.complete(automaton);
        }

        var printer = new HOAConsumerPrintFixed(writer);

        // Replace this by a fixed version to preserve owl header extension in case of state
        // acceptance.
        var wrappedPrinter = stateAcceptance
          ? new HOAIntermediateStoreAndManipulate(printer, new ToStateAcceptanceFixed())
          : printer;

        HoaWriter.write(
          automaton,
          wrappedPrinter,
          stateLabels,
          subcommand,
          subcommandArgs,
          automatonName);

        writer.flush();
      }

      @Override
      public void close() throws IOException {
        writer.close();
      }
    }

    public Sink sink(String subcommand, List<String> subcommandArgs) throws IOException {
      return new Sink(subcommand, subcommandArgs);
    }
  }