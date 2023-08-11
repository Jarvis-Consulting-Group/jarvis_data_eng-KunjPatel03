package ca.jrvs.apps;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * StreamJavaGrep is an interface that defines the contract for a Java implementation of the grep command.
 * It provides methods to process files, search for patterns, and write the matching lines to an output file.
 */
public interface StreamJavaGrep {

  /**
   * Processes the files in the specified root directory and its subdirectories, searching for lines that match a pattern.
   * Matching lines are stored in memory for further processing.
   *
   * @throws IOException if an I/O error occurs while reading the files.
   */
  void process() throws IOException;

  /**
   * Recursively lists all files in the specified root directory and its subdirectories.
   *
   * @param rootDir the root directory to search for files.
   * @return a stream of files found in the root directory and its subdirectories.
   */
  Stream<File> listFiles(String rootDir);

  /**
   * Reads all lines from the specified input file.
   *
   * @param inputFile the input file to read.
   * @return a stream of lines read from the input file.
   * @throws IOException if an I/O error occurs while reading the file.
   */
  Stream<String> readLines(File inputFile) throws IOException;

  /**
   * Checks if the specified line contains a pattern match.
   *
   * @param line the line to check for a pattern match.
   * @return true if the line contains a pattern match, false otherwise.
   */
  boolean containsPattern(String line);

  /**
   * Writes the specified lines to an output file.
   *
   * @param lines the lines to write to the output file.
   * @throws IOException if an I/O error occurs while writing to the file.
   */
  void writeToFile(Stream<String> lines) throws IOException;

}
