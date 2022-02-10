import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HuffmanDecompress {
    public int rebuildHashedTree(byte[] allBytes) {
        String headerSize;

        headerSize = String.format("%8s", Integer.toBinaryString((allBytes[0] + 256) % 256)).replaceAll(" ", "0");
        headerSize = headerSize + String.format("%8s", Integer.toBinaryString((allBytes[1] + 256) % 256)).replaceAll(" ", "0");
        headerSize = headerSize + String.format("%8s", Integer.toBinaryString((allBytes[2] + 256) % 256)).replaceAll(" ", "0");
        headerSize = headerSize + String.format("%8s", Integer.toBinaryString((allBytes[3] + 256) % 256)).replaceAll(" ", "0");
        int hashMapSize = 0;
        int ii = 0;

        for (int ll = headerSize.length() - 1; ll >= 0; ll--) {
            if (ll == headerSize.length() - 1 && headerSize.charAt(ii) == '1') {
                hashMapSize -= Math.pow(2, ll);
            } else {
                if (headerSize.charAt(ii) == '1')
                    hashMapSize += Math.pow(2, ll);
            }
            ii++;
        }
        return hashMapSize;
    }


    public List<Byte> rebuildHashedTreeAndInput(String outputFile, OutputStream outputStream) throws IOException {
        int flag = 0;
        List<Byte> by = new ArrayList<>();
        byte[] tempKeyByte = {};
        int sizeFlag = 0;
        int stringSize = 0;
        StringBuilder outputCompressed = new StringBuilder();
        long fileSizes = new File(outputFile).length();
        byte[] allBytes = new byte[(int) fileSizes];
        InputStream inputStream = new FileInputStream(outputFile);
        HashMap<String, String> newTree = new HashMap<>();
        inputStream.read(allBytes);
        inputStream.close();
        int hashMapSize = rebuildHashedTree(allBytes);
        byte byteRead;
        int index = 4;
        int maxSize = 0;
        int readFlag = 0;

        List<Byte> bytes = new ArrayList<>();
        String textHelp;
        while (index < fileSizes) {
            byteRead = allBytes[index++];
            if (sizeFlag == 1) {

                stringSize = byteRead;
                by.add(byteRead);
                sizeFlag = 0;
            } else {
                if (flag == 0) {
                    by.add(byteRead);
                    if (by.size() > 1) {
                        int res = by.get(0).intValue();
                        if (res == by.size() - 1 && readFlag == 0) {
                            int jj = 0;
                            by.remove(0);
                            tempKeyByte = new byte[by.size()];
                            for (Byte b : by) {
                                tempKeyByte[jj++] = b.byteValue();
                            }
                            by.clear();
                            sizeFlag = 1;
                            readFlag = 1;
                        }
                    }
                    if (by.size() > 1) {
                        int res;
                        res = (int) Math.ceil(by.get(0) / 8.0);
                        if (res == by.size() - 1 && readFlag == 1) {

                            readFlag = 0;
                            by.remove(0);

                            int lo = 0;
                            StringBuilder t = new StringBuilder();
                            for (lo = 0; lo < by.size() - 1; lo++) {//handling case if bits < 8

                                t.append(String.format("%8s", Integer.toBinaryString((by.get(lo) + 256) % 256)).replaceAll(" ", "0"));
                            }
                            if (t.length() != stringSize) {
                                t.append(String.format("%" + (stringSize - t.length()) + "s", Integer.toBinaryString((by.get(lo) + 256) % 256)).replaceAll(" ", "0"));
                            }
                            newTree.put(t.toString(), Arrays.toString(tempKeyByte));
                            by.clear();
                            maxSize++;
                        }
                    }
                    if (maxSize == hashMapSize) {
                        by.clear();
                        flag = 1;
                    }

                } else {
                    if (index == fileSizes - 1) {
                        String ts = String.format("%8s", Integer.toBinaryString((byteRead + 256) % 256)).replaceAll(" ", "0");
                        String tem = String.format("%8s", Integer.toBinaryString((allBytes[index++] + 256) % 256)).replaceAll(" ", "0");
                        int num = Integer.parseInt(tem, 2);
                        if (num < 8) {
                            ts = ts.substring(ts.length() - num, ts.length());
                        }
                        outputCompressed.append(ts.replace(" ", "0"));
                    } else {
                        String ts = String.format("%8s", Integer.toBinaryString((byteRead + 256) % 256));
                        outputCompressed.append(ts.replace(" ", "0"));
                    }
                    textHelp = "";
                    for (int i = 0; i < outputCompressed.length(); i++) {
                        textHelp += outputCompressed.charAt(i);
                        if (newTree.containsKey(textHelp)) {
                            String d = newTree.get(textHelp).replaceAll("\\[|\\]", "").replaceAll(" ", "");
                            String[] words = d.split(",");
                            for (int ii = 0; ii < words.length; ii++) {
                                byte bys = (byte) Integer.parseInt(words[ii]);
                                bytes.add(bys);
                            }
                            outputCompressed.delete(0, i + 1);
                            i = -1;
                            textHelp = "";
                        }
                    }
                    if (index % 10000000 == 0) {
                        byte[] outBytes = new byte[bytes.size()];
                        int jj;
                        for (jj = 0; jj < bytes.size(); jj++) {
                            outBytes[jj] = bytes.get(jj);
                        }
                        outputStream.write(outBytes);
                        bytes.clear();
                    }
                }
            }
        }
        return bytes;
    }

    public void decode(String outputFile, String decompressedFile) throws IOException {
        File decompressedfile = new File(decompressedFile);
        OutputStream outputStream = new FileOutputStream(decompressedfile);
        List<Byte> bytes = rebuildHashedTreeAndInput(outputFile, outputStream);

        if (bytes.size() != 0) {
            byte[] outBytes = new byte[bytes.size()];
            int jj;
            for (jj = 0; jj < bytes.size(); jj++) {
                outBytes[jj] = bytes.get(jj);
            }
            outputStream.write(outBytes);
        }
        outputStream.close();
    }
}
