package eg.edu.alexu.csd.filestructure.redblacktree.implement;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javax.management.RuntimeErrorException;

import eg.edu.alexu.csd.filestructure.redblacktree.INode;
import eg.edu.alexu.csd.filestructure.redblacktree.ITreeMap;

public class TreeMap<T extends Comparable<T>, V> implements ITreeMap<T, V> {

	Comparator<Entry<T, V>> keyComparator = new Comparator<Entry<T, V>>() {
		@Override
		public int compare(Entry<T, V> o1, Entry<T, V> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
	};
	Set<Entry<T, V>> Entryset;
	Set<T> keyset;
	RedBlackTree<T, V> tree;

	public TreeMap() {
		tree = new RedBlackTree<>();
		Entryset = new TreeSet<Entry<T, V>>(keyComparator);
		keyset = new TreeSet<T>();
	}

	@Override
	public Entry<T, V> ceilingEntry(T key) {
		if (key == null)
			throw new RuntimeErrorException(null);
		if (!tree.isEmpty()) {
			INode<T, V> temp = tree.getRoot();
			INode<T, V> Parent = null;
			while (!temp.isNull()) {
				if (temp.getKey().compareTo(key) < 0) {
					if ((Parent != null) && (temp.getRightChild().isNull())) {
						return new AbstractMap.SimpleEntry<T, V>(Parent.getKey(), Parent.getValue());
					}
					temp = temp.getRightChild();
				} else if (temp.getKey().compareTo(key) == 0) {
					return new AbstractMap.SimpleEntry<T, V>(temp.getKey(), temp.getValue());
				} else {
					if (temp.getLeftChild().isNull()) {
						return new AbstractMap.SimpleEntry<T, V>(temp.getKey(), temp.getValue());
					}
					Parent = temp;
					temp = temp.getLeftChild();
				}
			}
		}

		return null;
	}

	@Override
	public T ceilingKey(T key) {
		Entry<T, V> curr = ceilingEntry(key);
		if (curr == null) {
			return null;
		} else {
			return curr.getKey();
		}
	}

	@Override
	public void clear() {
		tree = new RedBlackTree<T, V>();
		Entryset = new TreeSet<Map.Entry<T, V>>(keyComparator);
		keyset = new TreeSet<T>();
	}

	@Override
	public boolean containsKey(T key) {
		Entry<T, V> curr = ceilingEntry(key);
		if (curr == null) {
			return false;
		} else if (key.compareTo(curr.getKey()) == 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null)
			throw new RuntimeErrorException(null);
		if (tree.getRoot() == null || tree.getRoot().isNull())
			return false;

		Stack<INode<T, V>> s = new Stack<INode<T, V>>();
		INode<T, V> curr = tree.getRoot();

		// traverse the tree
		while (!curr.isNull() || s.size() > 0) {
			while (!curr.isNull()) {
				s.push(curr);
				curr = curr.getLeftChild();
			}

			curr = s.pop();

			if (curr.getValue().equals(value)) {
				return true;
			}

			curr = curr.getRightChild();
		}
		return false;
	}

	@Override
	public Set<Entry<T, V>> entrySet() {
		return Entryset;
	}

	@Override
	public Entry<T, V> firstEntry() {
		if (tree.isEmpty()) {
			return null;
		} else {
			INode<T, V> root = tree.getRoot();
			INode<T, V> child = root.getLeftChild();
			while (!child.isNull()) {
				root = root.getLeftChild();
				child = child.getLeftChild();
			}
			return new AbstractMap.SimpleEntry<T, V>(root.getKey(), root.getValue());
		}
	}

	@Override
	public T firstKey() {
		if (tree.isEmpty()) {
			return null;
		} else {
			Entry<T, V> last = firstEntry();
			return last.getKey();
		}
	}

	@Override
	public Entry<T, V> floorEntry(T key) {
		if (key == null)
			throw new RuntimeErrorException(null);
		if (!tree.isEmpty()) {
			INode<T, V> temp = tree.getRoot();
			INode<T, V> Parent = null;
			while (!temp.isNull()) {
				if (temp.getKey().compareTo(key) > 0) {
					if ((Parent != null) && (temp.getLeftChild().isNull())) {
						return new AbstractMap.SimpleEntry<T, V>(Parent.getKey(), Parent.getValue());
					}
					temp = temp.getLeftChild();
				} else if (temp.getKey().compareTo(key) == 0) {
					return new AbstractMap.SimpleEntry<T, V>(temp.getKey(), temp.getValue());
				} else {
					if (temp.getRightChild().isNull()) {
						return new AbstractMap.SimpleEntry<T, V>(temp.getKey(), temp.getValue());
					}
					Parent = temp;
					temp = temp.getRightChild();
				}
			}
		}
		return null;
	}

	@Override
	public T floorKey(T key) {
		Entry<T, V> temp = floorEntry(key);
		if (temp == null)
			return null;
		return temp.getKey();
	}

	@Override
	public V get(T key) {
		return tree.search(key);
	}

	@Override
	public ArrayList<Entry<T, V>> headMap(T toKey) {
		return headMap(toKey, false);
	}

	@Override
	public ArrayList<Entry<T, V>> headMap(T toKey, boolean inclusive) {
		if (toKey == null)
			throw new RuntimeErrorException(null);
		ArrayList<Entry<T, V>> Nodes = new ArrayList<>();
		headMapHelper(toKey, inclusive, tree.getRoot(), Nodes);
		return Nodes;
	}

	private void headMapHelper(T toKey, boolean inclusive, INode<T, V> temp, ArrayList<Entry<T, V>> nodes) {
		if (temp.isNull())
			return;
		headMapHelper(toKey, inclusive, temp.getLeftChild(), nodes);
		if (temp.getKey().compareTo(toKey) < 0) {
			nodes.add(new AbstractMap.SimpleEntry<T, V>(temp.getKey(), temp.getValue()));
			headMapHelper(toKey, inclusive, temp.getRightChild(), nodes);
		} else if (temp.getKey().compareTo(toKey) == 0) {
			if (inclusive)
				nodes.add(new AbstractMap.SimpleEntry<T, V>(temp.getKey(), temp.getValue()));
		}
	}

	@Override
	public Set<T> keySet() {
		return keyset;
	}

	@Override
	public Entry<T, V> lastEntry() {
		if (tree.isEmpty())
			return null;
		INode<T, V> temp = tree.getRoot();
		while (!temp.isNull()) {
			if (temp.getRightChild().isNull())
				return new AbstractMap.SimpleEntry<T, V>(temp.getKey(), temp.getValue());
			temp = temp.getRightChild();
		}
		return null;
	}

	@Override
	public T lastKey() {
		Entry<T, V> temp = lastEntry();
		if (temp == null)
			return null;
		return temp.getKey();
	}

	@Override
	public Entry<T, V> pollFirstEntry() {
		if (size() == 0) {
			return null;
		}
		Entry<T, V> temp = firstEntry();
		if (temp != null) {
			Entryset.remove(temp);
			keyset.remove(temp.getKey());
		}
		tree.delete(temp.getKey());
		return temp;
	}

	@Override
	public Entry<T, V> pollLastEntry() {
		if (size() == 0) {
			return null;
		}
		Entry<T, V> temp = lastEntry();
		if (temp != null) {
			Entryset.remove(temp);
			keyset.remove(temp.getKey());
		}
		tree.delete(temp.getKey());
		return temp;
	}

	@Override
	public void put(T key, V value) {
		if (containsKey(key)) {
			V val = tree.search(key);
			Entryset.remove(new AbstractMap.SimpleEntry<T, V>(key, val));
			Entryset.add(new AbstractMap.SimpleEntry<T, V>(key, value));
		} else {
			Entryset.add(new AbstractMap.SimpleEntry<T, V>(key, value));
			keyset.add(key);
		}
		tree.insert(key, value);
	}

	@Override
	public void putAll(Map<T, V> map) {
		if (map == null)
			throw new RuntimeErrorException(null);
		tree = new RedBlackTree<T, V>();
		for (Map.Entry<T, V> entry : map.entrySet()) {
			T key = entry.getKey();
			V val = entry.getValue();
			if (containsKey(key)) {
				V value = tree.search(key);
				Entryset.remove(new AbstractMap.SimpleEntry<T, V>(key, value));
				Entryset.add(new AbstractMap.SimpleEntry<T, V>(key, val));
			} else {
				Entryset.add(new AbstractMap.SimpleEntry<T, V>(key, val));
				keyset.add(key);
			}
			tree.insert(key, val);
		}
	}

	@Override
	public boolean remove(T key) {
		if (key == null)
			throw new RuntimeErrorException(null);
		if (containsKey(key)) {
			V val = tree.search(key);
			Entryset.remove(new AbstractMap.SimpleEntry<T, V>(key, val));
			keyset.remove(key);
		}
		return tree.delete(key);
	}

	@Override
	public int size() {
		return entrySet().size();
	}

	@Override
	public Collection<V> values() {
		ArrayList<V> arrayList = new ArrayList<>();
		return coll(tree.getRoot(), arrayList);
	}

	public ArrayList<V> coll(INode<T, V> node, ArrayList<V> arrayList) {
		if (node.isNull()) {
			return arrayList;
		}
		coll(node.getLeftChild(), arrayList);
		arrayList.add((V) node.getValue());
		coll(node.getRightChild(), arrayList);
		return arrayList;
	}

	@Override
	public String toString() {
		return "TreeMap [tree=" + tree + "]";
	}
}