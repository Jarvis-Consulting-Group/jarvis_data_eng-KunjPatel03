package ca.jrvs.apps;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Unit tests for the StreamJavaGrepImp class.
 */
public class StreamJavaGrepImpTest {

  private StreamJavaGrepImp streamJavaGrepImp;
  private final String outFile = "OutPutTest.txt";

  private final String testDirectoryPath = "testing";
  private final String testFilePath = "testfile.txt";

  private final Logger logger = LoggerFactory.getLogger(StreamJavaGrepImpTest.class);

  /**
   * Setup method to initialize test resources.
   *
   * @throws IOException if an error occurs during setup.
   */
  @Before
  public void setup() throws IOException {
    BasicConfigurator.configure();

    streamJavaGrepImp = new StreamJavaGrepImp();
    streamJavaGrepImp.setOutFile(outFile);
    Files.createFile(Paths.get(outFile));
  }

  /**
   * Cleanup method to clean up test resources after each test.
   */
  @After
  public void cleanup() {
    // Delete the created file
    deleteFile(testFilePath);
    deleteFile(outFile);
    deleteDirectory(testDirectoryPath);
  }

  /**
   * Test case for containsPattern method with valid input and a matching pattern.
   */
  @Test
  public void testContainsPattern_ValidInput_MatchFound() {
    // Arrange
    streamJavaGrepImp.setRegex("pattern");
    String line = "This is a line containing a pattern.";

    // Act
    boolean result = streamJavaGrepImp.containsPattern(line);

    // Assert
    assertTrue(result);
  }

  /**
   * Test case for containsPattern method with valid input and no matching pattern.
   */
  @Test
  public void testContainsPattern_ValidInput_NoMatchFound() {
    // Arrange
    streamJavaGrepImp.setRegex("pattern");
    String line = "This is a line without a match.";

    // Act
    boolean result = streamJavaGrepImp.containsPattern(line);

    // Assert
    assertFalse(result);
  }

  /**
   * Test case for containsPattern method with null input.
   */
  @Test
  public void testContainsPattern_NullInput() {
    // Arrange
    streamJavaGrepImp.setRegex("pattern");
    String line = null;

    // Act
    boolean result = streamJavaGrepImp.containsPattern(line);

    // Assert
    assertFalse(result);
  }

  /**
   * Test case for containsPattern method with empty input.
   */
  @Test
  public void testContainsPattern_EmptyInput() {
    // Arrange
    streamJavaGrepImp.setRegex("pattern");
    String line = "";

    // Act
    boolean result = streamJavaGrepImp.containsPattern(line);

    // Assert
    assertFalse(result);
  }

  /**
   * Test case for containsPattern method with null regex.
   * Expects NullPointerException to be thrown.
   */
  @Test(expected = NullPointerException.class)
  public void testContainsPattern_NullRegex() {
    // Arrange
    streamJavaGrepImp.setRegex(null);
    String line = "This is a line with a match.";

    // Act
    streamJavaGrepImp.containsPattern(line);
  }

  /**
   * Test case for writeToFile method with valid input.
   * Expects the output file to contain the expected lines.
   */
  @Test
  public void testWriteToFile_ValidInput_Success() throws IOException {
    // Arrange
    List<String> lines = new ArrayList<>();
    lines.add("Line 1");
    lines.add("Line 2");
    lines.add("Line 3");

    // Act
    streamJavaGrepImp.writeToFile(lines.stream());

    // Assert
    List<String> fileContent = readLinesFromFile(outFile);
    assertEquals(lines, fileContent);
  }

  /**
   * Test case for readLines method with a valid file.
   * Expects the lines to be read successfully.
   */
  @Test
  public void testReadLines_ValidFile_Success() throws IOException {
    // Arrange
    // Create a test file with sample lines
    createTestFile("Line 1\nLine 2\nLine 3");

    // Act
    // Read the lines from the test file
    Stream<String> lines = streamJavaGrepImp.readLines(new File(testFilePath));

    // Assert
    // Assert the expected lines
    List<String> lineList = lines.collect(Collectors.toList());
    assertEquals(3, lineList.size());
    assertEquals("Line 1", lineList.get(0));
    assertEquals("Line 2", lineList.get(1));
    assertEquals("Line 3", lineList.get(2));
  }

