public class BinarySearchTree<T extends Comparable<T>> extends BinaryTree<T> {

    /* Creates an empty BST. */
    public BinarySearchTree() {
        super();
    }

    /* Creates a BST with root as ROOT. */
    public BinarySearchTree(TreeNode root) {
        super(root);

    }

    /* Returns true if the BST contains the given KEY. */
    public boolean contains(T key) {
        if (root == null) {
            return false;
        } else {
            return containsHelper(root, key);
        }
    }

    /* Adds a node for KEY iff KEY isn't in the BST already. */
    public void add(T key) {
        root = addHelper(root, key);
    }

    /* Deletes a node from the BST. */
    public T delete(T key) {
        TreeNode parent = null;
        TreeNode curr = root;
        TreeNode delNode = null;
        TreeNode replacement = null;
        boolean rightSide = false;

        while (curr != null && !curr.item.equals(key)) {
            if (((Comparable<T>) curr.item).compareTo(key) > 0) {
                parent = curr;
                curr = curr.left;
                rightSide = false;
            } else {
                parent = curr;
                curr = curr.right;
                rightSide = true;
            }
        }
        delNode = curr;
        if (curr == null) {
            return null;
        }

        if (delNode.right == null) {
            if (root == delNode) {
                root = root.left;
            } else {
                if (rightSide) {
                    parent.right = delNode.left;
                } else {
                    parent.left = delNode.left;
                }
            }
        } else {
            curr = delNode.right;
            replacement = curr.left;
            if (replacement == null) {
                replacement = curr;
            } else {
                while (replacement.left != null) {
                    curr = replacement;
                    replacement = replacement.left;
                }
                curr.left = replacement.right;
                replacement.right = delNode.right;
            }
            replacement.left = delNode.left;
            if (root == delNode) {
                root = replacement;
            } else {
                if (rightSide) {
                    parent.right = replacement;
                } else {
                    parent.left = replacement;
                }
            }
        }
        return delNode.item;
    }
    public boolean containsHelper(TreeNode node, T key) {
        if (key.compareTo(node.item) == 0) {
            return true;
        } else {
            if (key.compareTo(node.item) < 0 && node.left != null) {
                TreeNode anode = node.left;
                return containsHelper(anode, key);
            }
            if (key.compareTo(node.item) > 0 && node.right != null) {
                TreeNode anode = node.right;
                return containsHelper(anode, key);
            }
        }
        return false;
    }

    public TreeNode addHelper(TreeNode node, T key) {
        if (node == null) {
            node = new TreeNode(key);
            return node;
        }
        if (key.compareTo(node.item) < 0) {
            node.left = addHelper(node.left, key);
        } else if (key.compareTo(node.item) > 0) {
            node.right = addHelper(node.right, key);
        }
        return node;
    }
}
