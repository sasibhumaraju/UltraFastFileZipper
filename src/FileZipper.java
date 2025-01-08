import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.github.luben.zstd.Zstd;

public class FileZipper {
    private static Scanner sc;
    private static File file;
    private static final int CHUNK_SIZE;

    private FileZipper() {};

    static {
        sc = new Scanner(System.in);
        CHUNK_SIZE = 1024 * 1024 * 10; // 10 mb each chunk size
    }

    public static void compress()  {

        // Accept valid file 
        Message.info("Provide File Path", 0);
        while(!(file = new File(sc.nextLine())).exists() || !file.isFile())  {
            Message.info("Provide valid file path: ", 0);
        }

        Message.info("File taken..", 0);

        // pass valid file to core compressor object
        FileZipper fz = new FileZipper();
        try {
            fz.coreCompress(file);
        } catch(Exception e) {
            Message.error("Failed to compress file :"+e.getMessage(), 2);
        }
        
    }

    private void coreCompress(File file) throws IOException, InterruptedException, Exception {
        
        // create random file access pointer in "r" mode
        RandomAccessFile raSource = new RandomAccessFile(file, "r");

        Message.info("Inside core", 0);
        // varibles for file length & file name
        int threadCount = Runtime.getRuntime().availableProcessors();
        int fileLength = (int) file.length();
        String fileName = file.getName().split("\\.")[0];
        String extenion = file.getName().split("\\.")[1];

        // creating threads - tasks list, using executor fixed thread pool with runtime available count
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Callable<Boolean>> tasks = new LinkedList<>();

        // tree map for storing compressed bytes in order
        final Hashtable<Integer, byte[]> compressedBytes = new Hashtable<>();

        // creating out file inside root to store compressed file bytes
        File outFile = new File(getOutputFile(fileName, fileLength, extenion)+"/"+fileName+".zip") ;
        outFile.delete();
        outFile.createNewFile();

        RandomAccessFile metaFile = new RandomAccessFile("c:/UltraFileZipper/"+fileName+"/"+fileName+".txt", "rw");

        // dividing file into chunks and compressing each chunk in order
        int latchCount = (int)Math.ceil((double)fileLength/(double)CHUNK_SIZE);
        CountDownLatch latch = new CountDownLatch(latchCount); // Initialize latch with the number of tasks
        
        for(int i = 0; i < fileLength; i+=CHUNK_SIZE) {
            byte[] bytes = new byte[Math.min(i+CHUNK_SIZE,fileLength)-i];
            raSource.seek(i);
            raSource.readFully(bytes);

            metaFile.seek(metaFile.length());
            metaFile.write((bytes.length+",").getBytes());

            int temp = i;

            tasks.add(()->{
                try {
                    byte[] cb = Zstd.compress(bytes);
                    compressedBytes.put(temp, cb);
                    System.out.println(cb.length);
                    // System.out.println(Thread.currentThread().getName());

                } catch (Exception e) {
                    Message.error("Compressing chuncks failed"+e.getMessage(), 2);
                } finally {
                    
                    latch.countDown();
                    // System.out.println(latch.getCount());
                }
                return true;
            });
        }


        // executing tasks by invoke all
        executor.invokeAll(tasks);

        // stops current thread until count reaches to zero
        latch.await();
        executor.shutdown();
        System.out.println(";;"+latchCount);
        System.out.println("latch count: "+latch.getCount());

        
        

        RandomAccessFile craf = new RandomAccessFile(outFile,"rw");
        metaFile.write("\n".getBytes());
        // interating compressedBytes map, to store compressed bytes in out file one by one
        compressedBytes.forEach((Integer i, byte[] cb) -> {
            try {
                craf.seek(craf.length());
                craf.write(cb);
                metaFile.write((cb.length+",").getBytes());
                System.out.println(i);

            } catch (IOException e) {
                Message.error("Saving compressed bytes to file failed"+e.getMessage(), 2);
            }
        });

        craf.close(); raSource.close(); metaFile.close();

    }

    // Creating output folder -> with meta data file
    private String getOutputFile(String srcFileName, int srcFileLength, String extension) throws IOException {
    
        // output dir creation
        File outF = getDirectoryInCDrive("c:/UltraFileZipper/"+srcFileName);

        // meta data of original file, which is needed while decompressing file
        byte[] metaData = (srcFileLength+":"+srcFileName+":"+extension+"\n").getBytes();
        FileOutputStream fw = new FileOutputStream("c:/UltraFileZipper/"+srcFileName+"/"+srcFileName+".txt");
        fw.write(metaData);
        fw.close(); 

        // return outfile root dir name 
        return "c:/UltraFileZipper/"+srcFileName;
    }

    // dir creation
    private static File getDirectoryInCDrive(String name) {
        File file = new File(name);
        if(!file.exists()) file.mkdirs();
        System.out.println("*** "+file.getAbsolutePath()+" *******");
        return file;
    }


    // decompress file
    public static void decompress() {

        String compressedRootDirLocation; // input file location
        File rootFile, childFile, metaFile; // root, org file, meta file - file refs

        // take input and validate 
        while(true) {
            compressedRootDirLocation = sc.nextLine();
            rootFile = new File(compressedRootDirLocation);
            childFile = new File(compressedRootDirLocation+"/"+rootFile.getName()+".zip");
            metaFile = new File(compressedRootDirLocation+"/"+rootFile.getName()+".txt");
            if(rootFile.exists() && rootFile.isDirectory() && childFile.exists() && metaFile.exists()) break;
            Message.error("Please provide valid zip root path..", 2);
        }
        

        try {
            // read meta data to get org file length, name, extension to decompress
            BufferedReader br = new BufferedReader(new FileReader(metaFile));
            String[] metaData = br.readLine().split(":");

            List<Integer> orgChunckLengths = Arrays.stream(br.readLine().split(",")).map((v)->Integer.parseInt(v)).collect(Collectors.toList());

            System.out.println(orgChunckLengths);
            int orgFileLength = Integer.parseInt(metaData[0]);
            String extension = metaData[2];
            String orgFileName = metaData[1];

            // input stream from compressed file
            FileInputStream fi = new FileInputStream(childFile);

            // read all bytes form input stream and decompress
            byte[] orFB = Zstd.decompress(fi.readAllBytes(), orgFileLength);

            // re store org file
            FileOutputStream fo = new FileOutputStream(getDirectoryInCDrive("c:/UltraFileUnZipper").getAbsolutePath()+"/"+orgFileName+"."+extension);
            fo.write(orFB);

            // closing resources
            br.close(); fi.close(); fo.close();
           
        } catch (Exception e) {
            Message.error("Failed unzip the file"+e.getMessage(), 2);
        } 
    }
    
}
