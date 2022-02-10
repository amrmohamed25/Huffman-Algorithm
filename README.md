# Huffman-Algorithm
In this part, it is required to implement Huffman's algorithm that we discussed in the greedy algorithms lecture. Your implementation should allow compressing and decompressing arbitrary files. As discussed in class, the implementation should collect statistics from the input file first, then apply the compression algorithm. Note that you will need to store a reasonable representation of the codewords in the compressed file, so that you can decompress the file back. 
Your program should have the capability of considering more than one byte. For example, instead of just collecting the frequencies and finding codewords for single bytes. The same can be done assuming the basic unit is n bytes, where n is an integer.
The implementation will be graded based on correctness and performance.

# Specifications

- You will submit a single runnable jar that will be used for both compression and decompression. Your jar should be named as huffman_<id>.jar. Replace id with your group id. The jar must include the source code files.
To use it for compressing an input file, the following will be called:
  <h3>java -jar huffman_<id>.jar c absolute_path_to_input_file n</h3> 
    c means compressing the file.
n is the number of bytes that will be considered together.
To use it for decompressing an input file, the following be called:
<h3>java -jar huffman_<id>.jar d absolute_path_to_input_file</h3>

- If the user chooses to compress a file with the name abc.exe, the compressed file should have the name <id>.<n>.abc.exe.hc where <id> should be replaced by your group id number, and <n> should be replaced by n (the number of bytes per group). The compressed file should appear in the same directory of the input file. The program should print the compression ratio and the compression time in seconds.
  
- If the user chooses to decompress a file with name abc.exe.hc, the output file should be named extracted.abc.exe. This should appear in the same directory of the input file. You don't need to include the id number here.
In this case, the program should only print the decompression time in seconds.

