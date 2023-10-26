import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Your implementation of an AVL.
 *
 * @author Quang Nguyen
 * @version 1.0
 * @userid YOUR USER ID HERE qnguyen305
 * @GTID YOUR GT ID HERE 903770019
 *
 * Collaborators: LIST ALL COLLABORATORS YOU WORKED WITH HERE
 *
 * Resources: LIST ALL NON-COURSE RESOURCES YOU CONSULTED HERE
 */
public class AVL<T extends Comparable<? super T>> {

    // Do not add new instance variables or modify existing ones.
    private AVLNode<T> root;
    private int size;

    /**
     * Constructs a new AVL.
     *
     * This constructor should initialize an empty AVL.
     *
     * Since instance variables are initialized to their default values, there
     * is no need to do anything for this constructor.
     */
    public AVL() {
        // DO NOT IMPLEMENT THIS CONSTRUCTOR!
    }

    /**
     * Constructs a new AVL.
     *
     * This constructor should initialize the AVL with the data in the
     * Collection. The data should be added in the same order it is in the
     * Collection.
     *
     * @param data the data to add to the tree
     * @throws java.lang.IllegalArgumentException if data or any element in data
     *                                            is null
     */
    public AVL(Collection<T> data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        for (var d : data) {
            if (d == null) {
                throw new IllegalArgumentException("element is null");
            }
            add(d);
        }
    }

    /**
     * Adds the element to the tree.
     *
     * Start by adding it as a leaf like in a regular BST and then rotate the
     * tree as necessary.
     *
     * If the data is already in the tree, then nothing should be done (the
     * duplicate shouldn't get added, and size should not be incremented).
     *
     * Remember to recalculate heights and balance factors while going back
     * up the tree after adding the element, making sure to rebalance if
     * necessary.
     *
     * Hint: Should you use value equality or reference equality?
     *
     * @param data the data to add
     * @throws java.lang.IllegalArgumentException if data is null
     */
    public void add(T data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }

