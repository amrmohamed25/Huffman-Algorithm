import java.io.*;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class HuffmanNode {
    HuffmanNode left;
    HuffmanNode right;
    String ch;
    int frequency;
}

class ImplementComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {
        return x.frequency - y.frequency;
    }
}

public class Main {


    public static void main(String[] args) throws IOException {
        String cha = args[0];
        if (cha.equals("c")) {
            int n = Integer.parseInt(args[2]);
            String inputFile = args[1];
            Path p=Paths.get(args[1]);
            String outputFile = p.getFileName().toString()+ ".hc";


            HuffmanCompress huffmanCompresss = new HuffmanCompress();
            long start = System.currentTimeMillis();
            huffmanCompresss.encode(inputFile, outputFile, n);
            long end = System.currentTimeMillis();
//        //finding the time difference and converting it into seconds
            float sec = (end - start) / 1000F;
            System.out.println(sec + " seconds");
            long fileInSize = new File(inputFile).length();//getting fileSize
            long fileOutSize = new File(outputFile).length();//getting fileSize
            System.out.println((float) fileOutSize / fileInSize);
        }
        if (cha.equals(("d"))) {
            String outputFile = args[1];
            Path p=Paths.get(outputFile);
            String decompressedFile = "extracted." + p.getFileName().toString().substring(0,p.getFileName().toString().length()-3);
            HuffmanDecompress huffmanDecompress = new HuffmanDecompress();
            long start = System.currentTimeMillis();
            huffmanDecompress.decode(outputFile, decompressedFile);
            long end = System.currentTimeMillis();
//        finding the time difference and converting it into seconds
            float sec = (end - start) / 1000F;
            System.out.println(sec + " seconds");
        }
    }
}
