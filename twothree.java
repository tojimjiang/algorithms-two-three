import java.io.*;
import java.util.*;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.StreamTokenizer;

public class Solution {
    static class twothree {
        // Provided
        static void insert(String key, int value, TwoThreeTree tree) {
            // insert a key value pair into tree (overwrite existsing value
            // if key is already present)

            int h = tree.height;

            if (h == -1) {
                LeafNode newLeaf = new LeafNode();
                newLeaf.guide = key;
                newLeaf.value = value;
                tree.root = newLeaf;
                tree.height = 0;
            }
            else {
                WorkSpace ws = doInsert(key, value, tree.root, h);

                if (ws != null && ws.newNode != null) {
                    // create a new root

                    InternalNode newRoot = new InternalNode();
                    if (ws.offset == 0) {
                        newRoot.child0 = ws.newNode;
                        newRoot.child1 = tree.root;
                    }
                    else {
                        newRoot.child0 = tree.root;
                        newRoot.child1 = ws.newNode;
                    }
                    resetGuide(newRoot);
                    tree.root = newRoot;
                    tree.height = h+1;
                }
            }
        }

        static WorkSpace doInsert(String key, int value, Node p, int h) {
            // auxiliary recursive routine for insert

            if (h == 0) {
                // we're at the leaf level, so compare and
                // either update value or insert new leaf

                LeafNode leaf = (LeafNode) p; //downcast
                int cmp = key.compareTo(leaf.guide);

                if (cmp == 0) {
                    leaf.value = value;
                    return null;
                }

                // create new leaf node and insert into tree
                LeafNode newLeaf = new LeafNode();
                newLeaf.guide = key;
                newLeaf.value = value;

                int offset = (cmp < 0) ? 0 : 1;
                // offset == 0 => newLeaf inserted as left sibling
                // offset == 1 => newLeaf inserted as right sibling

                WorkSpace ws = new WorkSpace();
                ws.newNode = newLeaf;
                ws.offset = offset;
                ws.scratch = new Node[4];

                return ws;
            }
            else {
                InternalNode q = (InternalNode) p; // downcast
                int pos;
                WorkSpace ws;
                // Modified from Given
                q.child0.value += q.value; // Add value to child 0
                q.child1.value += q.value; //  ... to child 1
                if (q.child2 != null){
                    q.child2.value += q.value; // ... to child 2
                }
                q.value = 0; // Reset original node to 0
                // End of Modification

                if (key.compareTo(q.child0.guide) <= 0) {
                    pos = 0;
                    ws = doInsert(key, value, q.child0, h-1);
                }
                else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
                    pos = 1;
                    ws = doInsert(key, value, q.child1, h-1);
                }
                else {
                    pos = 2;
                    ws = doInsert(key, value, q.child2, h-1);
                }

                if (ws != null) {
                    if (ws.newNode != null) {
                        // make ws.newNode child # pos + ws.offset of q

                        int sz = copyOutChildren(q, ws.scratch);
                        insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
                        if (sz == 2) {
                            ws.newNode = null;
                            ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
                        }
                        else {
                            ws.newNode = new InternalNode();
                            ws.offset = 1;
                            resetChildren(q, ws.scratch, 0, 2);
                            resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
                        }
                    }
                    else if (ws.guideChanged) {
                        ws.guideChanged = resetGuide(q);
                    }
                }

                return ws;
            }
        }


        static int copyOutChildren(InternalNode q, Node[] x) {
            // copy children of q into x, and return # of children

            int sz = 2;
            x[0] = q.child0; x[1] = q.child1;
            if (q.child2 != null) {
                x[2] = q.child2;
                sz = 3;
            }
            return sz;
        }

        static void insertNode(Node[] x, Node p, int sz, int pos) {
            // insert p in x[0..sz) at position pos,
            // moving existing extries to the right

            for (int i = sz; i > pos; i--)
                x[i] = x[i-1];

            x[pos] = p;
        }

        static boolean resetGuide(InternalNode q) {
            // reset q.guide, and return true if it changes.

            String oldGuide = q.guide;
            if (q.child2 != null)
                q.guide = q.child2.guide;
            else
                q.guide = q.child1.guide;

            return q.guide != oldGuide;
        }


        static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
            // reset q's children to x[pos..pos+sz), where sz is 2 or 3.
            // also resets guide, and returns the result of that

            q.child0 = x[pos];
            q.child1 = x[pos+1];

            if (sz == 3)
                q.child2 = x[pos+2];
            else
                q.child2 = null;

