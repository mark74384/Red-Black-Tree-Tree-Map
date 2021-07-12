package eg.edu.alexu.csd.filestructure.redblacktree.implement;

import javax.management.RuntimeErrorException;

import eg.edu.alexu.csd.filestructure.redblacktree.INode;
import eg.edu.alexu.csd.filestructure.redblacktree.IRedBlackTree;

public class RedBlackTree<T extends Comparable<T>, V> implements IRedBlackTree<T, V> {

	private INode<T, V> nill;
	private INode<T, V> root;

	public RedBlackTree() {
		nill = new Node<T, V>();
		nill.setColor(false);
		root = nill;
	}

	@Override
	public INode<T, V> getRoot() {
		return this.root;
	}

	@Override
	public boolean isEmpty() {
		return this.root == nill;
	}

	@Override
	public void clear() {
		this.root = nill;
	}

	@Override
	public V search(T key) {
		if (key == null)
			throw new RuntimeErrorException(null, "Error, null key");
		
		INode<T, V> curr = this.root;
		while (curr != nill) {
			if (key.compareTo(curr.getKey()) < 0) // key < current.key
				curr = curr.getLeftChild();
			else if (key.compareTo(curr.getKey()) > 0) // key > current.key
				curr = curr.getRightChild();
			else // key == current.key
				return curr.getValue();
		}
		return null;
	}

	@Override
	public boolean contains(T key) {
		return search(key) != null;
	}

	@Override
	public void insert(T key, V value) {
		if (key == null)
			throw new RuntimeErrorException(null, "Error, null key");
		if (value == null)
			throw new RuntimeErrorException(null, "Error, null value");
		/*** insert in suitable place like any BST ***/

		// if empty tree, easy
		if (isEmpty()) {
			root = new Node<T, V>(key, value, null);
			root.setColor(false);
			root.setLeftChild(nill);
			root.setRightChild(nill);
			return;
		}

		INode<T, V> curr = this.root;
		boolean flag = true;

		while (flag) {
			if (key.compareTo(curr.getKey()) < 0) { // key < current.key
				if (curr.getLeftChild() == nill) {
					curr.setLeftChild(new Node<T, V>(key, value, curr));
					flag = false;
				}
				curr = curr.getLeftChild();
			} else if (key.compareTo(curr.getKey()) > 0) { // key > current.key
				if (curr.getRightChild() == nill) {
					curr.setRightChild(new Node<T, V>(key, value, curr));
					flag = false;
				}
				curr = curr.getRightChild();
			} else { // key == current.key
				curr.setValue(value);
				return;
			}
		}
		curr.setLeftChild(nill);
		curr.setRightChild(nill);
		if (curr.getParent().getParent() == null)
			return;

		/*** fix violations ***/

		while (curr.getParent().getColor()) {
			if (curr.getParent() == curr.getParent().getParent().getRightChild()) {
				INode<T, V> y = curr.getParent().getParent().getLeftChild();
				if (y.getColor()) {
					curr.getParent().setColor(false);
					y.setColor(false);
					curr.getParent().getParent().setColor(true);
					curr = curr.getParent().getParent();
				} else {
					if (curr == curr.getParent().getLeftChild()) {
						curr = curr.getParent();
						right_rotate(curr);
					}
					curr.getParent().setColor(false);
					curr.getParent().getParent().setColor(true);
					left_rotate(curr.getParent().getParent());
				}
			} else {
				INode<T, V> y = curr.getParent().getParent().getRightChild();
				if (y.getColor()) {
					curr.getParent().setColor(false);
					y.setColor(false);
					curr.getParent().getParent().setColor(true);
					curr = curr.getParent().getParent();
				} else {
					if (curr == curr.getParent().getRightChild()) {
						curr = curr.getParent();
						left_rotate(curr);
					}
					curr.getParent().setColor(false);
					curr.getParent().getParent().setColor(true);
					right_rotate(curr.getParent().getParent());
				}
			}
			if (curr == root)
				break;
		}
		root.setColor(false);
	}

	@Override
	public boolean delete(T key) {
		if (key == null)
			throw new RuntimeErrorException(null, "Error, null key");
		if (this.isEmpty())
			throw new RuntimeErrorException(null, "Error, deleting from empty tree");

		boolean check = deletNode(root, key);
		return check;
	}

