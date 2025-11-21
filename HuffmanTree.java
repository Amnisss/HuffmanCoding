import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class HuffmanTree implements IHuffConstants{

    private TreeNode root;

    public HuffmanTree (int[] freqList) {

        PriorityQueue314<TreeNode> q = new PriorityQueue314<>();

        for (int i = 0; i < freqList.length; i++) {
            if (freqList[i] != 0) {
                TreeNode n = new TreeNode(i, freqList[i]);
                q.enqueue(n);
            }
        }

        q.enqueue(new TreeNode(IHuffConstants.PSEUDO_EOF, 1)); 

        while (q.size() > 1) {

            TreeNode n1 = q.dequeue();
            TreeNode n2 = q.dequeue();
            TreeNode combined = new TreeNode(n1, 0, n2);
            q.enqueue(combined);

        }

        root = q.dequeue();

    }

    public Map<Integer, String> createMap() {

        Map<Integer, String> result = new HashMap<>();
        createMapHelper(root, "", result);
        return result;
        
    }

    private void createMapHelper(TreeNode n, String path, Map<Integer, String> result) {
        if (n != null) {
            if (n.isLeaf()) {
                result.put(n.getValue(), path);
            }
            createMapHelper(n.getLeft(), path + "0", result);
            createMapHelper(n.getRight(), path + "1", result);
        } 
    }

    public HuffmanTree(BitInputStream btIn) throws IOException{
        root = reconstructHelper(root, btIn);
    }

    private TreeNode reconstructHelper(TreeNode n, BitInputStream btIn) throws IOException {
        int currentBit = btIn.readBits(1);
        if (currentBit == 1) {
            // leaf node
            int value = btIn.readBits(BITS_PER_WORD + 1);
            return new TreeNode(value, -1); 
        } else if (currentBit == 0) {
            // add internal node
            return new TreeNode(reconstructHelper(n.getLeft(), btIn), -1, reconstructHelper(n.getRight(), btIn));
        } else {
            // error: at the end of the file
            throw new IOException("incomplete header");
        }
    }

    public TreeNode getRoot() { // TODO unhygenic
        return root;
    }

    public void writeHeader(BitOutputStream btOut) throws IOException {
        preOrderTraversalHelper(root, btOut);
    }

    private void preOrderTraversalHelper(TreeNode n, BitOutputStream bt) throws IOException {
        if (n != null) {
            if (n.isLeaf()) {
                bt.writeBits(1,1);
                bt.writeBits(BITS_PER_WORD + 1, n.getValue()); 
            } else {
                bt.writeBits(1,0);
            }
            preOrderTraversalHelper(n.getLeft(), bt);
            preOrderTraversalHelper(n.getRight(), bt);
        }
    }


}