  /**
   * Test case for readLines method with an empty file.
   * Expects no lines to be read.
   */
  @Test
  public void testReadLines_EmptyFile_Success() throws IOException {
    // Arrange
    // Create an empty test file
    createTestFile("");

    // Act
    // Read the lines from the test file
    Stream<String> lines = streamJavaGrepImp.readLines(new File(testFilePath));

    // Assert
    // Assert that no lines were read
    List<String> lineList = lines.collect(Collectors.toList());
    assertTrue(lineList.isEmpty());
  }

  /**
   * Test case for readLines method with a null file.
   * Expects an empty stream to be returned.
   */
  @Test
  public void testReadLines_NullFile_Success() throws IOException {
    // Arrange
    // Read the lines from a null file
    Stream<String> lines = streamJavaGrepImp.readLines(null);

    // Assert
    // Assert that no lines were read
    List<String> lineList = lines.collect(Collectors.toList());
    assertTrue(lineList.isEmpty());
  }

  /**
   * Test case for readLines method with a nonexistent file.
   * Expects an empty stream to be returned.
   */
  @Test
  public void testReadLines_NonexistentFile_Success() throws IOException {
    // Arrange
    // Read the lines from a nonexistent file
    Stream<String> lines = streamJavaGrepImp.readLines(new File("nonexistent.txt"));

    // Assert
    // Assert that no lines were read
    List<String> lineList = lines.collect(Collectors.toList());
    assertTrue(lineList.isEmpty());
  }

  /**
   * Test case for listFiles method with a valid root directory.
   * Expects the files to be listed successfully.
   */
  @Test
  public void testListFiles_ValidRootDirectory_Success() {
    // Arrange
    // Create a temporary test directory with some files
    String rootDir = createTempDirectory();
    createTempFile(rootDir + "/file1.txt");
    createTempFile(rootDir + "/file2.txt");
    createTempFile(rootDir + "/subdir/file3.txt");

    // Act
    // Invoke the listFiles method
    List<File> files = streamJavaGrepImp.listFiles(rootDir).collect(Collectors.toList());

    // Assert
    // Assert the expected files
    assertEquals(3, files.size());
    assertEquals("file1.txt", files.get(0).getName());
    assertEquals("file2.txt", files.get(1).getName());
    assertEquals("file3.txt", files.get(2).getName());

    // Clean up the temporary test directory
    deleteDirectory(rootDir);
  }

  /**
   * Test case for listFiles method with an empty root directory.
   * Expects no files to be listed.
   */
  @Test
  public void testListFiles_EmptyRootDirectory_Success() {
    // Arrange
    // Create an empty temporary test directory
    String rootDir = createTempDirectory();

    // Act
    // Invoke the listFiles method
    List<File> files = streamJavaGrepImp.listFiles(rootDir).collect(Collectors.toList());

    // Assert
    // Assert that no files were found
    assertEquals(0, files.size());

    // Clean up the temporary test directory
    deleteDirectory(rootDir);
  }

  /**
   * Test case for listFiles method with a nonexistent root directory.
   * Expects no files to be listed.
   */
  @Test
  public void testListFiles_NonexistentRootDirectory_Success() {
    // Arrange
    // Invoke the listFiles method with a nonexistent directory
    List<File> files = streamJavaGrepImp.listFiles("nonexistent-dir").collect(Collectors.toList());

    // Assert
    // Assert that no files were found
    assertEquals(0, files.size());
  }

  /**
   * Test case for process method with no match found.
   * Expects the output file to be empty.
   */
  @Test
  public void testProcess_NoMatchFound_Success() throws IOException {
    // Arrange
    // Create test files
    File file1 = createTempFile(testDirectoryPath + "/file1.txt");
    File file2 = createTempFile(testDirectoryPath + "/file2.txt");
    File file3 = createTempFile(testDirectoryPath + "/file3.txt");

    // Write lines to the files
    writeToFile(file1, "This is a test line.");
    writeToFile(file2, "This is another test line.");
    writeToFile(file3, "This line is good.");

    // Set the regex and rootPath for the StreamJavaGrepImp instance
    streamJavaGrepImp.setRegex("pattern");
    streamJavaGrepImp.setRootPath(testDirectoryPath);

    // Act
    // Invoke the process method
    streamJavaGrepImp.process();

    // Assert
    // Verify that the outFile is empty
    List<String> outFileContent = readLinesFromFile(outFile);
    assertTrue(outFileContent.isEmpty());
  }

