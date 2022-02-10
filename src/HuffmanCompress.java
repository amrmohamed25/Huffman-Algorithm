import java.io.*;
import java.util.*;

public class HuffmanCompress {

    public static void compress(HuffmanNode root, String str, HashMap<String, String> output) {//reference geeksforgeeks
        if (root == null) {//base case after reaching a leaf node
            return;
        }

        // Found a leaf HuffmanNode
        if (root.right == null && root.left == null) {
            output.put(root.ch, str);//storing binary code
        }

        compress(root.left, str + '0', output);//recursive call to left node and concatenating a zero for left node
        compress(root.right, str + '1', output);//recursive call to right node and concatenating a one for right node
    }

    public HashMap<String, Integer> buildFreqMap(InputStream inputStream, String inputFile, int n) throws IOException {
        HashMap<String, Integer> freq = new HashMap<>();//initialize empty hashmap
        int index = 0;//index used to access bytes
        byte[] bytes = new byte[n];//temp byte used to fill the hashmap
        long fileSize = new File(inputFile).length();//getting length of file
        byte[] allByte = new byte[(int) fileSize];//allByte used to read from file
        inputStream.read(allByte);//read all bytes from file
        inputStream.close();//closing input stream as reading is done
        for (; index < fileSize; index += n) {
            if (index + n < fileSize) {
                System.arraycopy(allByte, index, bytes, 0, n);//copying n bytes from allByte to bytes
                freq.put(Arrays.toString(bytes), freq.getOrDefault(Arrays.toString(bytes), 0) + 1);//inserting into hashmap and incrementing frequency or initializing it with zero if not found
            } else {
                byte[] te = new byte[(int) fileSize - index];//calculating remaining bytes that wasn't divisible by n
                System.arraycopy(allByte, index, te, 0, (int) fileSize - index);//copying from allByte to te
                freq.put(Arrays.toString(te), freq.getOrDefault(Arrays.toString(te), 0) + 1);//inserting into hashmap and incrementing frequency or initializing it with zero if not found
            }
        }
        return freq;
    }

    public PriorityQueue<HuffmanNode> buildPriorityQueue(int n, HashMap<String, Integer> freq) {//reference from geeksforgeeks
        PriorityQueue<HuffmanNode> q = new PriorityQueue<>(n, new ImplementComparator());//building priority queue with a new specific compare function
        for (HashMap.Entry<String, Integer> entry : freq.entrySet()) {//iterating through hashmap elements

            HuffmanNode hn = new HuffmanNode();//initialize empty node

            hn.ch = entry.getKey();//getting key from hashmap
            hn.frequency = entry.getValue();//getting frequency from hashmap
            hn.left = null;
            hn.right = null;

            q.add(hn);//inserting in priority queue and arranging every insert
        }
        return q;
    }

    public HashMap<String, String> buildHashTree(PriorityQueue<HuffmanNode> q) {//reference from geeksforgeeks
        HuffmanNode root = null;
        while (q.size() > 1) {//while there is still an element

            HuffmanNode x = q.poll();//pop first element

            HuffmanNode y = q.poll();//pop first element

            HuffmanNode f = new HuffmanNode();

            f.frequency = x.frequency + y.frequency;//sum frequencies
            f.ch = null;//no characters(key) for non leaf nodes
            f.left = x;
            f.right = y;
            root = f;
            q.add(f);//inserting in comparator with new node
        }
        HashMap<String, String> output = new HashMap<>();//initializing output which will hold key(ab...) value(0110101.....)
        compress(root, "", output);//inserting in hashmap with value equivalent to frequency in binary
        return output;
    }

