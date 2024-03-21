/**
 * Class for a Node containing a key-value mapping. Each node contains a
 * reference to the left child, right child, and parent.
 * @author Brian S. Borowski
 * @version 1.0 October 19, 2022
 */
public class Node<K, V> {
    Node<K, V> left, right, parent;
    K key;
    V value;

    /**
     * Creates a node with a key-value mapping.
     */
    public Node(K key, V Value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns a String representation of the Node with the key and value
     * inside angled brackets.
     * @return a String representation of the Node
     */
    public String toString() {
        return "<" + key + ", " + value + ">";
    }
}
