# Introduction
The Grep application offers two approaches for efficiently searching patterns within files in a directory and its subdirectories. The Stream-Based Approach, implemented in the StreamJavaGrepImp class, leverages Java Streams and functional programming paradigms to process large datasets with concise and elegant code. By utilizing Streams, the application can efficiently handle files that exceed the system's physical memory size while maintaining a small heap memory footprint (e.g., 10-20MB). This approach is particularly advantageous for handling massive datasets, making it ideal for scenarios where memory efficiency is a concern.

The Traditional Approach, implemented in the JavaGrepImp class, follows conventional file I/O and looping mechanisms and is better suited for handling moderate-sized data sets. While it may not be as memory-efficient as the Stream-Based Approach for very large datasets, it still provides a reliable and straightforward method for searching patterns within files.

The core technologies used in the development of Grep include core Java, Java Streams for the Stream-Based Approach, and Apache Maven as the build tool. Additionally, the application is Dockerized, allowing for easy distribution and deployment across different environments.

To ensure the reliability and accuracy of the implementation, the application is extensively unit-tested using JUnit and Mockito. Test scenarios include checking for matching patterns in files with various structures, testing different regex patterns, and verifying the accuracy of the search and processing operations.

In conclusion, Grep provides flexible pattern search solutions that cater to different requirements and system capabilities. The two distinct approaches empower users to select the most suitable method for their specific use cases, enabling efficient processing of both large and moderate-sized datasets. With its memory-efficient Stream-Based Approach and robust Traditional Approach, Grep is a versatile and powerful tool for searching and processing text files in Java applications.

# Quick Start
To use the Grep application, follow these steps:

1. **Build the Application:** Build the application using Maven. Open a terminal/command prompt and navigate to the project directory. Then, execute the following command:
```bash
mvn clean package
```

2. **Run the JavaGrep Approach:** To use the JavaGrep approach, run the following command:
```bash
java -cp target/grep-1.0-SNAPSHOT.jar ca.jrvs.apps.grep.JavaGrepImp '.*pattern.*' /path/to/root /path/to/out.txt
```

3. **Run the StreamJavaGrep Approach:** To use the StreamJavaGrep approach, run the following command:
```bash
java -cp target/grep-1.0-SNAPSHOT.jar ca.jrvs.apps.StreamJavaGrepImp '.*pattern.*' /path/to/root /path/to/out.txt
```

## Implementation

The Grep application is designed to search for lines containing a specific pattern (specified using a regular expression) within a root directory and its subdirectories. It provides two approaches to achieve this functionality:

1. **Stream-Based Approach** (`StreamJavaGrepImp` class):
   - In this approach, Java Streams are utilized to process files and search for matching lines.
   - The application recursively lists all files in the root directory and its subdirectories using Streams, reads lines from each file using Stream-based I/O, and filters out the lines that contain the specified pattern.
   - The matching lines are then written to an output file using a Stream-based approach.

2. **Traditional Approach** (`JavaGrepImp` class):
   - In this approach, traditional file I/O and looping mechanisms are used to process files and search for matching lines.
   - The application recursively lists all files in the root directory and its subdirectories using traditional recursive file traversal.
   - It then reads lines from each file using a BufferedReader and checks if the lines contain the specified pattern.
   - The matching lines are written to an output file using traditional file writing.

Both approaches provide similar functionalities but differ in the way they handle file processing and line searching. The Stream-Based Approach leverages Java Streams and functional programming paradigms, making the code concise and expressive. On the other hand, the Traditional Approach follows a more conventional file I/O approach with loops.

The key technologies and libraries used in this project include:
- Core Java for implementing the application logic.
- Java Streams for the Stream-Based Approach to process files and lines efficiently.
- BufferedReader and BufferedWriter for reading from and writing to files in the Traditional Approach.
- JUnit and Mockito for unit testing, ensuring the correctness of the application.
- Apache Maven as the build tool to compile, package, and manage dependencies.
- Docker to containerize the application for easy distribution and deployment.

### Pseudocode (Process Method)

Below is the pseudocode for the `process()` method in both the Stream-Based Approach (`StreamJavaGrepImp` class) and the Traditional Approach (`JavaGrepImp` class):

#### Stream-Based Approach (StreamJavaGrepImp class)

```plaintext
function process():
    log "Root Path: " + rootPath

    output <- create new output file writer for outFile

    // Step 1: List all files in the root directory and its subdirectories
    files <- listFiles(rootPath)

    // Step 2: Process each file
    for file in files:
        // Step 2.1: Read lines from the file using Streams
        lines <- readLines(file)

        // Step 2.2: Filter and find lines containing the pattern using Streams
        matchingLines <- lines.filter(line -> containsPattern(line))

        // Step 2.3: Write matching lines to the output file
        matchingLines.forEach(line -> output.println(line))
        output.flush()

    // Step 3: Close the output file writer
    output.close()

    log "StreamJavaGrepImp process completed"
end function
```

