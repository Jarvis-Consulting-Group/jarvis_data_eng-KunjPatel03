package ca.jrvs.apps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Unit tests for the JavaGrepImp class.
 */
public class JavaGrepImpTest {

  private JavaGrepImp javaGrepImp;
  private final String outFile = "OutPutTest.txt";

  private final String testDirectoryPath = "testing";
  private final String testFilePath = "testfile.txt";

  private final Logger logger = LoggerFactory.getLogger(JavaGrepImpTest.class);

  /**
   * Setup method to initialize test resources.
   * @throws IOException if an error occurs during setup.
   */
  @Before
  public void setup() throws IOException {
    MockitoAnnotations.openMocks(this);
    javaGrepImp = new JavaGrepImp();
    javaGrepImp.setOutFile(outFile);
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
    javaGrepImp.setRegex("pattern");
    String line = "This is a line containing a pattern.";

    // Act
    boolean result = javaGrepImp.containsPattern(line);

    // Assert
    assertTrue(result);
  }

  /**
   * Test case for containsPattern method with valid input and no matching pattern.
   */
  @Test
  public void testContainsPattern_ValidInput_NoMatchFound() {
    // Arrange
    javaGrepImp.setRegex("pattern");
    String line = "This is a line without a match.";

    // Act
    boolean result = javaGrepImp.containsPattern(line);

    // Assert
    assertFalse(result);
  }

  /**
   * Test case for containsPattern method with null input.
   */
  @Test
  public void testContainsPattern_NullInput() {
    // Arrange
    javaGrepImp.setRegex("pattern");
    String line = null;

    // Act
    boolean result = javaGrepImp.containsPattern(line);

    // Assert
    assertFalse(result);
  }

  /**
   * Test case for containsPattern method with empty input.
   */
  @Test
  public void testContainsPattern_EmptyInput() {
    // Arrange
    javaGrepImp.setRegex("pattern");
    String line = "";

    // Act
    boolean result = javaGrepImp.containsPattern(line);

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
    javaGrepImp.setRegex(null);
    String line = "This is a line with a match.";

    // Act
    javaGrepImp.containsPattern(line);
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

    // Spy on the JavaGrepImp instance
    JavaGrepImp javaGrepImpSpy = spy(javaGrepImp);

    // Act
    javaGrepImpSpy.writeToFile(lines);

    // Assert
    verify(javaGrepImpSpy, times(1)).writeToFile(lines);
    // Read the file content and assert against expected values
    List<String> fileContent = javaGrepImpSpy.readLines(new File(javaGrepImpSpy.getOutFile()));
    assertThat(lines, is(fileContent));
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
    List<String> lines = javaGrepImp.readLines(new File(testFilePath));

    // Assert
    // Assert the expected lines
    assertEquals(3, lines.size());
    assertEquals("Line 1", lines.get(0));
    assertEquals("Line 2", lines.get(1));
    assertEquals("Line 3", lines.get(2));
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
    List<String> lines = javaGrepImp.readLines(new File(testFilePath));

    // Assert
    // Assert that no lines were read
    assertEquals(0, lines.size());
  }

  /**
   * Test case for readLines method with a null file.
   * Expects NullPointerException to be thrown.
   */
  @Test(expected = NullPointerException.class)
  public void testReadLines_NullFile_Success() throws IOException {
    // Arrange
    // Read the lines from a null file
    List<String> lines = javaGrepImp.readLines(null);

    // Assert
    // Assert that no lines were read
    assertEquals(0, lines.size());
  }

  /**
   * Test case for readLines method with a nonexistent file.
   * Expects IOException to be thrown.
   */
  @Test(expected = IOException.class)
  public void testReadLines_NonexistentFile_IOException() throws IOException {
    // Arrange
    // Read the lines from a nonexistent file
    javaGrepImp.readLines(new File("nonexistent.txt"));
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
    javaGrepImp = new JavaGrepImp();
    List<File> files = javaGrepImp.listFiles(rootDir);

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
    javaGrepImp = new JavaGrepImp();
    List<File> files = javaGrepImp.listFiles(rootDir);

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
    javaGrepImp = new JavaGrepImp();
    List<File> files = javaGrepImp.listFiles("nonexistent-dir");

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

    // Set the regex and rootPath for the JavaGrepImp instance
    javaGrepImp.setRegex("pattern");
    javaGrepImp.setRootPath(testDirectoryPath);

    // Act
    // Invoke the process method
    javaGrepImp.process();

    // Assert
    // Verify that the outFile is empty
    List<String> outFileContent = javaGrepImp.readLines(new File(javaGrepImp.getOutFile()));
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

    // Set the regex and rootPath for the JavaGrepImp instance
    javaGrepImp.setRegex("pattern");
    javaGrepImp.setRootPath(testDirectoryPath);

    // Act
    // Invoke the process method
    javaGrepImp.process();

    // Assert
    // Verify that the outFile contains the matched lines
    List<String> outFileContent = javaGrepImp.readLines(new File(javaGrepImp.getOutFile()));
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
    javaGrepImp.setRootPath("");

    // Act
    // Invoke the process method
    javaGrepImp.process();

    // Assert
    // Verify that the outFile is empty
    List<String> outFileContent = javaGrepImp.readLines(new File(javaGrepImp.getOutFile()));
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
    javaGrepImp.setRootPath("nonexistent-path");

    // Act
    // Invoke the process method
    javaGrepImp.process();

    // Assert
    // Verify that the outFile is empty
    List<String> outFileContent = javaGrepImp.readLines(new File(javaGrepImp.getOutFile()));
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
}
