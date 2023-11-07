package miner;

import java.io.PrintStream;
import org.jetbrains.annotations.NotNull;
import parser.ESMJsonParser;

/**
 * Interface for all Data Miners
 *
 * @author Eric Karlson
 */
public interface IDataMiner {
  /**
   * Entry point for running the data miner
   *
   * @param parser The {@link ESMJsonParser} that contains all the raw ESM Record Data
   * @param output A {@link PrintStream} to use for exporting the mined data
   */
  void run(@NotNull ESMJsonParser parser, @NotNull PrintStream output);
}
