public class Compressor {

    private BitInputStream bt;
    private int[] freqs;
    private HuffmanTree huffTree;
    private Map<Integer, String> codeMap;

    public Compressor(BitInputStream bitStream, int[] frequencies) {
        bt = bitStream;
        freqs = frequencies;
    }

    public void createHuffTree() {
        huffTree = new HuffmanTree(frequencies);
        codeMap = huffTree.createMap();
    }

    public int getNumBits() {
        int compressedBits = 0;
        for (int n : codeMap.keySet()) {
            compressedBits += (codeMap.get(n).length() * frequencies[n]); // is this only for store counts?
        }

        // add magic number, header format number, header to compressedBits 

        return compressedBits;
    }

}