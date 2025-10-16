# 🚀 Fibonacci Heap – Advanced Java Implementation

### 💡 Overview
This repository contains a **complete, from-scratch implementation of a Fibonacci Heap** — one of the most sophisticated priority queue data structures in algorithmic computer science.  
It supports all standard heap operations with **amortized O(1)** insertion, decrease-key, and meld, and **O(log n)** extract-min and delete.

This project was developed as part of an advanced **Data Structures** course and was engineered for **clarity, correctness, and performance** — not just to “work,” but to **demonstrate mastery** of amortized analysis, pointer-based structures, and algorithmic design.

---

## 🧱 Design Philosophy
Fibonacci heaps are notoriously subtle. This implementation focuses on **clean architecture**, **educational readability**, and **instrumentation for analysis**:

- **Readable structure**: clear separation between heap logic and node internals (`HeapNode` class).
- **Low-level control**: explicit doubly linked circular lists for root and child lists — no Java `LinkedList` shortcuts.
- **Instrumented metrics**: counters for total cuts and links to visualize amortized performance.
- **Defensive handling**: all corner cases explicitly covered — singleton heaps, empty deletes, self-melds, and cascading cuts.
- **Analytic hooks**: methods expose inner operations for profiling and theoretical validation.

---

## ⚙️ Core Operations

| Operation | Complexity | Description |
|------------|-------------|-------------|
| **insert(key, info)** | O(1) amortized | Adds a node to the root list and updates `min` pointer. |
| **findMin()** | O(1) | Returns the minimum node. |
| **deleteMin()** | O(log n) amortized | Removes the min node, performs **consolidation** by degree, and rebuilds heap structure. |
| **decreaseKey(node, diff)** | O(1) amortized | Decreases a key, performs cuts and cascading cuts if necessary. |
| **delete(node)** | O(log n) amortized | Internally calls `decreaseKey(node, ∞)` followed by `deleteMin()`. |
| **meld(otherHeap)** | O(1) | Efficiently merges two heaps by concatenating root lists. |

---

## 🧠 Algorithmic Highlights

### Consolidation (after `deleteMin`)
- All trees in the root list are grouped by degree.
- Trees of equal degree are **linked** (smaller key becomes the new parent).
- The number of trees reduces to O(log n), ensuring the logarithmic amortized bound.

### Cascading Cuts
- When a node’s key decreases below its parent’s, it’s **cut** to the root list.
- If the parent was already marked, it too is cut — recursively (cascading cut).
- Guarantees potential function drop → amortized O(1).

### Meld
- Concatenates root lists in **constant time**.
- Chooses new `min` based on both heaps.

---

## 🧩 Data Model

```java
class FibonacciHeap {
    HeapNode min;          // current minimum
    int size;              // number of nodes
    static int totalLinks; // global counter
    static int totalCuts;  // global counter
    ...
}

class HeapNode {
    int key;
    String info;
    HeapNode child, parent, next, prev;
    int rank;
    boolean mark;
}
```

Each `HeapNode` forms part of a circular doubly linked list.  
Children form their own lists; `mark` tracks whether a node has already lost one child — essential for cascading cuts.

---

## 🔬 Instrumentation for Analysis
To make the heap not just *functional* but *insightful*:

```java
public static int totalLinks(); // total link operations since start
public static int totalCuts();  // total cut operations since start
public int potential();         // Φ = trees + 2 × marked nodes
```

These let you empirically verify amortized behavior — perfect for coursework or interviews when asked to “prove it works in O(1) amortized”.

---

## 🧪 Testing Ideas
Interviewers love hearing about how you tested invariants:
- **Consolidation tests**: insert keys with same degree, deleteMin, verify all root degrees unique.
- **Cascading cuts tests**: force multiple decreaseKeys, check totalCuts counter increments correctly.
- **Edge cases**: empty heap deletes, meld with empty heap, repeated findMin calls.
- **Stress tests**: thousands of random operations — confirm `min` and `size` remain consistent.

---

## 🧰 Example

```java
FibonacciHeap heap = new FibonacciHeap();

HeapNode a = heap.insert(17, "A");
HeapNode b = heap.insert(3, "B");
HeapNode c = heap.insert(24, "C");

System.out.println(heap.findMin().getKey()); // 3

heap.decreaseKey(a, 16); // Now 1
heap.deleteMin();

System.out.println(heap.findMin().getKey()); // 3
```

---

## 🚀 Why It Impresses Interviewers
- Shows **deep algorithmic understanding** beyond surface-level data structures.
- Demonstrates **OOP design** with separation of concerns.
- Highlights **amortized analysis**, potential method, and efficiency trade-offs.
- The code’s structure shows **engineering maturity**: clear naming, no redundant operations, and careful state invariants.
- Perfect example for **“Explain a complex data structure you implemented from scratch.”**

---

## 🧑‍💻 Authors
**Gad Rozen** and **Hila Etzioni**  
Developed as part of the *Advanced Data Structures* course project.

---

## 📜 License
For academic and interview demonstration use.  
Please credit the authors if reused.
