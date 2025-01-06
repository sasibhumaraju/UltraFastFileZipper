import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.luben.zstd.Zstd;

public class FileZipper {
    private static Scanner sc;
    private static File file;
    private static final int CHUNK_SIZE;

    static {
        sc = new Scanner(System.in);
        CHUNK_SIZE = 1024 * 1024 * 10; // 10 mb each chunk size
    }

    public static boolean compress() throws IOException, InterruptedException, Exception {

        // Accept valid file 
        while(!(file = new File(sc.nextLine())).exists() || !file.isFile())  {
            Message.info("Provide valid file path: ", 1);
        }

        FileZipper fz = new FileZipper();
        fz.coreCompress(file);
        return true;
        
    }

    private void coreCompress(File file) throws IOException, InterruptedException, Exception {
        
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        int fileLength = (int) file.length();
        String fileName = file.getName().split("\\.")[0];

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        File outFile = new File(getOutputFile(fileName,fileLength)+"/"+fileName+".zip") ;
        outFile.createNewFile();
        List<Callable<Boolean>> tasks = new LinkedList<>();
        TreeMap<Integer, byte[]> compressedBytes = new TreeMap<>();
        AtomicInteger tempIdx = new AtomicInteger();
        AtomicInteger length = new AtomicInteger();


        for(int i = 0; i < fileLength; i+=CHUNK_SIZE) {
            byte[] bytes = new byte[Math.min(i+CHUNK_SIZE,fileLength)-i];
            raf.seek(i);
            raf.readFully(bytes);
            tasks.add(()->{
                try {
                    byte[] cb = Zstd.compress(bytes);
                    System.out.println("thread"+Thread.currentThread().getName());
                    compressedBytes.put(tempIdx.incrementAndGet(), cb);
                    length.set(length.get()+cb.length);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        RandomAccessFile craf = new RandomAccessFile(outFile,"rw");

        compressedBytes.forEach((Integer i, byte[] cb) -> {
            try {
                craf.seek(craf.length());
                craf.write(cb);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private String getOutputFile(String srcFileName, int srcFileLength) throws IOException {
    
        File outF = new File("c:/UltraFileZipper/"+srcFileName);
        if(!outF.exists()) outF.mkdirs();
        byte[] metaData = (srcFileLength+":"+srcFileName).getBytes();
        FileOutputStream fw = new FileOutputStream("c:/UltraFileZipper/"+srcFileName+"/"+srcFileName+".txt");
        fw.write(metaData);
        fw.close();
        return "c:/UltraFileZipper/"+srcFileName;
    }

    public static void decompress() {
        String comFL = sc.nextLine();
        
        String relName = new LinkedList<String>(Arrays.asList(comFL.split("\\\\"))).getLast();
        System.out.println(relName);
        FileReader fr;
        try {
            fr = new FileReader(comFL+"/"+relName+".txt");
            BufferedReader br = new BufferedReader(fr);
            int originalLength = Integer.parseInt((br.readLine()).split(":")[0]);
        
            FileInputStream fi = new FileInputStream(comFL+"/"+relName+".zip");
            byte[] orFB = Zstd.decompress(fi.readAllBytes(), originalLength);
            FileOutputStream fo = new FileOutputStream(comFL+"/abc.png");
            fo.write(orFB);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
