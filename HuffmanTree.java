public class HuffmanTree {

    private TreeNode root;

    public HuffmanTree (int[] freqList) {

        PriorityQueue314<TreeNode> q = new PriorityQueue314<>();

        for (int i = 0; i < freqList.length; i++) {
            if (freqList[i] != 0) {
                TreeNode n = new TreeNode(i, freqList[i]);
                q.enqueue(n);
            }
        }

        while (q.size() > 1) {

            TreeNode n1 = q.dequeue();
            TreeNode n2 = q.dequeue();
            TreeNode combined = new TreeNode(n1, n1.getFrequency() + n2.getFrequency(), n2);
            q.enqueue(combined);

        }

        root = q.dequeue();
    }

    public Map<Integer, String> createMap() {

        Map<Integer, String> result = new HashMap<>();
        createMapHelper(root, "", result);
        // pseudo IOF
        return result;
        
    }

    private void createMapHelper(TreeNode n, String path, Map<Integer, String> result) {
        if (n.isLeaf()) {
            result.put(n.getValue(), path);
        }
        createMapHelper(n.getLeft(), path + "0");
        createMapHelper(n.getRight(), path + "1");
    }
    

}