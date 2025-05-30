/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over positive integers.
 * id1:212201735
 * name1:Gad Rozen
 * username1:gadrozen
 * id2: 314621509
 * name2: Hila Etziony
 * username2: hilaetziony
 */
public class FibonacciHeap
{
	public HeapNode min;
	public int TotalLinks;
	public int Totalcuts;
	public int Size;
	public int NumTrees;

	/**
	 *
	 * Constructor to initialize an empty heap.
	 *
	 */
	public FibonacciHeap()
	{
		this.min = null;
		this.TotalLinks = 0;
		this.Totalcuts = 0;
		this.Size = 0;
		this.NumTrees = 0;
	}

	/**
	 *
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info)
	{
		HeapNode node = new HeapNode(key, info);
		this.Size += 1;
		if (this.min == null){
			node.next = node;
			node.prev = node;
			this.min = node;
			this.NumTrees += 1;
		}
		else{
			add_to_tree_linked_list(node);
			if (this.min.key > node.key){
				this.min = node;
			}
		}
		return node;
	}

	/**
	 *
	 * Return the minimal HeapNode, null if empty.
	 *
	 */
	public HeapNode findMin()
	{
		return this.min;
	}

	/**
	 *
	 * Delete the minimal item
	 *
	 */
	public void deleteMin()
	{
		delete_root(this.min);
		if (this.min != null)
			this.successive_linking();
	}

	/**
	 *
	 * pre: 0<diff<x.key
	 *
	 * Decrease the key of x by diff and fix the heap.
	 *
	 */
	public void decreaseKey(HeapNode x, int diff)
	{
		if (x == null){
			return;
		}
		decreaseKey_selective_updating_min(x,diff, true);
		return; // should be replaced by student code
	}

	/**
	 *
	 * pre: 0<diff<x.key
	 *
	 * Decrease the key of x by diff and fix the heap - update the min Node only if update_flag True.
	 *
	 */
	public void decreaseKey_selective_updating_min(HeapNode x, int diff, boolean update_flag)
	{
		// decrease key
		x.key = x.key-diff;
		// if x has parent, and he became smaller than his parent's key - do cascading cuts.
		if (x.parent != null){
			if (x.key < x.parent.key){
				cascadingCuts(x, x.parent);
			}
		}
		// update heap's min node if update_flag_is true
		if (update_flag){
			if (x.key < this.min.key) {
				this.min = x;
			}
		}
		return;
	}

	/**
	 *
	 * pre: y is x's parent.
	 *
	 * cascadingCuts(x, y) - do cascadingCuts and add the node the was cuts to the root's linked list.
	 *
	 */
	public void cascadingCuts(HeapNode x, HeapNode y) {
		HeapNode node_that_was_cut = cut(x,y);
		this.add_to_tree_linked_list(node_that_was_cut);
		this.Totalcuts += 1;
		if (y.parent != null){
			if (!y.mark){
				y.mark = true;
			}
			else{
				cascadingCuts(y, y.parent);
			}
		}
		return;
	}

	/**
	 *
	 * pre: y is x's parent.
	 *
	 * cut(x, y) - cut x from y updates the heap's structure.
	 *
	 */
	public HeapNode cut(HeapNode x, HeapNode y) {
		x.parent = null;
		x.mark = false;
		y.rank -= 1;
		// if x is an only child
		if (x.next == x){
			y.child = null;
		}
		else{
			if (y.child == x){
				y.child = x.next;
			}
			x.prev.next = x.next;
			x.next.prev = x.prev;
			x.next = null;
			x.prev = null;
		}
		return x;
	}

	/**
	 *
	 * pre: heap is not empty.
	 *
	 * add_to_tree_linked_list(node) - add node to the root's kinked list.
	 *
	 */
	public void add_to_tree_linked_list(HeapNode node){
		node.next = this.min.next;
		node.prev = this.min;
		node.parent = null;
		node.mark = false;
		this.min.next.prev = node;
		this.min.next = node;
		this.NumTrees += 1;
	}

	/**
	 *
	 * Delete the x from the heap.
	 *
	 */
	public void delete(HeapNode x)
	{
		if (x == null){
			return;
		}
		if (this.min == null){
			return;
		}

		HeapNode node = x;
		if (x == this.min){
			deleteMin();
			return;
		}
		//make x to be a root
		if(x.parent != null){
			int minkey =x.key - this.min.key + 1;
			this.decreaseKey_selective_updating_min(x , minkey,false);
		}
		delete_root(node);
	}