#### Traditional Approach (JavaGrepImp class)

```plaintext
function process():
    log "Root Path: " + rootPath

    output <- create new output file writer for outFile

    // Step 1: List all files in the root directory and its subdirectories
    files <- listFiles(rootPath)

    // Step 2: Process each file
    for file in files:
        // Step 2.1: Open file for reading
        fileReader <- open file for reading

        // Step 2.2: Read lines from the file
        line <- fileReader.readLine()
        while line is not null:
            // Step 2.3: Check if the line contains the pattern
            if containsPattern(line):
                // Step 2.4: Write matching lines to the output file
                output.println(line)
                output.flush()

            // Step 2.5: Read the next line
            line <- fileReader.readLine()

        // Step 2.6: Close the file reader
        fileReader.close()

    // Step 3: Close the output file writer
    output.close()

    log "JavaGrepImp process completed"
end function
```

Please note that the above pseudocode is a high-level representation of the `process()` method in both approaches. The actual implementation has additional error handling and details not shown in the pseudocode.

## Performance Issue
The primary performance issue with the JavaGrep approach is its reliance on loading the entire content of each file into memory before processing. This can be problematic when dealing with large files or numerous files simultaneously, as it may lead to excessive memory consumption and potential

 OutOfMemoryErrors.

In contrast, the StreamJavaGrep approach overcomes this memory issue by reading files line by line using BufferedReader and Java Streams. This stream-based processing enables the application to handle vast datasets with limited heap memory, making it more efficient and scalable.

# Test
To ensure the correctness and reliability of the Grep application, I conducted comprehensive unit testing using JUnit and Mockito. The unit tests covered various components and functionalities of the application.

**Testing the process() Method:**
- Validated the behavior of the `process()` method with different input scenarios, including files with matching patterns, files without matching patterns, empty files, and nonexistent files.
- Employed Mockito to mock dependencies such as `listFiles()` and `readLines()` to isolate the `process()` method for focused testing.

**Testing Helper Methods:**
- Wrote unit tests for critical helper methods, such as `containsPattern()` and `readLines()`, to ensure they correctly identified pattern matches and read lines from files as expected.
- Tested the `listFiles()` method for its ability to recursively list files in a directory, covering both existing and nonexistent directories.

**Edge Cases and Exception Handling:**
- Designed tests for edge cases and exceptional scenarios, such as null inputs, empty files, and invalid regex patterns.
- Ensured the application gracefully handled these cases and provided appropriate error handling and logs.

**Performance Testing:**
- Evaluated the application's performance and efficiency using unit tests with large files and directories.
- Ensured the application performed well with significant amounts of data while staying within specified memory limits.

**Assertions and Verification:**
- Employed JUnit's assertions to verify expected outcomes for each test scenario.
- Utilized Mockito's verification capabilities to confirm interactions between methods under test and their dependencies.

The rigorous unit testing approach resulted in a high-quality Grep application capable of efficiently and accurately processing large data sets.

# Deployment
To facilitate easier distribution and deployment, I dockerized the Grep application using Docker.

1. **Docker Image Creation:** I created a Dockerfile that specifies the application's build and execution process. The Dockerfile copies the built JAR file into the container and sets it as the entry point.

2. **Build Docker Image:** I built the Docker image using the following command in the project directory:
```bash
docker build -t kunjpatel8/grep .
```

3. **Run Docker Container:** Once the Docker image was successfully built, I ran the Grep application in a Docker container with the desired parameters:
```bash
docker run --rm -v `pwd`/data:/data -v `pwd`/log:/log kunjpatel8/grep '.*Romeo.*Juliet.*' /data /log/grep.out
```

The above command mounts the host machine's `data` directory to the `/data` directory inside the container and the host machine's `log` directory to the `/log` directory inside the container. It executes the Grep application with the provided regex pattern, root directory, and output file path.


# Improvement
While the Grep application offers efficient file processing, there are several areas for potential improvement:

1. **Multi-Threading:** Implementing multi-threading can further enhance performance by allowing concurrent processing of files, especially when dealing with large datasets.

2. **Additional Output Formats:** Expanding the application to support other output formats, such as JSON or XML, would make it more versatile for different use cases.

3. **Security Considerations:** As the application deals with processing files and user-defined regex patterns, it's essential to incorporate security measures. To enhance security, consider the following points:
   - **Input Validation:** Implement robust input validation for user-provided parameters, such as the regex pattern and file paths, to prevent malicious inputs and potential security vulnerabilities.
   - **File Access Control:** Ensure that the application has appropriate access control and permissions to read and write files, preventing unauthorized access and data breaches.
   - **Sanitization of Output:** If the application writes output to files, ensure that the output is properly sanitized to prevent injection attacks and unintended consequences.