        this.root = addRecursive(data, root);
    }

    /**
     *
     * @param data the data
     * @param currNode current node
     * @return current node
     */
    private AVLNode<T> addRecursive(T data, AVLNode<T> currNode) {
        if (currNode == null) {
            this.size++;
            AVLNode<T> newNode = new AVLNode<>(data);
            update(newNode);
            currNode = newNode;
            return currNode;
        } else if (data.compareTo(currNode.getData()) < 0) {
            currNode.setLeft(addRecursive(data, currNode.getLeft()));
            update(currNode);
            currNode = rotate(currNode);

        } else if (data.compareTo(currNode.getData()) > 0) {
            currNode.setRight(addRecursive(data, currNode.getRight()));
            update(currNode);
            currNode = rotate(currNode);
        }
        update(currNode);
        currNode = rotate(currNode);
        return currNode;
    }

    /**
     * Update height and balance factor for the node
     * @param currNode current node
     */
    private void update(AVLNode<T> currNode) {
        int leftHeight = calcHeight(currNode.getLeft());
        int rightHeight = calcHeight(currNode.getRight());

        currNode.setHeight(Math.max(leftHeight, rightHeight) + 1);
        currNode.setBalanceFactor(leftHeight - rightHeight);
    }

    /**
     * calculate the height
     * @param currNode current node
     * @return the height
     */
    private int calcHeight(AVLNode<T> currNode) {
        if (currNode == null) {
            return -1;
        }
        return currNode.getHeight();
    }

    /**
     * Rotation cases
     * @param currNode current node
     * @return node
     */
    private AVLNode<T> rotate(AVLNode<T> currNode) {
        if (currNode.getBalanceFactor() > 1) {
            if (currNode.getLeft().getBalanceFactor() >= 0) {
                currNode = rightRotation(currNode);
            } else {
                currNode.setLeft(leftRotation(currNode.getLeft()));
                currNode = rightRotation(currNode);
            }
        } else if (currNode.getBalanceFactor() < -1) {
            if (currNode.getRight().getBalanceFactor() <= 0) {
                currNode = leftRotation(currNode);
            } else {
                currNode.setRight(rightRotation(currNode.getRight()));
                currNode = leftRotation(currNode);
            }
        }

        return currNode;
    }

    /**
     * Left rotation
     * @param currNode current node
     * @return node
     */
    private AVLNode<T> leftRotation(AVLNode<T> currNode) {
        AVLNode<T> temp = currNode.getRight();
        currNode.setRight(temp.getLeft());
        temp.setLeft(currNode);
        update(currNode);
        update(temp);
        return temp;
    }

    /**
     *Right rotation
     * @param currNode current node
     * @return node
     */
    private AVLNode<T> rightRotation(AVLNode<T> currNode) {
        AVLNode<T> temp = currNode.getLeft();
        currNode.setLeft(temp.getRight());
        temp.setRight(currNode);
        update(currNode);
        update(temp);
        return temp;
    }

    /**
     * Removes and returns the element from the tree matching the given
     * parameter.
     *
     * There are 3 cases to consider:
     * 1: The node containing the data is a leaf (no children). In this case,
     * simply remove it.
     * 2: The node containing the data has one child. In this case, simply
     * replace it with its child.
     * 3: The node containing the data has 2 children. Use the predecessor to
     * replace the data, NOT successor. As a reminder, rotations can occur
     * after removing the predecessor node.
     *
     * Remember to recalculate heights and balance factors while going back
     * up the tree after removing the element, making sure to rebalance if
     * necessary.
     *
     * Do not return the same data that was passed in. Return the data that
     * was stored in the tree.
     *
     * Hint: Should you use value equality or reference equality?
     *
     * @param data the data to remove
     * @return the data that was removed
     * @throws java.lang.IllegalArgumentException if data is null
     * @throws java.util.NoSuchElementException   if the data is not found
     */
    public T remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }

        AVLNode<T> dummy = new AVLNode<>(null);
        this.root = removeRecursive(data, this.root, dummy);
        if (dummy.getData() == null) {
            throw new NoSuchElementException("data is not found");
        }

        return dummy.getData();
    }

    /**
     *
     * @param data data
     * @param currNode current node
     * @param dummy dummy node
     * @return current node
     */
    private AVLNode<T> removeRecursive(T data, AVLNode<T> currNode, AVLNode<T> dummy) {
        if (currNode == null) {
            return null;
        } else if (data.compareTo(currNode.getData()) < 0) {
            currNode.setLeft(removeRecursive(data, currNode.getLeft(), dummy));
            update(currNode);
            currNode = rotate(currNode);
        } else if (data.compareTo(currNode.getData()) > 0) {
            currNode.setRight(removeRecursive(data, currNode.getRight(), dummy));
            update(currNode);
            currNode = rotate(currNode);
        } else {
            this.size--;
            dummy.setData(currNode.getData());
            if (currNode.getLeft() == null && currNode.getRight() == null) {
                return null;
            } else if (currNode.getLeft() == null) {
                return currNode.getRight();
            } else if (currNode.getRight() == null) {
                return currNode.getLeft();
            } else {
                AVLNode<T> dum = new AVLNode<>(null);
                currNode.setLeft(removePredecessor(currNode.getLeft(), dum));
                currNode.setData(dum.getData());
                update(currNode);
                currNode = rotate(currNode);
            }
        }
        update(currNode);
        currNode = rotate(currNode);
        return currNode;
    }

    /**
     *
     * @param currNode current node
     * @param dummy dummy node
     * @return current node
     */
    private AVLNode<T> removePredecessor(AVLNode<T> currNode, AVLNode<T> dummy) {
        if (currNode.getRight() == null) {
            dummy.setData(currNode.getData());
            return currNode.getLeft();
        } else {
            currNode.setRight(removePredecessor(currNode.getRight(), dummy));
            update(currNode);
            currNode = rotate(currNode);
            return currNode;
        }
    }

    /**
     * Returns the element from the tree matching the given parameter.
     *
     * Hint: Should you use value equality or reference equality?
     *
     * Do not return the same data that was passed in. Return the data that
     * was stored in the tree.
     *
     * @param data the data to search for in the tree
     * @return the data in the tree equal to the parameter
     * @throws java.lang.IllegalArgumentException if data is null
     * @throws java.util.NoSuchElementException   if the data is not in the tree
     */
    public T get(T data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        T d = findRecursive(data, root);
        if (d == null) {
            throw new NoSuchElementException("data is not in the tree");
        }
        return d;
    }

    /**
     * find node
     * @param data the data
     * @param currNode  the current node
     * @return the data
     */
    private T findRecursive(T data, AVLNode<T> currNode) {
        if (data.compareTo(currNode.getData()) == 0) {
            return currNode.getData();
        } else if (data.compareTo(currNode.getData()) < 0 && currNode.getLeft() != null) {
            return findRecursive(data, currNode.getLeft());
        } else if (data.compareTo(currNode.getData()) > 0 && currNode.getRight() != null) {
            return findRecursive(data, currNode.getRight());
        }

        return null;
    }

    /**
     * Returns whether or not data matching the given parameter is contained
     * within the tree.
     *
     * Hint: Should you use value equality or reference equality?
     *
     * @param data the data to search for in the tree.
     * @return true if the parameter is contained within the tree, false
     * otherwise
     * @throws java.lang.IllegalArgumentException if data is null
     */
    public boolean contains(T data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }

        return findRecursive(data, this.root) != null;
    }

    /**
     * Returns the height of the root of the tree.
     *
     * Should be O(1).
     *
     * @return the height of the root of the tree, -1 if the tree is empty
     */
    public int height() {
        if (size == 0) {
            return -1;
        }

        return this.root.getHeight();
    }

    /**
     * Clears the tree.
     *
     * Clears all data and resets the size.
     */
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    /**
     * The predecessor is the largest node that is smaller than the current data.
     *
     * Should be recursive.
     *
     * This method should retrieve (but not remove) the predecessor of the data
     * passed in. There are 2 cases to consider:
     * 1: The left subtree is non-empty. In this case, the predecessor is the
     * rightmost node of the left subtree.
     * 2: The left subtree is empty. In this case, the predecessor is the lowest
     * ancestor of the node containing data whose right child is also
     * an ancestor of data.
     *
     * This should NOT be used in the remove method.
     *
     * Ex:
     * Given the following AVL composed of Integers
     *     76
     *   /    \
     * 34      90
     *  \    /
     *  40  81
     * predecessor(76) should return 40
     * predecessor(81) should return 76
     *
     * @param data the data to find the predecessor of
     * @return the predecessor of data. If there is no smaller data than the
     * one given, return null.
     * @throws java.lang.IllegalArgumentException if the data is null
     * @throws java.util.NoSuchElementException   if the data is not in the tree
     */
    public T predecessor(T data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }

        T pred = null;
        return predecessorRecursive(data, root, pred);
    }

    /**
     * Find the predecessor recursively
     * @param data the data
     * @param currNode current node
     * @param predCand predecessor Candidate for case 2
     * @return the data
     */
    private T predecessorRecursive(T data, AVLNode<T> currNode, T predCand) {
        if (currNode == null) {
            throw new NoSuchElementException("data is not in the tree");
        }

        if (data.compareTo(currNode.getData()) < 0) {
            return predecessorRecursive(data, currNode.getLeft(), predCand);
        } else if (data.compareTo(currNode.getData()) > 0) {
            predCand = currNode.getData();
            return predecessorRecursive(data, currNode.getRight(), predCand);
        } else {
            if (currNode.getLeft() != null) {
                return predecessorCase1(currNode.getLeft()).getData();
            }
            return predCand;
        }
    }

    /**
     *Predecessor case 1
     * @param currNode current node
     * @return the predecessor
     */
    private AVLNode<T> predecessorCase1(AVLNode<T> currNode) {
        if (currNode.getRight() == null) {
            return currNode;
        } else {
            return predecessorCase1(currNode.getRight());
        }
    }


    /**
     * Returns the data in the deepest node. If there is more than one node
     * with the same deepest depth, return the rightmost (i.e. largest) node with
     * the deepest depth.
     *
     * Should be recursive.
     *
     * Must run in O(log n) for all cases.
     *
     * Example
     * Tree:
     *           2
     *        /    \
     *       0      3
     *        \
     *         1
     * Max Deepest Node:
     * 1 because it is the deepest node
     *
     * Example
     * Tree:
     *           2
     *        /    \
     *       0      4
     *        \    /
     *         1  3
     * Max Deepest Node:
     * 3 because it is the maximum deepest node (1 has the same depth but 3 > 1)
     *
     * @return the data in the maximum deepest node or null if the tree is empty
     */
    public T maxDeepestNode() {
        if (size == 0) {
            return null;
        }

        return maxDeepestNodeRecursively(root);
    }

    /**
     * @param currNode current node
     * @return the deepest node
     */
    private T maxDeepestNodeRecursively(AVLNode<T> currNode) {
        if (currNode.getLeft() == null && currNode.getRight() == null) {
            return currNode.getData();
        } else if (currNode.getBalanceFactor() >= 1) {
            return maxDeepestNodeRecursively(currNode.getLeft());
        } else if (currNode.getBalanceFactor() <= 0) {
            return maxDeepestNodeRecursively(currNode.getRight());
        }
        return null;
    }

    /**
     * Returns the root of the tree.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the root of the tree
     */
    public AVLNode<T> getRoot() {
        // DO NOT MODIFY THIS METHOD!
        return root;
    }

    /**
     * Returns the size of the tree.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the size of the tree
     */
    public int size() {
        // DO NOT MODIFY THIS METHOD!
        return size;
    }
}