    public void writeHashMapSize(HashMap<String, String> hashedTree, OutputStream outputStream) throws IOException {
        int headSize = hashedTree.size(); //getting size hashmap to be converted to binary then it will be converted to bytes
        int iii = 0;
        char[] tet = new char[8];//temp byte will be used to get 8 bits and convert them to one byte
        StringBuilder s = new StringBuilder();//string that will hold bits as a string
        while (headSize > 0) {
            s.insert(0, ((headSize % 2) == 0 ? "0" : "1"));//adding zero if it is divisible and adding one if it isn't (adding is from the left because it is from division)
            headSize = headSize / 2;//dividing by 2
        }
        //NOTE: we are making 4 bytes to store hashmap size
        while (s.length() != 32) {//while not 32 it means we will add zero from the left to fill 32 bits or 4 bytes
            s.insert(0, "0");//inserting zero from the left
        }
        for (int i = 0; i < s.length(); i++) {//this loop is used to convert 8 bits to one byte to be stored in file
            tet[iii++] = s.charAt(i);//setting 8 bit by bit
            if (iii % 8 == 0) {//if it is divisible by 8 then convert it to byte and store
                int ii = 0;//used to reference tet byte
                int result = 0;//result that will hold the integer equivalent to 8 bits
                for (int ll = tet.length - 1; ll >= 0; ll--) {
                    if (ll == tet.length - 1 && tet[ii] == '1') {//if first element is 1 then it is signed negative
                        result -= Math.pow(2, ll);//-value
                    } else {
                        if (tet[ii] == '1')//if it is one add it
                            result += Math.pow(2, ll);
                    }
                    ii++;
                }
                byte bys = (byte) result;//storing byte equivalent to int
                outputStream.write(bys);//writing to file
                iii = 0;//it will be used again with new 8 bits
            }
        }
    }

    public void writeHashedTree(HashMap<String, String> hashedTree, OutputStream outputStream) throws IOException {
        for (HashMap.Entry<String, String> entry : hashedTree.entrySet()) {//iterate through hashed map and storing it in file
            String word = entry.getKey().replaceAll("\\[|\\]", "");//removing brackets
            word = word.replaceAll(" ", "");//removing spaces
            String[] words = word.split(",");//splitting by ,
            byte b = (byte) (words.length & 0xFF);//storing length of key
            outputStream.write(b);//writing length
            for (int ii = 0; ii < words.length; ii++) {
                outputStream.write((byte) Integer.parseInt(words[ii]));//converting string to integer and inserting in file
            }
            char[] tep = new char[8];//initializing temp array of chars which will be used to write 8 bits at a time
            int tempIndex = 0;

            b = (byte) (entry.getValue().length() & 0xFF);//storing value length
            outputStream.write(b);//writing in file
            for (int k = 0; k < entry.getValue().length(); k++) {
                tep[tempIndex++] = entry.getValue().charAt(k);//getting 8 bits
                if (tempIndex % 8 == 0) {
                    int res = Integer.parseInt(String.valueOf(tep));//converting string to integer but take care that last 8 bits if they start with zeros they will be neglected
                                                                    //so length of value will be used to handle this case
                    byte bys = (byte) (res & 0xFF);
                    int result = 0;
                    int ii = 0;
                    for (int ll = tep.length - 1; ll >= 0; ll--) {//converting each 8 bits to integer
                        if (ll == tep.length - 1 && tep[ii] == '1') {
                            result -= Math.pow(2, ll);
                        } else {
                            if (tep[ii] == '1')
                                result += Math.pow(2, ll);
                        }
                        ii++;
                    }
                    bys = (byte) result;
                    outputStream.write(bys);
                    tempIndex = 0;
                }
            }
            if (tempIndex != 0) {//remaining bits will be converted alone here
                char[] te = new char[tempIndex];
                for (int kk = 0; kk < tempIndex; kk++) {
                    te[kk] = tep[kk];
                }
                byte bys = (byte) (int) Integer.valueOf(String.valueOf(te), 2);
                outputStream.write(bys);
            }
        }
    }

    public void writeHeader(HashMap<String, String> hashedTree, String inputFile, OutputStream outputStream) throws IOException {

        writeHashMapSize(hashedTree, outputStream);//writing how many element is stored in hashmap

        writeHashedTree(hashedTree, outputStream);//writing the hashmap

    }