	private boolean deletNode(INode<T, V> node, T key) {
		INode<T, V> z = nill;
		INode<T, V> x, y;
		// we need first to get the node.
		while (node != nill) {
			if (key.compareTo(node.getKey()) == 0) {
				z = node;
			}
			if (key.compareTo(node.getKey()) < 0) {
				node = node.getLeftChild();
			} else {
				node = node.getRightChild();
			}
		}
		if (z == nill) { // if not found print message.
			return false;
		}

		y = z;
		boolean yOriginalColor = y.getColor();
		if (z.getLeftChild() == nill) { // if it has no left children then replace z by the right child.
			x = z.getRightChild();
			rbTransplant(z, z.getRightChild());
		} else if (z.getRightChild() == nill) { // if it has no right children then replace z by the left child.
			x = z.getLeftChild();
			rbTransplant(z, z.getLeftChild());
		} else { // in case it have right and left child
			y = minimum(z.getRightChild()); // get the minimum child in right branch.
			yOriginalColor = y.getColor();
			x = y.getRightChild();
			if (y.getParent() == z) { // in case that there is 1 node in the right subtree
				x.setParent(y);
			} else { // in case of there is multiple node in the right subtree
				/*
				 * first fix the pointers between nill>>x<< node and the parent of y because we
				 * move y to take the place of z. x always is nill node.
				 */
				rbTransplant(y, y.getRightChild());
				y.setRightChild(z.getRightChild());
				y.getRightChild().setParent(y);
			}
			/*
			 * fix the pointers between y node and the parent of z & all child of z.
			 */
			rbTransplant(z, y);
			y.setLeftChild(z.getLeftChild());
			y.getLeftChild().setParent(y);
			y.setColor(z.getColor());
		}
		/*
		 * this to fix the balanced of the tree in case of the deletion node is't red
		 */
		if (!yOriginalColor) { // may occur error.
			fixDelete(x);
		}
		return true;
	}

	private void fixDelete(INode<T, V> x) {
		INode<T, V> s;
		while ((x != root) && (!x.getColor())) {
			if (x == x.getParent().getLeftChild()) { // case that x is left child.
				s = x.getParent().getRightChild(); // s is the sibling of x.
				if (s.getColor()) { // if s is red then case 3 occur.
					s.setColor(false); // re color s & x.
					x.getParent().setColor(true);
					left_rotate(x.getParent()); // left rotate.
					s = x.getParent().getRightChild(); // change pointer of s after the rotate.
				}
				/*
				 * now we may be in case 1 or 2
				 */
				if ((!s.getLeftChild().getColor()) && (!s.getRightChild().getColor())) {
					/*
					 * if the 2 child of s is black we in case 2 S is black and both its children
					 * are black
					 */
					s.setColor(true); // re color s
					x = x.getParent();
				} else {
					/*
					 * if at least 1 is black if the right child of s is the black node then enter
					 * if condition
					 */
					if (!s.getRightChild().getColor()) {
						/*
						 * case 2 right left.
						 */
						s.getLeftChild().setColor(false);
						s.setColor(true);
						right_rotate(s);
						s = x.getParent().getRightChild();
					}

					/*
					 * case 2 right right.
					 */
					s.setColor(x.getParent().getColor());
					x.getParent().setColor(false);
					s.getRightChild().setColor(false);
					left_rotate(x.getParent());
					x = root;
				}

			}
			/*
			 * case that x is right child. same as the previous iteration but swap from left
			 * to right.
			 */
			else {
				s = x.getParent().getLeftChild();
				if (s.getColor()) {
					s.setColor(false);
					x.getParent().setColor(true);
					right_rotate(x.getParent());
					s = x.getParent().getLeftChild();
				}
				if ((!s.getLeftChild().getColor()) && (!s.getRightChild().getColor())) {
					s.setColor(true);
					x = x.getParent();
				} else {
					if (!s.getLeftChild().getColor()) {
						s.getRightChild().setColor(false);
						s.setColor(true);
						left_rotate(s);
						s = x.getParent().getLeftChild();
					}

					s.setColor(x.getParent().getColor());
					x.getParent().setColor(false);
					s.getLeftChild().setColor(false);
					right_rotate(x.getParent());
					x = root;
				}
			}
		}
		x.setColor(false);
	}

	/*
	 * to get the minimum element in the right subtree.
	 */
	private INode<T, V> minimum(INode<T, V> node) {
		while (node.getLeftChild() != nill) {
			node = node.getLeftChild();
		}
		return node;
	}

	private void rbTransplant(INode<T, V> u, INode<T, V> v) {
		if (u.getParent() == null) { // in case that we delete the root.
			root = v;
		} else if (u == u.getParent().getLeftChild()) { // in case that u is the left child of the parent change
														// pointer.
			u.getParent().setLeftChild(v);
		} else { // in case that u is the right child of the parent change pointer.
			u.getParent().setRightChild(v);
		}
		v.setParent(u.getParent()); // fix the pointer of the new child to the parent of u.
	}

	private void right_rotate(INode<T, V> x) {
		INode<T, V> y = x.getLeftChild();
		x.setLeftChild(y.getRightChild());
		if (y.getRightChild() != nill)
			y.getRightChild().setParent(x);
		y.setParent(x.getParent());
		if (x.getParent() == null)
			this.root = y;
		else if (x == x.getParent().getRightChild())
			x.getParent().setRightChild(y);
		else
			x.getParent().setLeftChild(y);
		y.setRightChild(x);
		x.setParent(y);
	}

	private void left_rotate(INode<T, V> x) {
		INode<T, V> y = x.getRightChild();
		x.setRightChild(y.getLeftChild());
		if (y.getLeftChild() != nill)
			y.getLeftChild().setParent(x);
		y.setParent(x.getParent());
		if (x.getParent() == null)
			this.root = y;
		else if (x == x.getParent().getLeftChild())
			x.getParent().setLeftChild(y);
		else
			x.getParent().setRightChild(y);
		y.setLeftChild(x);
		x.setParent(y);
	}

}
