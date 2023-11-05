/**
 * Class that implements an AVL tree which implements the MyMap interface.
 * @author Brian S. Borowski
 * @version 1.1 October 24, 2023
 */
public class AVLTreeMap<K extends Comparable<K>, V> extends BSTMap<K, V>
        implements MyMap<K, V> {
    private static final int ALLOWED_IMBALANCE = 1;

    /**
     * Creates an empty AVL tree map.
     */
    public AVLTreeMap() { }

    public AVLTreeMap(Pair<K, V>[] elements) {
        insertElements(elements);
    }

    /**
     * Creates a AVL tree map of the given key-value pairs. If
     * sorted is true, a balanced tree will be created via a divide-and-conquer
     * approach. If sorted is false, the pairs will be inserted in the order
     * they are received, and the tree will be rotated to maintain the AVL tree
     * balance property.
     * @param elements an array of key-value pairs
     */
    public AVLTreeMap(Pair<K, V>[] elements, boolean sorted) {
        if (!sorted) {
            insertElements(elements);
        } else {
            root = createBST(elements, 0, elements.length - 1);
        }
    }

    /**
     * Recursively constructs a balanced binary search tree by inserting the
     * elements via a divide-snd-conquer approach. The middle element in the
     * array becomes the root. The middle of the left half becomes the root's
     * left child. The middle element of the right half becomes the root's right
     * child. This process continues until low > high, at which point the
     * method returns a null Node.
     * @param pairs an array of <K, V> pairs sorted by key
     * @param low   the low index of the array of elements
     * @param high  the high index of the array of elements
     * @return      the root of the balanced tree of pairs
     */
    protected Node<K, V> createBST(Pair<K, V>[] pairs, int low, int high) {
        // TODO
        // Write code to create the balanced BST, as you did in the previous assignment.
        // The node reference must be called parent.
        if (low > high) {
            return null;
        }
        int mid = low + (high - low) / 2;
        Pair<K, V> pair = pairs[mid];
        Node<K, V> parent = new Node<>(pair.key, pair.value);
        size++;
        parent.left = createBST(pairs, low, mid - 1);
        if (parent.left != null) {
            parent.left.parent = parent;
        }
        parent.right = createBST(pairs, mid + 1, high);
        if (parent.right != null) {
            parent.right.parent = parent;
        }


        // TODO
        // Write code to set the height field of the parent.
        // This line is critical for being able to add additional nodes or to
        // remove nodes. Forgetting this line leads to incorrectly balanced
        // trees.
        if(parent.left == null && parent.right == null){
            parent.height = 0;
        }
        else if(parent.left == null) {
            parent.height = Math.max(parent.right.height, -1) + 1;
        }
        else if(parent.right == null){
            parent.height = Math.max(parent.left.height, -1) + 1;
        }
        else parent.height = Math.max(parent.left.height, parent.right.height) + 1;

        return parent;
    }

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is replaced
     * by the specified value.
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    @Override
    public V put(K key, V value) {
        NodeOldValuePair nvp = new NodeOldValuePair(null, null);
        nvp = insertAndBalance(key, value, root, nvp);
        return nvp.oldValue;
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    public V remove(K key) {
        // Replace the line with the code required for proper removal from an
        // AVL tree. This task is extra credit.
        return removeHelper(key, root);
    }
    /** Helper method used to traverse recursively to the node with the key
     * to be removed
     * returns the old value of the key removed or null**/
    private V removeHelper(K key, Node<K, V> z){
        V oldValue = null;
        if(z==null){
            return oldValue;
        }
        int comparison = key.compareTo(z.key);
        if(comparison < 0){
            oldValue = removeHelper(key, z.left);
        }
        if(comparison > 0){
            oldValue = removeHelper(key, z.right);
        }
        if(comparison == 0) {
            oldValue = z.value; //store oldValue

            //remove using remove method logic
            if (z.left == null) {

                transplant(z, z.right);
            } else if (z.right == null) {

                transplant(z, z.left);
            } else {
                Node<K, V> y = treeMinimum(z.right);
                Node<K, V> startingPoint;
                if (y.parent != z) {
                    startingPoint = y.parent;
                    transplant(y, y.right);
                    y.right = z.right;
                    y.right.parent = y;
                    //used to traverse and balance from the place of the treeMinimum node
                    //back to the place where the node to remove was found
                    //only in the case where the treeMinimum's parent is not the node with
                    //the key that was removed
                    balanceHelper(startingPoint, y);
                }
                transplant(z, y);
                y.left = z.left;
                y.left.parent = y;

            }
            size--;

            //System.out.println(oldValue);
            return oldValue;
        }

        //update height
         if(z.left == null && z.right == null){
             z.height = 0;
         }
         else if(z.left == null) {
             z.height = Math.max(z.right.height, -1) + 1;
         }
         else if(z.right == null){
             z.height = Math.max(z.left.height, -1) + 1;
         }
         else z.height = Math.max(z.left.height, z.right.height) + 1;

         //balance

         balance(z);
         //System.out.println(oldValue);
         return oldValue;

     }

     /** Used to traverse recursively from the place where the treeMinimum node was
      *  found back up the tree to the place where that node was moved to. This
      *  happens in the case in which the treeMinimum's parent is not the node to be
      *  removed**/
     private void balanceHelper(Node<K, V> start, Node<K, V> end){
        if(start==end) return;
        balance(start);
        balanceHelper(start.parent, end);
     }

    private NodeOldValuePair insertAndBalance(
            K key, V value, Node<K, V> t, NodeOldValuePair nvp) {
        if (t == null) {
            size++;
            nvp.node = new Node<K, V>(key, value);
            if (root == null) {
                root = nvp.node;
            }
            return nvp;
        }
        int comparison = key.compareTo(t.key);

        // TODO
        // Complete the missing section of code here.
        if(comparison < 0){
            NodeOldValuePair left = insertAndBalance(key, value, t.left, nvp); //insertAndBalance return a
            //NodeOldValuePair not a node.
            t.left = left.node;
            t.left.parent = t;
        }
        else if (comparison > 0){
            NodeOldValuePair right = insertAndBalance(key, value, t.right, nvp);
            t.right = right.node;
            t.right.parent = t;
        }
        else{
            // The key was found in the tree. Return the previous value
            // associated with the key.
            nvp.oldValue = t.value;
            t.value = value;
        }
        if(t.left == null && t.right == null){
            t.height = 0;
        }
        else if(t.left == null) {
            t.height = Math.max(t.right.height, -1) + 1;
        }
        else if(t.right == null){
            t.height = Math.max(t.left.height, -1) + 1;
        }
        else t.height = Math.max(t.left.height, t.right.height) + 1;
        // part of original
        Node<K, V> n = balance(t);
        nvp.node = n;
        return nvp;
    }

    private Node<K, V> balance(Node<K, V> t) {
        // TODO
        if (t == null) {
            return t;
        }
        if (avlHeight(t.left) - avlHeight(t.right) > ALLOWED_IMBALANCE) {
            if (avlHeight(t.left.left) >= avlHeight(t.left.right)) {
                t = rotateWithLeftChild(t);
            }
            else t = doubleWithLeftChild(t);
        }
        else if(avlHeight(t.right) - avlHeight(t.left) > ALLOWED_IMBALANCE){
            if (avlHeight(t.right.right) >= avlHeight(t.right.left)){
                t = rotateWithRightChild(t);
            }
            else t = doubleWithRightChild(t);
        }
        t.height = Math.max(avlHeight(t.left),avlHeight(t.right))+1;
        return t;
    }

    private int avlHeight(Node<K, V> t) {
        return t == null ? -1 : t.height;
    }

    private Node<K, V> rotateWithLeftChild(Node<K, V> k2) {
        // TODO
        Node<K, V> k1 = new Node<>(null, null);
        k1 = k2.left;
        k2.left = k1.right;
        if(k2.left!=null){
            k2.left.parent=k2; ///
        }
        k1.right = k2;
        k1.parent = k2.parent;
        if(k2.parent!=null) {
            if (k2.parent.left == k2) {
                k2.parent.left = k1;
            } else {
                k2.parent.right = k1;
            }
        }
        k2.parent = k1;
        k2.height = Math.max(avlHeight(k2.left),avlHeight(k2.right))+1;
        k1.height = Math.max(avlHeight(k1.left),k2.height)+1;
        if(k1.parent==null){
            root = k1;
        }
        return k1;
    }

    private Node<K, V> rotateWithRightChild(Node<K, V> k1) {
        // TODO
        Node<K, V> k2 = new Node<K, V>(null, null);
        k2 = k1.right;
        k1.right = k2.left;
        if(k1.right!=null){
            k1.right.parent=k1; //
        }
        k2.left = k1;
        k2.parent = k1.parent;
        if (k1.parent != null) {
            if (k1.parent.left == k1) {
                k1.parent.left = k2;
            } else {
                k1.parent.right = k2;
            }
        }
        k1.parent = k2;
        k1.height = Math.max(avlHeight(k1.right), avlHeight(k1.left))+1;
        k2.height = Math.max(avlHeight(k2.right), k1.height)+1;
        if(k2.parent==null){
            root = k2;
        }
        return k2;
    }

    private Node<K, V> doubleWithLeftChild(Node<K, V> k3) {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }

    private Node<K, V> doubleWithRightChild(Node<K, V> k3) {
        k3.right = rotateWithLeftChild(k3.right);
        return rotateWithRightChild(k3);
    }

    private class NodeOldValuePair {
        Node<K, V> node;
        V oldValue;

        NodeOldValuePair(Node<K, V> n, V oldValue) {
            this.node = n;
            this.oldValue = oldValue;
        }
    }

    public static void main(String[] args) {
        boolean usingInts = true;
        if (args.length > 0) {
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                usingInts = false;
            }
        }

        AVLTreeMap avlTree;
        if (usingInts) {
            @SuppressWarnings("unchecked")
            Pair<Integer, Integer>[] pairs = new Pair[args.length];
            for (int i = 0; i < args.length; i++) {
                try {
                    int val = Integer.parseInt(args[i]);
                    pairs[i] = new Pair<>(val, val);
                } catch (NumberFormatException nfe) {
                    System.err.println("Error: Invalid integer '" + args[i]
                            + "' found at index " + i + ".");
                    System.exit(1);
                }
            }
            avlTree = new AVLTreeMap<Integer, Integer>(pairs);
        } else {
            @SuppressWarnings("unchecked")
            Pair<String, String>[] pairs = new Pair[args.length];
            for (int i = 0; i < args.length; i++) {
                pairs[i] = new Pair<>(args[i], args[i]);
            }
            avlTree = new AVLTreeMap<String, String>(pairs);
        }

        System.out.println(avlTree.toAsciiDrawing());
        System.out.println();
        System.out.println("Height:                   " + avlTree.height());
        System.out.println("Total nodes:              " + avlTree.size());
        System.out.printf("Successful search cost:   %.3f\n",
                avlTree.successfulSearchCost());
        System.out.printf("Unsuccessful search cost: %.3f\n",
                avlTree.unsuccessfulSearchCost());
        avlTree.printTraversal(PREORDER);
        avlTree.printTraversal(INORDER);
        avlTree.printTraversal(POSTORDER);
    }
}
