package ca.jrvs.apps;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * StreamJavaGrepImp is an implementation of the StreamJavaGrep interface.
 * It provides methods to process files, search for patterns, and write the matching lines to an output file.
 */
public class StreamJavaGrepImp implements StreamJavaGrep {

  final static Logger logger = LoggerFactory.getLogger(JavaGrepImp.class);

  private String regex;
  private String rootPath;
  private String outFile;

  /**
   * Main method to execute the StreamJavaGrepImp program.
   *
   * @param args command line arguments: regex, rootPath, outFile.
   */
  public static void main(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: StreamJavaGrepImp regex rootPath outFile");
    }

    BasicConfigurator.configure();

    StreamJavaGrepImp streamJavaGrepImp = new StreamJavaGrepImp();
    streamJavaGrepImp.setRegex(args[0]);
    streamJavaGrepImp.setRootPath(args[1]);
    streamJavaGrepImp.setOutFile(args[2]);

    try {
      streamJavaGrepImp.process();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  /**
   * Get the regular expression pattern.
   *
   * @return the regex pattern.
   */
  public String getRegex() {
    return this.regex;
  }

  /**
   * Set the regular expression pattern.
   *
   * @param regex the regex pattern to set.
   */
  public void setRegex(String regex) {
    this.regex = regex;
  }

  /**
   * Get the output file path.
   *
   * @return the output file path.
   */
  public String getOutFile() {
    return this.outFile;
  }

  /**
   * Set the output file path.
   *
   * @param outFile the output file path to set.
   */
  public void setOutFile(String outFile) {
    this.outFile = outFile;
  }

  /**
   * Get the root directory path.
   *
   * @return the root directory path.
   */
  public String getRootPath() {
    return this.rootPath;
  }

  /**
   * Set the root directory path.
   *
   * @param rootPath the root directory path to set.
   */
  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  /**
   * Processes the files in the specified root directory and its subdirectories, searching for lines that match the pattern.
   * Matching lines are written to the output file.
   *
   * @throws IOException if an I/O error occurs while reading or writing files.
   */
  @Override
  public void process() throws IOException {
    // Logging the rootPath before calling listFiles()
    logger.info("Root Path: " + this.getRootPath());

    try (PrintWriter writer = new PrintWriter(new FileWriter(this.getOutFile()))) {
      listFiles(this.getRootPath())
          .flatMap(this::readLines)
          .filter(this::containsPattern)
          .forEach(line -> {
            writer.println(line);
            writer.flush();
          });
    }

    // Logging the completion of the process
    logger.info("StreamJavaGrepImp process completed");
  }

  /**
   * Recursively lists all files in the specified root directory and its subdirectories.
   *
   * @param rootDir the root directory to search for files.
   * @return a stream of files found in the root directory and its subdirectories.
   */
  @Override
  public Stream<File> listFiles(String rootDir) {
    File directory = new File(rootDir);

    // Logging the directory being processed
    logger.debug("Processing directory: " + directory.getAbsolutePath());

    if (directory.isDirectory()) {
      return Stream.of(Objects.requireNonNull(directory.listFiles()))
          .sorted()
          .flatMap(file -> file.isFile() ? Stream.of(file) : listFiles(file.getAbsolutePath()));
    }

    return Stream.empty();
  }

  /**
   * Reads all lines from the specified input file.
   *
   * @param inputFile the input file to read.
   * @return a stream of lines read from the input file.
   * @throws NullPointerException if the input file is null.
   */
  @Override
  public Stream<String> readLines(File inputFile) {
    if (inputFile == null) {
      logger.error("Input file cannot be null");
      return Stream.empty();
    }

    try {
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      return reader.lines().onClose(() -> {
        try {
          reader.close();
        } catch (IOException e) {
          logger.error("Error closing file reader", e);
        }
      });
    } catch (IOException e) {
      logger.error("Error reading lines from file: " + inputFile.getAbsolutePath(), e);
      return Stream.empty();
    }
  }

  /**
   * Checks if the specified line contains a pattern match.
   *
   * @param line the line to check for a pattern match.
   * @return true if the line contains a pattern match, false otherwise.
   */
  @Override
  public boolean containsPattern(String line) {
    if (line == null) {
      return false;
    }
    Pattern pattern = Pattern.compile(this.getRegex(), Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(line);
    return matcher.find();
  }

  /**
   * Writes the specified lines to an output file.
   *
   * @param lines the lines to write to the output file.
   * @throws IOException if an I/O error occurs while writing to the file.
   */
  @Override
  public void writeToFile(Stream<String> lines) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(this.getOutFile()))) {
      lines.forEach(writer::println);
    }
  }
}