  /**
   * Test case for process method with a match found.
   * Expects the output file to contain the matched lines.
   */
  @Test
  public void testProcess_MatchFound_Success() throws IOException {
    // Arrange
    // Create test files
    File file1 = createTempFile(testDirectoryPath + "/file1.txt");
    File file2 = createTempFile(testDirectoryPath + "/file2.txt");
    File file3 = createTempFile(testDirectoryPath + "/file3.txt");

    // Write lines to the files
    writeToFile(file1, "This is a test line containing the pattern.");
    writeToFile(file2, "This is another test line.");
    writeToFile(file3, "This line does contain the pattern.");

    // Set the regex and rootPath for the StreamJavaGrepImp instance
    streamJavaGrepImp.setRegex("pattern");
    streamJavaGrepImp.setRootPath(testDirectoryPath);

    // Act
    // Invoke the process method
    streamJavaGrepImp.process();

    // Assert
    // Verify that the outFile contains the matched lines
    List<String> outFileContent = readLinesFromFile(outFile);
    assertEquals(2, outFileContent.size());
  }

  /**
   * Test case for process method with an empty rootPath.
   * Expects no output to be generated.
   */
  @Test
  public void testProcess_EmptyRootPath_Success() throws IOException {
    // Arrange
    // Set an empty rootPath
    streamJavaGrepImp.setRootPath("");

    // Act
    // Invoke the process method
    streamJavaGrepImp.process();

    // Assert
    // Verify that the outFile is empty
    List<String> outFileContent = readLinesFromFile(outFile);
    assertTrue(outFileContent.isEmpty());
  }

  /**
   * Test case for process method with a nonexistent rootPath.
   * Expects no output to be generated.
   */
  @Test
  public void testProcess_NonexistentRootPath_Success() throws IOException {
    // Arrange
    // Set a nonexistent rootPath
    streamJavaGrepImp.setRootPath("nonexistent-path");

    // Act
    // Invoke the process method
    streamJavaGrepImp.process();

    // Assert
    // Verify that the outFile is empty
    List<String> outFileContent = readLinesFromFile(outFile);
    assertTrue(outFileContent.isEmpty());
  }

  // Helper method to create a temporary directory
  private String createTempDirectory() {
    try {
      File tempDir = File.createTempFile("temp", Long.toString(System.nanoTime()));
      tempDir.delete();
      tempDir.mkdir();
      return tempDir.getAbsolutePath();
    } catch (Exception e) {
      logger.error("Failed to create temporary directory", e);
      throw new AssertionError("Failed to create temporary directory");
    }
  }

  // Helper method to create a temporary file
  private File createTempFile(String filePath) {
    try {
      File tempFile = new File(filePath);
      tempFile.getParentFile().mkdirs();  // Create parent directories if they don't exist
      tempFile.createNewFile();
      return tempFile;
    } catch (Exception e) {
      logger.error("Failed to create temporary file", e);
      throw new AssertionError("Failed to create temporary file");
    }
  }

  // Helper method to write content to a file
  private void writeToFile(File file, String content) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      writer.write(content);
    } catch (IOException e) {
      logger.error("Failed to write to file: " + file.getAbsolutePath(), e);
      throw new AssertionError("Failed to write to file: " + file.getAbsolutePath(), e);
    }
  }

  // Helper method to delete a directory recursively
  private void deleteDirectory(String directoryPath) {
    File directory = new File(directoryPath);
    if (directory.exists()) {
      File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            deleteDirectory(file.getAbsolutePath());
          } else {
            file.delete();
          }
        }
      }
      directory.delete();
    }
  }

  // Helper method to delete a file
  private void deleteFile(String filePath) {
    try {
      Files.deleteIfExists(Paths.get(filePath));
    } catch (IOException e) {
      logger.error("Failed to delete file", e);
      throw new AssertionError("Failed to delete file");
    }
  }

  // Helper method to create a test file with given content
  private void createTestFile(String content) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFilePath))) {
      writer.write(content);
    }
  }

  // Helper method to read lines from a file
  private List<String> readLinesFromFile(String filePath) throws IOException {
    return Files.readAllLines(Paths.get(filePath));
  }
}
