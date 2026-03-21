public class Solution implements Algs202526Tick2 {
    private boolean blackDeleted = false;

    public RBNode insert(RBNode root, int key){
        // Insert into tree
        root = insertAndFix(root, key);
        // Assert root be black
        root.colour = RBNode.BLACK;

        return root;
    } // returns new root pointer

    private RBNode insertAndFix(RBNode root, int key){
        if (root == null) return initRBNode(key, RBNode.RED);

        // Should be in right subtree
        if (key > root.key) root.right = insertAndFix(root.right, key);
        // Should be in left subtree
        if (key < root.key) root.left = insertAndFix(root.left, key);

        // Now assert that root.left and root.right are valid
        // apart from if they contained a double red; hence fix and propagate up.

        if (!isRed(root)){
            if (isRed(root.left) && isRed(root.right)){
                // n, p, u red and g black
                root.left.colour = RBNode.BLACK;
                root.right.colour = RBNode.BLACK;
                root.colour = RBNode.RED;
            }

            if (isRed(root.left) && isRed(root.left.right) && !isRed(root.right)){
                // n, p red and g, u black - Triangle shape
                root.left = rotateLeft(root.left);
            }
            // Mirror image
            else if (isRed(root.right) && isRed(root.right.left) && !isRed(root.left)){
                // n, p red and g, u black - Triangle shape
                root.right = rotateRight(root.right);
            }

            if (isRed(root.left) && isRed(root.left.left) && !isRed(root.right)){
                // n, p red and g, u black - line shape
                root.left.colour = RBNode.BLACK;
                root.colour= RBNode.RED;
                root = rotateRight(root);
            }
            // Mirror image
            else if (isRed(root.right) && isRed(root.right.right) && !isRed(root.left)){
                // n, p red and g, u black - line shape
                root.right.colour = RBNode.BLACK;
                root.colour= RBNode.RED;
                root = rotateLeft(root);
            }
        }

        return root;
    }

    private boolean isRed(RBNode node){
        return node != null && node.colour == RBNode.RED;
    }

    private RBNode rotateLeft(RBNode node){
        // Assert that output tree will still be valid
        // if input tree is valid
        RBNode parent = node.right;
        RBNode middle = node.right.left;

        node.right = middle;
        parent.left = node;

        return parent;
    }

    private RBNode rotateRight(RBNode node){
        // Assert that output tree will still be valid
        // if input tree is valid
        RBNode parent = node.left;
        RBNode middle = node.left.right;

        node.left = middle;
        parent.right = node;

        return parent;
    }

    private RBNode getSuccessor(RBNode node){
        assert node.right != null;

        RBNode current = node.right;
        while (current.left != null) current = current.left;
        return current;
    }

    public RBNode delete(RBNode root, int key){
        // Delete the element
        root = deleteInner(root, key);

        // Enforce root be black - if null then already black
        if (root != null) root.colour = RBNode.BLACK;
        blackDeleted = false;
        return root;
    }

    private RBNode deleteInner(RBNode root, int key){
        // If empty tree then nothing to delete
        if (root == null) return null;

        // If this is the node to delete, then delete and leave fixing to parent
        if (root.key == key) {
            // If no children, simply delete it and leave correcting RB-tree property to parent
            if (root.right == null && root.left == null){
                if (!isRed(root)) blackDeleted = true;
                return null;
            }

            // One child necessarily means child must be red and node must be black (no other case valid)
            // (Right child only) If one child, simply replace with that child and color black
            if (root.right != null && root.left == null){
                root.right.colour = RBNode.BLACK;
                return root.right;
            }

            // (Left child only) If one child, simply replace with that child and color black
            if (root.right == null && root.left != null){
                root.left.colour = RBNode.BLACK;
                return root.left;
            }

            // Otherwise 2 children, hence replace with successor, delete successor
            else {
                RBNode successor = getSuccessor(root);

                root.right = deleteInner(root.right, successor.key);
                root.key = successor.key;

                root = deleteFixUp(root, true);
                return root;
            }
        }

        // Node to delete in right subtree
        if (key > root.key) {
            if (root.right != null){
                root.right = deleteInner(root.right, key);
                root = deleteFixUp(root, true);
            } else {
                // Not in tree so nothing to do
                return root;
            }
        }

        // Node to delete in left subtree
        else {
            if (root.left != null){
                root.left = deleteInner(root.left, key);
                root = deleteFixUp(root, false);
            } else {
                // Not in tree so nothing to do
                return root;
            }
        }

        return root;
    } // returns new root pointer

    private RBNode deleteFixUp(RBNode root, boolean onRight){
        // If a black wasn't deleted then no need to fix up
        if (!blackDeleted) return root;

        // Case 1: Sibling is red - do rotation and color change
        if (onRight && isRed(root.left)){
            root.colour = RBNode.RED;
            root.left.colour = RBNode.BLACK;
            root = rotateRight(root);

            root.right = deleteFixUp(root.right, onRight);
            return root;
        } // Case 1 mirror image
        else if (!onRight && isRed(root.right)){
            root.colour = RBNode.RED;
            root.right.colour = RBNode.BLACK;
            root = rotateLeft(root);

            root.left = deleteFixUp(root.left, onRight);
            return root;
        }
        // Now ASSERT that right child is black - now apply other cases

        // Case 2: Both of siblings children are black
        if (!onRight && !isRed(root.right.left) && !isRed(root.right.right)) {
            root.right.colour = RBNode.RED;

            // If root red flip colours of sibling and parent, and now we are done.
            // Return, and end fixing procedure.
            // Otherwise, whole subtree is one black short not just right subtree, hence
            // return and leave fixing to the parent
            if (isRed(root)){
                root.colour = RBNode.BLACK;
                blackDeleted = false;
            }
        } // Case 2 mirror image
        else if (onRight && !isRed(root.left.left) && !isRed(root.left.right)) {
            root.left.colour = RBNode.RED;
            if (isRed(root)){
                root.colour = RBNode.BLACK;
                blackDeleted = false;
            }
        } else {
            // Case 3: Siblings right child is black and left is red
            if (!onRight && !isRed(root.right.right) && isRed(root.right.left)) {
                root.right.left.colour = RBNode.BLACK;
                root.right.colour = RBNode.RED;
                root.right = rotateRight(root.right);
                // Now ASSERT that root-sibling-child line will be ?-black-red
                // i.e. triangle converted to line
            } // Case 3 Mirror image
            else if (onRight && !isRed(root.left.left) && isRed(root.left.right)) {
                root.left.right.colour = RBNode.BLACK;
                root.left.colour = RBNode.RED;
                root.left = rotateLeft(root.left);
            }

            // ASSERT right child red
            // Case 4: sibling black and right child red
            if (!onRight) {
                // Essentially case 4 is the MAIN fix
                root.right.colour = root.colour;
                root.colour = RBNode.BLACK;
                root.right.right.colour = RBNode.BLACK;
                root = rotateLeft(root);
            } // Case 4 mirror image
            else {
                root.left.colour = root.colour;
                root.colour = RBNode.BLACK;
                root.left.left.colour = RBNode.BLACK;
                root = rotateRight(root);

            }

            // Case 4 has rectified the black height property
            blackDeleted = false;
        }

        return root;
    }

    private RBNode initRBNode(int key, boolean colour){
        RBNode newRoot =  new RBNode();
        newRoot.colour = colour;
        newRoot.key = key;
        return newRoot;
    }
}

