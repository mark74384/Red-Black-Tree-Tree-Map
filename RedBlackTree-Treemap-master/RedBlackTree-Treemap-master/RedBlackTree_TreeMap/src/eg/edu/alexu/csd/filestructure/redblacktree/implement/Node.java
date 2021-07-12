package eg.edu.alexu.csd.filestructure.redblacktree.implement;

import eg.edu.alexu.csd.filestructure.redblacktree.INode;

public class Node<T extends Comparable<T>, V> implements INode<T, V> {

	private T key;
	private V value;
	private boolean isRed;
	private INode<T, V> parent;
	private INode<T, V> leftChild;
	private INode<T, V> rightChild;

	public Node() {
	}

	public Node(T key, V vale, INode<T, V> parent) {
		this.key = key;
		this.value = vale;
		this.isRed = true;
		this.parent = parent;
	}

	@Override
	public void setParent(INode<T, V> parent) {
		this.parent = parent;
	}

	@Override
	public INode<T, V> getParent() {
		return this.parent;
	}

	@Override
	public void setLeftChild(INode<T, V> leftChild) {
		this.leftChild = leftChild;
	}

	@Override
	public INode<T, V> getLeftChild() {
		return this.leftChild;
	}

	@Override
	public void setRightChild(INode<T, V> rightChild) {
		this.rightChild = rightChild;
	}

	@Override
	public INode<T, V> getRightChild() {
		return this.rightChild;
	}

	@Override
	public T getKey() {
		return this.key;
	}

	@Override
	public void setKey(T key) {
		this.key = key;
	}

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public boolean getColor() {
		return isRed;
	}

	@Override
	public void setColor(boolean color) {
		this.isRed = color;
	}

	@Override
	public boolean isNull() {
		return this.key == null;
	}

	@Override
	public String toString() {
		String color = (this.isRed) ? "red" : "black";
		return "[" + key + ", " + value + ", " + color + "]";
	}
}