	/**
	 * Delete the root node x from the heap and add his children to the list of roots.
	 * @param x is a root.
	 */
	public void delete_root(HeapNode x)
	{
		if (this.min == null){
			return;
		}

		if (this.min == x){
			this.min = this.min.next;
		}
		x = cutRoot(x);
		int x_rank = x.rank;
		if (x.rank != 0){
			HeapNode node = x.child;
			x.child = null;

			//Parent reset
			while (node.parent != null){
				node.parent = null;
				node.mark = false;
				node = node.next;
			}
			//meld node to root list
			if (this.NumTrees == 0) {
				this.min = node;
			}
			else{
				HeapNode last_node_heap = this.min.prev;
				this.min.prev.next = node;
				node.prev.next = this.min;
				this.min.prev = node.prev;
				node.prev = last_node_heap;
			}
		}
		this.NumTrees += x_rank;
		this.Totalcuts+= x_rank;
		this.Size -= 1;

		//delete last node
		if (this.Size == 0){
			this.min = null;
		}
	}



	/**
	 * Perform successive linking of trees in the heap.
	 * This method links trees of the same rank in the heap and updates the minimum
	 */
	public void successive_linking()
	{
		int maxDegree = (int) Math.floor(Math.log(this.Size) / Math.log(2)) + 1;
		HeapNode[] degreeTable = new HeapNode[maxDegree];
		HeapNode current = this.min;
		int minkey = this.min.key;
		HeapNode node = this.min.next;
		//Link trees
		while (this.NumTrees != 0){
			HeapNode next = node.next;
			node = cutRoot(node);
			int rank = node.rank;

			while (degreeTable[rank] != null){
				node = this.linkTrees(node , degreeTable[rank]);
				degreeTable[rank] = null;
				rank += 1;
			}
			degreeTable[rank] = node;
			node = next;
		}
		//update min root
		for(HeapNode root : degreeTable){
			if (root != null) {
				if (root.key <= minkey){
					minkey = root.key;
					this.min = root;
				}
			}
		}
		this.NumTrees = 1;
		degreeTable[this.min.rank] = null;
		//Pull the trees out of the bucket
		for(HeapNode root : degreeTable) {
			if (root != null) {
				this.add_to_tree_linked_list(root);
			}
		}
	}
	/**
	 * Link two trees, x and y, by combining them into one tree.
	 * The method also updates the total number of links performed.
	 *
	 * @param x the root of the first tree - Assume it is cut from the list of roots.
	 * @param y the root of the second tree to be linked to x - Assume it is cut from the list of roots.
	 * @return the new root of the merged tree (which is x).
	 */
	public HeapNode linkTrees(HeapNode x, HeapNode y){
		// x.key should be lower or equal to y.key
		if (x.key > y.key) {
			HeapNode tmp = x;
			x = y;
			y = tmp;
		}

		// x has children
		if (x.child != null) {
			y.next = x.child;
			y.prev = x.child.prev;
			y.prev.next = y;
			x.child.prev = y;
		}
		x.child = y;
		x.rank += 1;
		y.parent = x;
		y.mark = false;
		this.TotalLinks += 1;
		return x;
	}
	/**
	 * Cuts a root from the root list and updates the heap's structure.

	 * @param node = the node to be cut from the root list.
	 * @return the cut node, which will be reinserted back into the heap later.
	 */
	public HeapNode cutRoot(HeapNode node){
		node.prev.next = node.next;
		node.next.prev = node.prev;
		node.next = node;
		node.prev = node;
		this.NumTrees -= 1;
		return node;
	}

	/**
	 *
	 * Return the total number of links.
	 *
	 */
	public int totalLinks()
	{
		return this.TotalLinks;
	}


	/**
	 *
	 * Return the total number of cuts.
	 *
	 */
	public int totalCuts()
	{
		return this.Totalcuts;
	}


	/**
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2)
	{
		if (heap2 == null){
			return;
		}
		//meld two roots linked lists.
		HeapNode last_node_heap = this.min.prev;
		this.min.prev.next = heap2.min;
		heap2.min.prev.next = this.min;
		this.min.prev = heap2.min.prev;
		heap2.min.prev = last_node_heap;
		// update heap's fields.
		if (this.min.key > heap2.min.key){
			this.min = heap2.min;
		}
		this.Size += heap2.Size;
		this.NumTrees += heap2.NumTrees;
		this.Totalcuts += heap2.Totalcuts;
		this.TotalLinks += heap2.TotalLinks;
		return;
	}


	/**
	 *
	 * Return the number of elements in the heap
	 *
	 */
	public int size()
	{
		return this.Size;
	}


	/**
	 *
	 * Return the number of trees in the heap.
	 *
	 */
	public int numTrees()
	{
		return this.NumTrees;
	}


	/**
	 * Class implementing a node in a Fibonacci Heap.
	 *
	 */
	public static class HeapNode{
		public int key;
		public String info;
		public HeapNode child;
		public HeapNode next;
		public HeapNode prev;
		public HeapNode parent;
		public int rank;
		public boolean mark;

		/**
		 *
		 * Constructor to initialize an empty heap's node.
		 *
		 */
		public HeapNode(int key, String info){
			this.key = key;
			this.info = info;
			this.child = null;
			this.next = null;
			this.prev = null;
			this.parent = null;
			this.rank = 0;
			this.mark = false;
		}


	}
}
