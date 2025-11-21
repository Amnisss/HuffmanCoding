/*  Student information for assignment:
 *
 *  On <MY|OUR> honor, <NAME1> and <NAME2), this programming assignment is <MY|OUR> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 (Student whose Canvas account is being used)
 *  UTEID:
 *  email address:
 *  Grader name:
 *
 *  Student 2
 *  UTEID:
 *  email address:
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class SimpleHuffProcessor implements IHuffProcessor {

    // TODO: make compress and decompress class to reduce instance variables??
    private IHuffViewer myViewer;
    private int header;
    private Map<Integer, String> codeMap;
    private HuffmanTree huffTree;
    private int[] freqs;
    private boolean preprocessCalled;
    

    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it int one as needed.
     * @param in is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind of
     * header to use, standard count format, standard tree format, or
     * possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     * Note, to determine the number of
     * bits saved, the number of bits written includes
     * ALL bits that will be written including the
     * magic number, the header format number, the header to
     * reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
        int originalBits = 0;
        BitInputStream bt = new BitInputStream(in);
        int[] frequencies = new int[IHuffConstants.ALPH_SIZE];
        int code = bt.readBits(IHuffConstants.BITS_PER_WORD);
        while (code != -1) {
            frequencies[code]++;
            originalBits += 8;
            code = bt.readBits(IHuffConstants.BITS_PER_WORD);
        }

        bt.close();

        huffTree = new HuffmanTree(frequencies);
        codeMap = huffTree.createMap();
        header = headerFormat;
        freqs = frequencies;

        int compressedBits = calculateCompressedBits(headerFormat, codeMap, frequencies);
        
        //showString("Not working yet");
        //myViewer.update("Still not working");
        //throw new IOException("preprocess not implemented"); ??TODO

        return originalBits - compressedBits;
        
    }

    private int calculateCompressedBits(int headerFormat, Map<Integer, String> codeMap, int[] frequencies) {
        int compressedBits = 0;

        // Magic number
        compressedBits += IHuffConstants.BITS_PER_INT;

        // format identifier
        compressedBits += IHuffConstants.BITS_PER_INT;
        
        if (headerFormat == IHuffConstants.STORE_COUNTS) {
            compressedBits += IHuffConstants.BITS_PER_INT * IHuffConstants.ALPH_SIZE;
        } else if (headerFormat == IHuffConstants.STORE_TREE) {
            compressedBits += IHuffConstants.BITS_PER_INT + (codeMap.keySet().size() * (IHuffConstants.BITS_PER_WORD + 1 + 1)) + (codeMap.keySet().size() - 1);
        }

        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] != 0) {
                compressedBits += frequencies[i] * codeMap.get(i).length();
            }
        }

        //Pseudo-EOF
        compressedBits += codeMap.get(256).length();

        return compressedBits;
    }

    /**
	 * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br> pre: <code>preprocessCompress</code> must be called before this method
     * @param in is the stream being compressed (NOT a BitInputStream)
     * @param out is bound to a file/stream to which bits are written
     * for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than the input file.
     * If this is false do not create the output file if it is larger than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        
        // TODO: check precon (check if precompress was called)
        
        BitInputStream btIn = new BitInputStream(in);
        BitOutputStream btOut = new BitOutputStream(out);
        int bitsWritten = 0;

        // write magic number for hf file
        btOut.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.MAGIC_NUMBER);
        bitsWritten += IHuffConstants.BITS_PER_INT;

        if (header == IHuffConstants.STORE_COUNTS) {
            // write identifier for store count header format
            btOut.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.STORE_COUNTS);
            bitsWritten += IHuffConstants.BITS_PER_INT;

            // write out frequencies of each character
            for(int k=0; k < IHuffConstants.ALPH_SIZE; k++) {
                btOut.writeBits(BITS_PER_INT, freqs[k]);
                bitsWritten += BITS_PER_INT;
            }
        } else if (header == IHuffConstants.STORE_TREE) {
            // write identifier for store tree header format
            btOut.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.STORE_TREE);
            bitsWritten += IHuffConstants.BITS_PER_INT;

            // calculate and write outsize of header
            int size = (codeMap.keySet().size() * (BITS_PER_WORD + 1 + 1)) + (codeMap.keySet().size() - 1);
            btOut.writeBits(BITS_PER_INT, size);
            bitsWritten += BITS_PER_INT;

            // represent the huffman tree in bits
            huffTree.writeHeader(btOut);
            bitsWritten += size;
        }
        
        // read in the original file
        int code = btIn.readBits(IHuffConstants.BITS_PER_WORD);
        while (code != -1) {
            String compressed = codeMap.get(code);
            // write out the new code for each character
            for (int i = 0; i < compressed.length(); i++) {
                btOut.writeBits(1, compressed.charAt(i) - '0');
                bitsWritten++;
            }
            code = btIn.readBits(IHuffConstants.BITS_PER_WORD);
        }

        // write out pseudo EOF value at the end
        String pseudoEof = codeMap.get(256);
        for (int i = 0; i < pseudoEof.length(); i++) {
            btOut.writeBits(1, pseudoEof.charAt(i) - '0');
            bitsWritten++;
        }

        btOut.close();

        return bitsWritten;
    }

    

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     * @param in is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
	        //throw new IOException("uncompress not implemented");
            BitInputStream btIn = new BitInputStream(in);
            int magic = btIn.readBits(BITS_PER_INT);
            int bitsWritten = 0;

            int magic = in.readBits(BITS_PER_INT);
            if (magic != MAGIC_NUMBER) {
                viewer.showError("Error reading compressed file. \n" +
                        "File did not start with the huff magic number.");
                return -1;
            }

            int headerFormat = btIn.readBits(BITS_PER_INT);

	        if (headerFormat == IHuffConstants.STORE_COUNTS) {
                // reconstruct frequency array
                for(int k=0; k < IHuffConstants.ALPH_SIZE; k++) {
                    int frequencyInOriginalFile = in.readBits(BITS_PER_INT); // TODO: check if code = -1
                    freqs[k] = frequencyInOriginalFile;
                } 
                // reconstruct huffman tree
                huffTree = new HuffmanTree(freqs);
    
            } else if (headerFormat == IHuffConstants.STORE_TREE) {
                // read in size of the header
                int bitsLeft = btIn.readBits(BITS_PER_INT); 

                // reconstruct huffman tree
                huffTree = new HuffmanTree(btIn);
                
            }

            codeMap = huffTree.createMap();

            // create a reverse mapping
            Map<String, Integer> decompressMap = new HashMap<>();

            for (int key : codeMap.keySet()) { 
                decompressMap.put(codeMap.get(key), key);
            }

            // read compressed data and write out decompressed file
            String currentPath = "";
            String pseudoEofCode = codeMap.get(IHuffConstants.PSEUDO_EOF);

            // read compressed data until PseudoEOF is reached
            while(currentPath != pseudoEofCode) { // (TODO: have to check for if read -1)
                
                // if a valid code, write out the character
                if (decompressMap.keySet().contains(currentPath)) {
                    btOut.writeBits(BITS_PER_WORD, decompressMap.get(currentPath)); 
                    bitsWritten += BITS_PER_WORD;
                    currentPath = "";
                }

                int currentBit = btIn.readBits(1); 
                currentPath += currentBit;
                
            }
            
            return bitsWritten;
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s){
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}