            return resetGuide(q);
        }
        
        // New Code
        static void search(Node n, String value, int height, int total){
            if (height == 0){
                if (!n.guide.equals(value)){ // If key DNE
                    System.out.println(-1);
                }
                else{
                    System.out.println(total+n.value);
                }
            }
            else{
                InternalNode i = (InternalNode) n; // Utilize Polymorphism to Cast
                // Recursively search for leaf, add and store fees (total).
                if (i.child0.guide.compareTo(value) >= 0){ // Is key less than child0?
                    search(i.child0, value,height-1,total+=i.value); // Deeper search in child0 subtree
                }
                else if (i.child1.guide.compareTo(value) >= 0 || i.child2 == null){ // Is key less than child1?
                    search(i.child1, value,height-1,total+=i.value); // Deeper search in child1 subtree
                }
                else{ // Key is bigger than child1, AND child2 exists.
                    search(i.child2, value,height-1,total+=i.value); // Deeper search in child2 subtree
                }
            }
        }
        void valUpdate(String low,Node n,String left,String right,int height,int value){
            if (height==0){ // Is leaf
                if (n.guide.compareTo(left) >= 0 && n.guide.compareTo(right) <= 0){
                    n.value+=value; // Increase value of leaf
                }
                return;
            }
            else if (low.compareTo(left) >= 0 && n.guide.compareTo(right) <= 0){ // All internal nodes in range
                n.value+=value; // Increase value of internal node
            }
            else if (low.compareTo(right) < 0 && n.guide.compareTo(left) >= 0) { // Some nodes are in range
                InternalNode i = (InternalNode) n; // Utilize Polymorphism to Cast
                valUpdate(low,i.child0,left,right,height-1,value); // valUpdate in child0 subtree
                valUpdate(i.child0.guide,i.child1,left,right,height-1,value); // valUpdate in child1 subtree
                if (i.child2!=null){
                    valUpdate(i.child1.guide,i.child2,left,right,height-1,value); // If child exists, valUpdate in child2 subtree
                }
            }
        }
    }

    // Provided
    static class Node {
        String guide;
        int value;
        // guide points to max key in subtree rooted at node
    }

    static class InternalNode extends Node {
        Node child0, child1, child2;
        // child0 and child1 are always non-null
        // child2 is null iff node has only 2 children
    }

    static class LeafNode extends Node {
        // guide points to the key
    }

    static class TwoThreeTree {
        Node root;
        int height;

        TwoThreeTree() {
            root = null;
            height = -1;
        }
    }

    static class WorkSpace {
    // this class is used to hold return values for the recursive doInsert
    // routine
        
        Node newNode;
        int offset;
        boolean guideChanged;
        Node[] scratch;
    }

    // New Code
    public static void main(String[] args) {
        // Initializers
        int value;
        String name;
        String start = "";
        String end = "";
        TwoThreeTree Tree = new TwoThreeTree();
        twothree T = new twothree();
        int fee = 0;
        String line;
        String queryType;
        try{ // Primary I/O Handling, and Function Calling
            BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
            int linesNum = Integer.parseInt(reader.readLine()); // Number of queries
            while ((line = reader.readLine()) != null){
                StringTokenizer tk = new StringTokenizer(line);
                queryType = tk.nextToken();
                if (queryType.equals("1")){ // Querytype 1
                    name = tk.nextToken();
                    value = Integer.parseInt(tk.nextToken());
                    T.insert(name,value,Tree); // Insert name/value to tree as a node
                }
                else if (queryType.equals("2")) { // Querytype 2
                    start = tk.nextToken();
                    end = tk.nextToken();
                    fee = Integer.parseInt(tk.nextToken());
                    if (start.compareTo(end)>0){ // Swap if end < start
                        String temp;
                        temp = start;
                        start = end;
                        end = temp;
                    }
                    T.valUpdate("",Tree.root,start,end,Tree.height,fee); // Use updateVal to update fees, recursively and subtree-wise if necessary.
                }
                else if (queryType.equals("3")) { // Querytype 3
                    name = tk.nextToken();
                    T.search(Tree.root,name,Tree.height,0); // Use search to recursively find and total the fees.
                }
            }
            reader.close(); // Close reader
        }
        catch (IOException e){ // I/O Failure
            System.out.println("Encountered I/O Error - Exiting Now");
        }
        catch (NumberFormatException f) { // NumFormat Failure
            System.out.println("Encountered Input Error - Exiting Now");
        }
    }
}