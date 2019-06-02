import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.io.*;
import java.util.*;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Solution {
    static class twothree {
        // Provided
        static void insert(String key, int value, TwoThreeTree tree) {
            // insert a key value pair into tree (overwrite existing value
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
        void goThrough(String start, String end, Node n){
            if (n instanceof InternalNode){ // See if it is a internal method
                InternalNode i = (InternalNode) n; // polymorphism
                if (i.child0.guide.compareTo(start) >=0){ // Compare child0 subtree to starting key
                    goThrough(start,end,i.child0); // Recursive if child is greater
                }
                if (i.child1.guide.compareTo(start) >=0){ // Compare child1 subtree to starting key
                    goThrough(start,end,i.child1); // Recursive if child is greater
                }
                if (i.child2 != null && i.child2.guide.compareTo(start) >=0){ // Check and Compare child3 subtree to starting key
                    goThrough(start,end,i.child2); // Recursive if child is greater
                }
            }
            else{ // Otherwise is leaf
                LeafNode l = (LeafNode) n;
                if (n.guide.compareTo(start) >=0 && n.guide.compareTo(end) <= 0){ // Compare keyvalue to range
                    System.out.println(n.guide + " " + l.value); // output
                }
            }
        }
    }

    // Provided
    static class Node {
        String guide;
        // guide points to max key in subtree rooted at node
    }

    static class InternalNode extends Node {
        Node child0, child1, child2;
        // child0 and child1 are always non-null
        // child2 is null iff node has only 2 children
    }

    static class LeafNode extends Node {
        // guide points to the key

        int value;
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
        int value;
        String key;
        String start = "";
        String end = "";
        TwoThreeTree Tree = new TwoThreeTree();
        twothree T = new twothree();
        Scanner reader = new Scanner(System.in); // Setup to read input
        int linesA = Integer.parseInt(reader.next()); // First input read
        int linesB = 0; //Initialize
        while (reader.hasNext()){
            if (linesA != 0){ // Start Adding
                key = reader.next();
                value = Integer.parseInt(reader.next());
                T.insert(key,value,Tree); // Read and insert
                linesA--; // Decrement
                //System.out.println(key + " " + value);
                if (linesA == 0){
                    linesB = Integer.parseInt(reader.next()); // Second input read
                }
            }
            else { // if in second database
                if (linesB != 0) { // Run X times
                    start = reader.next();
                    end = reader.next();
                    if (start.compareTo(end) > 0) { // Switch start and end if end < start
                        String temp;
                        temp = start;
                        start = end;
                        end = temp;
                    }
                    T.goThrough(start, end, Tree.root); // Go through tree and print out
                    linesB--; //Decrement
                }
                else {
                    reader.close(); //Close reader
                }
            }
        }
    }
    // End my code additions
}