    public void writeEncodedBinary(String inputFile, File inputFiles, int n, HashMap<String, String> hashedTree, OutputStream outputStream) throws IOException {
        InputStream inputStream = new FileInputStream(inputFiles);
        long fileSize = new File(inputFile).length();//getting fileSize
        byte[] allByte = new byte[(int) fileSize];//initializing array that will be read at
        inputStream.read(allByte);//reading bytes
        inputStream.close();
        byte[] bytes = new byte[n];//array will be used to copy n bytes
        char[] temp = new char[8];//temp char that will be used to convert 8 bits to one byte
        int index = 0;
        StringBuilder out = new StringBuilder();//will hold output string 010101010...
        List<Byte> inputToFile = new ArrayList<>();//List to hold bytes after conversion
        int maxAllowedSpace = 10000000;//max allowed space to write
        int currentSpace = 0;//currentSpace used to know if we reached max space
        while (index < fileSize) {//checking if we reached end of file
            if (index + n < fileSize) {//checking if it is less than n bytes then it will stored
                System.arraycopy(allByte, index, bytes, 0, n);//copying n bits
                out.append(hashedTree.get(Arrays.toString(bytes)));//appending in output string which will be converted to
            } else {//else it is less than n bytes so it will be stored as remaining (handling case if there the remaining is less than n bytes)
                byte[] te = new byte[(int) fileSize - index];//remaining bytes
                System.arraycopy(allByte, index, te, 0, (int) fileSize - index);//copying remaining bytes
                out.append(hashedTree.get(Arrays.toString(te)));//appending remaining string
            }
            index += n;//adding n that were read
            currentSpace += n;//adding n that were read
            while (out.length() > 8) {//if out.length>8 means we got 8 bits so they will be compressed if not 8 and we reached end of file they will be handled outside
                out.getChars(0, 8, temp, 0);//getting 8 bits
                byte bys = (byte) (int) Integer.valueOf(String.valueOf(temp), 2);//converting 8 bits to one byte
                inputToFile.add(bys);//adding to list of byte
                out.delete(0, 8);//deleting 8 bits that wer read
            }
            if (currentSpace >= maxAllowedSpace) {//if we reached maxAllowedSpace then we will write at once
                byte[] inBytes = new byte[inputToFile.size()];//convert list of bytes to array of bytes
                for (int jj = 0; jj < inputToFile.size(); jj++) {
                    inBytes[jj] = inputToFile.get(jj);//convert from list to bytes to be written in file
                }
                outputStream.write(inBytes);//writing array of bytes
                inputToFile.clear();//clearing list of byte for new bytes
                currentSpace = 0;
            }
        }
        if (out.length() > 0) {//we have remaining bits that weren't converted to bytes then they will be written here
            char[] te = new char[out.length()];
            for (int k = 0; k < out.length(); k++) {
                te[k] = out.charAt(k);
            }
            byte bys = (byte) (int) Integer.valueOf(String.valueOf(te), 2);//converting character to byte
            inputToFile.add(bys);//writing to list
        }
        if (out.length() == 0) {//writing length of last byte
            byte bys = (byte) 8;
            inputToFile.add(bys);
            //last byte has a size of 8
        } else {
            //last byte has size of less than 8
            byte bys = (byte) out.length();//writing length of last byte
            inputToFile.add(bys);
        }
        byte[] inBytes = new byte[inputToFile.size()]; //writing all remaining
        for (int jj = 0; jj < inputToFile.size(); jj++) {
            inBytes[jj] = inputToFile.get(jj);
        }
        inputToFile.clear();//clearing list
        outputStream.write(inBytes);//writing in file
        outputStream.close();
    }


    public void encode(String inputFile, String outputFile, int n) throws IOException {
        File inputFiles = new File(inputFile);
        File outputFiles = new File(outputFile);
        InputStream inputStream = new FileInputStream(inputFiles);//initializing input stream that will be used to read binary files
        HashMap<String, Integer> freq = buildFreqMap(inputStream, inputFile, n);//Building frequency map that will be used in priority queue
        PriorityQueue<HuffmanNode> q = buildPriorityQueue(n, freq);//building priority queue that will be used in building hashedTree
        inputStream.close();//closing inputStream

        HashMap<String, String> hashedTree = buildHashTree(q);//building tree  and using priority queue to build hashedmap
        System.out.println("n = "+n);
        for (Map.Entry<String, String> entry : hashedTree.entrySet()) {
//            System.out.println(entry.getKey().+" : "+entry.getValue());
            String word = entry.getKey().replaceAll("\\[|\\]", "");//removing brackets
            word = word.replaceAll(" ", "");//removing spaces
            String[] words = word.split(",");//splitting by ,
            byte[] b = new byte[1];
            for (int ii = 0; ii < words.length; ii++) {
                b[0]= (byte) Integer.parseInt(words[ii]);//converting string to integer and inserting in file
                System.out.print(new String(b));
            }
            System.out.println(":"+entry.getValue());
        }
        q.clear();//clearing priority queue as it will not be needed to avoid hash space error
        freq.clear();//clearing frequency as it will not be needed to avoid hash space error

        OutputStream outputStream = new FileOutputStream(outputFiles);//output stream to write compressed header and old input
        writeHeader(hashedTree, inputFile, outputStream);//writing header which will be used in decompressing

        writeEncodedBinary(inputFile, inputFiles, n, hashedTree, outputStream);
    }

}
