/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 *
 * Mohammad Daghash | ID : 314811290 | username : daghash
 * Noor Nasser | ID : 318676418 | username : noornasser
 */
public class FibonacciHeap
{

    HeapNode min; // pointer to the minimum element in the heap.
    int size, trees, marked;
    public static int cuts = 0; // number of cuts.
    public static int links = 0; // number of links

    public FibonacciHeap() {
        min = null;
        size = 0;
        trees = 0;
        //self reminder: make sure we don't need to add any other information.
    }

    private FibonacciHeap(HeapNode node) {
        min = node;
        size = 1;
        trees = 1;
    }
   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
   // Time Complexity: O(1).
    public boolean isEmpty()
    {
        return min == null || size==0;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
   // Time Complexity: O(1).
    public HeapNode insert(int key) {
        HeapNode InsertedNode = new HeapNode(key);
        // if the heap is empty
        if (isEmpty()) {
            min = InsertedNode;
            InsertedNode.set_prev(min);
            InsertedNode.set_next(min);
        } else {
            //insert the node on the minimum node's left
            InsertedNode.set_next(min);
            InsertedNode.set_prev(min.get_prev());
            min.get_prev().set_next(InsertedNode);
            min.set_prev(InsertedNode);
            // update the min if needed
            if (InsertedNode.getKey() < min.getKey())
                min = InsertedNode;
        }
        size++; // increment the size
        trees ++; // increment the number of root list
        return InsertedNode;
    }

    /**
     * A HELPER FUNCTION.
     * Consolidate root-list so that no roots have the same degree. During the consolidation we traverse the root list.
     * Whenever we discover two trees that have the same degree we merge these trees. In order to efficiently
     * check whether two trees have the same degree, we use an array that contains
     * for every degree value d a pointer to a tree left of the current pointer whose root
     * has degree d (if such a tree exist).
     */
    // Time complexity: O(MAXrank+#trees)
    public void consolidate() {
        int length = (int) (Math.ceil(Math.log(size) / Math.log(2))) + 1;
        HeapNode[] succ = new HeapNode[length];
        HeapNode temp = min;
        int i = 0;
        int rank = 0;
        HeapNode t = null;
        while (i < trees) {
            rank = temp.get_degree();
            if (t == null && succ[rank] == null) {
                succ[rank] = temp;
                i++;
                temp = temp.get_next();
                succ[rank].set_next(succ[rank]);
                succ[rank].set_prev(succ[rank]);
            } else {
                links++;
                if (t == null) {
                    t = temp;
                    temp = temp.get_next();
                } else {
                    rank = t.get_degree();
                }
                t.set_next(t);
                t.set_prev(t);
                int keyt = t.getKey();
                int keysucc = succ[rank].getKey();
                if (keyt > keysucc) {
                    HeapNode currChild = succ[rank].get_child();
                    succ[rank].set_child(t);
                    t.set_parent(succ[rank]);
                    if (currChild != null) {
                        t.set_next(currChild);
                        t.set_prev(currChild.get_prev());
                        currChild.get_prev().set_next(t);
                        currChild.set_prev(t);
                    }
                    t = succ[rank];
                } else {
                    HeapNode currChild = t.get_child();
                    t.set_child(succ[rank]);
                    succ[rank].set_parent(t);
                    if (currChild != null) {
                        succ[rank].set_next(currChild);
                        succ[rank].set_prev(currChild.get_prev());
                        currChild.get_prev().set_next(succ[rank]);
                        currChild.set_prev(succ[rank]);
                    }
                }
                succ[rank] = null;
                if (succ[rank + 1] == null) {
                    succ[rank + 1] = t;
                    t = null;
                    i++;
                }
            }
        }
        HeapNode node1 = null;
        HeapNode first = null;
        trees = 0;
        for (int n = 0; n < succ.length; n++) {
            if (succ[n] != null) {
                if (node1 == null) {
                    node1 = succ[n];
                    first = node1;
                } else {
                    HeapNode node2 = succ[n];
                    node1.set_next(node2);
                    node2.set_prev(node1);
                    node1 = node2;
                }
                trees++;
            }
        }
        node1.set_next(first);
        first.set_prev(node1);
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
   // Time Complexity : O(log(n)).
    public void deleteMin()
    {
        trees= trees + min.get_degree() -1 ;
        size--;
        if(min.get_child()!=null && min.get_next().getKey()!=min.getKey())
        {
            HeapNode next = min.get_next();
            HeapNode prev = min.get_prev();
            HeapNode child = min.get_child();
            HeapNode childNext = child.get_next();
            next.set_prev(child);
            child.set_next(next);
            prev.set_next(childNext);
            childNext.set_prev(prev);
            HeapNode tmp = child;
            for(int i=0;i<=trees;i++) {
                tmp.set_parent(null);
                if(tmp.get_mark()==1) {
                    marked--;
                    tmp.set_mark(0);
                }
                tmp=tmp.get_next();
            }
        }
        else if(min.get_child()!=null) {
            int rank=min.get_degree();
            HeapNode min1=new HeapNode(Integer.MAX_VALUE);
            HeapNode tmp=min.get_child();
            for(int i=0;i<=rank;i++) {
                tmp.set_parent(null);
                if(tmp.getKey()<min1.getKey()) {
                    min1 = tmp;
                }
                if(tmp.get_mark()==1) {
                    marked--;
                    tmp.set_mark(0);
                }
                tmp=tmp.get_next();
            }
            min = min1;
            consolidate();
            return;
        }
        else if(min.get_next().getKey()!=min.getKey()){
            HeapNode prev= min.get_prev();
            HeapNode nxt = min.get_next();
            nxt.set_prev(prev);
            prev.set_next(nxt);
        }
        else {
            min=null;
            size=0;
            trees=0;
            marked=0;
            return;
        }
        HeapNode min1= new HeapNode(Integer.MAX_VALUE);
        HeapNode temp=min;
        for(int i=0;i<=trees;i++) {
            if(temp.getKey()<min1.getKey() && temp.getKey()!=min.getKey()) {
                min1 = temp;
            }
            temp = temp.get_next();
        }
        min = min1;
        consolidate();
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
   // Time Complexity: O(1).
    public HeapNode findMin()
    {
        return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
   // Time Complexity: O(1).
    public void meld(FibonacciHeap heap2)
    {
        if(this.isEmpty()) {
            this.min = heap2.min;
            this.size=heap2.size;
            this.trees= heap2.trees;
            this.marked=heap2.marked;
            return;
        }
        if(heap2.isEmpty()) {
            return;
        }
        HeapNode prev = this.min.prev;
        HeapNode next = heap2.min.next;
        heap2.min.set_next(this.min);
        this.min.set_prev(heap2.min);
        prev.set_next(next);
        next.set_prev(prev);
        if(this.min.getKey()>heap2.min.getKey()) {
            this.min=heap2.min;
        }
        this.size= this.size + heap2.size;
        this.trees = this.trees + heap2.trees;
        this.marked=this.marked+heap2.marked;
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
   // Time Complexity: O(1).
    public int size()
    {
    	return size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * 
    */
    // Time complexity: O(#trees=#RootList)
    public int[] countersRep()
    {
        int[] arr = new int[(int) Math.ceil((Math.log(size))/Math.log(2))+1];
        int maxRank =0;
        HeapNode temp = min;
        for(int i=0;i<trees;i++) {
            int rank = temp.get_degree();
            arr[rank]++;
            temp=temp.get_next();
            if(rank>maxRank) {
                maxRank = rank;
            }
        }
        int[] finalArr=new int[maxRank+1];
        for(int i=0;i<=maxRank;i++) {
            finalArr[i]=arr[i];
        }
        return finalArr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
   // Time Complexity: O(log(n)).
    public void delete(HeapNode x) 
    {
        decreaseKey(x, Integer.MAX_VALUE); // update the min to the node we want to delete
        this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
   // Time Complexity: O(1).
    public void decreaseKey(HeapNode x, int delta) //self reminder: check if the key is allowed to be negative.
    {
        if(x==null) {
            return;
        }
        x.set_key(x.getKey()-delta);
        if(x.get_parent()==null) {
            if(x.getKey()<min.getKey()) {
                min=x;
            }
            return;
        }
        if(x.getKey() < x.get_parent().getKey()) {
            cascading_cut(x);
        }
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
   // Time Complexity: O(1).
    public int potential() 
    {
    	return (trees + 2*marked);
    }
   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
   // Time Complexity: O(1).
    public static int totalLinks()
    {
        return links;
    }

    /**
     * A HELPER FUNCTION.
     * A recursive function which disconnects a subtree from its parent and updates the number of marked .
     * we do that because we want the number of nodes in a tree of order k to be exponential in k.
     * That way, we can have only logarithmically many trees in a consolidated heap.
     */
    // Time Complexity: O(log(n)).
    public void cascading_cut(HeapNode node) {
        cuts++;
        HeapNode parent = node.get_parent();
        node.set_parent(null);
        int rank = parent.get_degree();
        if (parent.get_child().getKey() == node.getKey()) {
            parent.set_child(node.get_next());
        }
        parent.set_degree(rank - 1);
        if (node.get_next().getKey() == node.getKey()) {
            parent.set_child(null);
            parent.set_degree(0);
        }
        node.get_next().set_prev(node.get_prev());
        node.get_prev().set_next(node.get_next());
        node.set_next(node);
        node.set_prev(node);
        if (node.get_mark() == 1) {
            marked--;
            node.set_mark(0);
        }
        FibonacciHeap heap = new FibonacciHeap(node);
        this.meld(heap);
        this.size--;
        if (parent.get_mark() == 1 && parent.get_parent() != null) {
            cascading_cut(parent);
        } else if (parent.get_parent() != null) {
            parent.set_mark(1);
            marked++;
        }
    }

    /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    // Time Complexity: O(1).
    public static int totalCuts()
    {    
    	return cuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
     // Time Complexity: O(k*deg(H)).
    public static int[] kMin(FibonacciHeap H, int k) {
        int[] result = new int[k];
        FibonacciHeap heap = new FibonacciHeap();
        HeapNode node = H.findMin();
        for (int i = 0; i < k; i++) {
            HeapNode curr = node;
            if (node != null)
                do {
                    HeapNode temp = heap.insert(curr.getKey());
                    temp.setHelper(curr);
                    curr = curr.get_next();
                } while (curr != node);
            node = heap.findMin().getHelper();
            result[i] = node.getKey();
            heap.deleteMin();
            node = node.get_child();
        }
        return result;

    }

   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{
        public int key;
        int degree, mark;
        HeapNode parent, prev, next, child , helper;

        public HeapNode(int key) {
            this.key = key;
            this.degree = 0; // number of children.
            this.mark = 0; // how many times has this node been marked.
            this.parent = null; // parent in the tree, if any.
            this.prev = this; // previous element in the list.
            this.next = this; // next element in the list.
            this.child = null; // child node, if any.
            this.helper = null;
        }
        public void set_key(int key) {
           this.key = key;
       }
        public int getKey() {
           return this.key;
       }
        public void set_parent(HeapNode x) {
           this.parent = x;
       }
        HeapNode get_parent() {
           return this.parent;
       }
        public void set_prev(HeapNode x) {
           this.prev = x;
       }
        HeapNode get_prev() {
           return this.prev;
       }
        public void set_next(HeapNode x) {
           this.next = x;
       }
        HeapNode get_next() {
           return this.next;
       }
        public void set_child(HeapNode x) {
            this.child = x;
            if(child==null) {
                degree = 0;
                }
                else {
                    degree = child.get_degree()+1;
                }
        }
        HeapNode get_child() { return this.child;}
        void set_degree(int x) {this.degree = x;}
        int get_degree() { return this.degree;}
        public void set_mark(int mark) {this.mark = mark;}
        public int get_mark() {
           return this.mark;
       }
        public HeapNode getHelper() {
           return helper;
        }
        public void setHelper(HeapNode helper) {
           this.helper = helper;
        }
    }
}
