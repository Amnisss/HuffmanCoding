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

    private IHuffViewer myViewer;
    private int header;
    private Map<Integer, String> codeMap;
    private HuffmanTree huffTree;

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
        int compressedBits = 0;
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

        compressedBits += BITS_PER_INT;
        compressedBits += BITS_PER_INT;
        
        if (headerFormat == 1) {
            bitsWritten += BITS_PER_INT * IHuffConstants.ALPH_SIZE;
        } else if (headerFormat == 2) {

        }

        for (int i = 0; i < frequencies.length; i++) {
            compressedBits += frequencies[i] * codeMap.get(i).length();
        }

        //Pseudo-EOF
        compressedBits += codeMap.get(256).length();
        
        //showString("Not working yet");
        //myViewer.update("Still not working");
        //throw new IOException("preprocess not implemented");

        return originalBits - compressedBits;
        
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
        //throw new IOException("compress is not implemented");
        BitInputStream btIn = new BitInputStream(in);
        BitOutputStream btOut = new BitOutputStream(out);
        int bitsWritten = 0;

        btOut.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.MAGIC_NUMBER);
        bitsWritten += IHuffConstants.BITS_PER_INT;
        if (header == 1) {
            btOut.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.STORE_COUNTS);
            bitsWritten += IHuffConstants.BITS_PER_INT;
        } else if (header == 2) {
            btOut.writeBits(IHuffConstants.BITS_PER_INT, IHuffConstants.STORE_TREE);
            bitsWritten += IHuffConstants.BITS_PER_INT;
        }

        // write out rest of the header
        if (header == 1) {
            for(int k=0; k < IHuffConstants.ALPH_SIZE; k++) {
                btOut.writeBits(BITS_PER_INT, frequencies[k]);
                bitsWritten += BITS_PER_INT;
            }
        } else if (header == 2) {
            btOut.writeBits(BITS_PER_INT, ____); // size of the header tree format
            preOrderTraversalHelper(huffTree.getRoot(), )

        }
        
        int code = btIn.readBits(IHuffConstants.BITS_PER_WORD);
        while (code != -1) {
            String compressed = codeMap.get(code);
            for (int i = 0; i < compressed.length(); i++) {
                btOut.write(compressed.charAt(i) - '0');
                bitsWritten++;
            }
            code = btIn.readBits(IHuffConstants.BITS_PER_WORD);
        }

        String pseudoEof = codeMap.get(256);
        for (int i = 0; i < pseudoEof.length(); i++) {
            btOut.write(pseudoEof.charAt(i) - '0');
            bitsWritten++;
        }

        return bitsWritten;
    }

    private void preOrderTraversalHelper(TreeNode n, BitOutputStream bt) {
        if (n != null) {
            if (n.isLeaf()) {
                bt.write(1);
                bt.write(BITS_PER_WORD + 1, n.data); 
            } else {
                bt.write(0);
            }
            preOrderTraversal(n.left);
            preOrderTraversal(n.right);
        }
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
	        throw new IOException("uncompress not implemented");
	        //return 0;
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
