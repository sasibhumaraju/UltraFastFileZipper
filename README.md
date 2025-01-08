# UltraFastFileZipper

UltraFastFileZipper is a high-performance file compression and decompression tool that utilizes multi-threading to achieve fast and efficient file operations. This project is designed to handle large files by dividing them into chunks and processing them in parallel using multiple threads.

## Features

- Compress large files into smaller sizes using Zstandard (Zstd) compression.
- Decompress files back to their original state.
- List all compressed files in the specified directory.
- Multi-threaded processing for faster compression and decompression.

## File Structure

The project directory structure is as follows:

```
UltraFastFileZipper/
├── src/
│   ├── App.java
│   ├── FileZipper.java
│   └── Message.java
└── README.md
```

```
+---------------------+
| UltraFastFileZipper |
+---------------------+
        |
        +--- src
        |    |
        |    +--- App.java
        |    +--- FileZipper.java
        |    +--- Message.java
        |
        +--- README.md
```

## Internal File Chunk Division and Compression

The compression process involves dividing the input file into smaller chunks and compressing each chunk in parallel using multiple threads. This approach significantly reduces the time required for compression.

```
+---------------------+
|   Input File        |
+---------------------+
        |
        v
+---------------------+
|  Divide into Chunks |
+---------------------+
        |
        v
+---------------------+
|  Compress in Parallel|
+---------------------+
        |
        v
+---------------------+
|  Compressed File    |
+---------------------+
```

## How to Use

1. **Run the Application:**
   - Compile and run the `App.java` file.
   - Follow the on-screen instructions to select the desired operation (compress, decompress, list files, or exit).

2. **Compress a File:**
   - Provide the file path to be compressed.
   - The application will divide the file into chunks and compress each chunk using multiple threads.
   - The compressed file and metadata will be saved in the specified output directory.

3. **Decompress a File:**
   - Provide the root directory path of the compressed file.
   - The application will read the metadata and decompress the file back to its original state.

4. **List All Compressed Files:**
   - The application will display all compressed files in the specified directory.

## Example

```sh
java App
```

Follow the prompts to compress, decompress, or list files.

## Requirements

- Java 8 or higher
- Zstandard (Zstd) library

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/UltraFastFileZipper.git
   ```
2. Navigate to the project directory:
   ```sh
   cd UltraFastFileZipper
   ```
3. Compile the Java files:
   ```sh
   javac src/*.java
   ```
4. Run the application:
   ```sh
   java src/App
   ```

## Java Concepts Used

This project demonstrates the following Java concepts:

- **Object-Oriented Programming (OOP):** The project is structured using classes and objects, encapsulating related data and behavior.
- **File I/O:** Reading from and writing to files using `RandomAccessFile`, `FileInputStream`, `FileOutputStream`, and `BufferedReader`.
- **Exception Handling:** Proper handling of exceptions using try-catch blocks to ensure the application runs smoothly.
- **Multi-threading:** Utilizing `ExecutorService`, `Callable`, and `CountDownLatch` to perform parallel processing for faster compression and decompression.
- **Collections Framework:** Using `List`, `Hashtable`, and `TreeMap` to manage and store data efficiently.
- **Streams API:** Leveraging Java Streams for processing collections of objects.
- **Third-party Libraries:** Integrating the Zstandard (Zstd) compression library for efficient file compression and decompression.
- **Command-line Interface (CLI):** Interacting with the user through the command line to perform various operations.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## License

This project is licensed under the MIT License.
