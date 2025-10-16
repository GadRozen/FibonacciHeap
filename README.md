# Fibonacci Heap (Java) — Interview‑Ready README

A production‑quality implementation of a **Fibonacci Heap** over positive integers, written in Java as part of a university *Data Structures* course project.  
This README highlights the **engineering decisions**, **algorithmic guarantees**, and **testing strategy** interviewers usually ask about.

---

## ✨ What stands out to interviewers

- **Full feature set**: `insert`, `findMin`, `deleteMin` (with consolidation), `decreaseKey` (with cascading cuts), `delete`, and `meld`.
- **Amortized guarantees**: `insert`, `findMin`, `meld`, and `decreaseKey` are **O(1) amortized**; `deleteMin` (consolidation) and `delete` are **O(log n) amortized**.
- **Clean node model** with explicit `parent/child/next/prev`, `rank`, and `mark` fields — enabling canonical Fibonacci‑heap operations and proofs.
- **Instrumentation hooks**: `totalLinks()`, `totalCuts()` expose the cost of consolidation and cascading cuts for profiling/experiments.
- **Robust correctness**: careful handling of singleton nodes, root list concatenation in `meld`, and min‑pointer updates across all operations.

---

## 📦 Public API (selected)

```java
// Core operations
HeapNode insert(int key, String info);
HeapNode findMin();                 // null if empty
void     deleteMin();               // O(log n) amortized
void     decreaseKey(HeapNode x, int diff);        // O(1) amortized
void     delete(HeapNode x);                         // O(log n) amortized
void     meld(FibonacciHeap other);                 // O(1)

// Analytics
int totalLinks();   // number of tree links performed (during consolidation)
int totalCuts();    // number of (cascading) cuts performed

// Size & structure
int size();         // number of items in the heap
int numTrees();     // number of trees in the root list
```
> Internally there are also helpers such as `successive_linking()` (consolidation), `linkTrees()`, `cutRoot()`, `cut()`, and `cascadingCuts()`.

---

## 🧱 Data model & invariants

- **Root list**: circular doubly‑linked list of tree roots. Melding two heaps concatenates their root lists in **O(1)** and recomputes `min`.
- **Node fields** (`HeapNode`): `key`, `info`, `parent`, `child`, `next`, `prev`, `rank` (degree), `mark` (for cascading cuts).
- **Min pointer**: maintained eagerly on `insert`, `meld`, and after `decreaseKey`; safely recomputed during consolidation after `deleteMin`.
- **Consolidation** (`successive_linking`): after `deleteMin`, roots are bucketed by degree and repeatedly **linked** so at most one tree per degree remains.  
  The temporary array size is bounded by `⌊log₂ n⌋ + 1` (safe overestimate of `log_φ n`) which guarantees **O(log n)** links.
- **Decrease‑key**: if a node’s key drops below its parent, we `cut` it to the root list and perform **cascading cuts** following marked ancestors.

---

## ⏱️ Complexity (amortized)

| Operation      | Time             | Notes |
| -------------- | ---------------- | ----- |
| `insert`       | **O(1)**         | Append to root list and update `min` |
| `findMin`      | **O(1)**         | Return `min` |
| `meld`         | **O(1)**         | Splice circular root lists |
| `decreaseKey`  | **O(1)**         | Cuts and marking rules |
| `deleteMin`    | **O(log n)**     | Consolidation by degrees |
| `delete(x)`    | **O(log n)**     | Implemented via cut + potential `deleteMin` (after `decreaseKey`) or direct root deletion helper |

Space is **O(n)**.

---

## ✅ Usage example

```java
FibonacciHeap heap = new FibonacciHeap();

// Inserts return a handle to the node (useful for later decreaseKey/delete)
FibonacciHeap.HeapNode a = heap.insert(17, "A");
FibonacciHeap.HeapNode b = heap.insert(3,  "B");
FibonacciHeap.HeapNode c = heap.insert(24, "C");

System.out.println(heap.findMin().key);   // 3

heap.decreaseKey(a, 16);                  // A: 17 -> 1 (may trigger cascading cuts)
System.out.println(heap.findMin().key);   // 1

heap.deleteMin();                         // removes key 1, consolidates
System.out.println(heap.findMin().key);   // 3

// Meld in O(1)
FibonacciHeap other = new FibonacciHeap();
other.insert(2, "X");
heap.meld(other);
System.out.println(heap.findMin().key);   // 2
```

---

## 🧪 Testing strategy (high‑value cases)

1. **Consolidation**: insert keys with degrees that force many links; call `deleteMin`; assert `numTrees()` shrinks and degrees are unique.  
2. **Cascading cuts**: craft a deep path and call `decreaseKey` repeatedly; validate `totalCuts()` increments and all cut nodes land in the root list.
3. **Meld**: meld two non‑empty heaps; verify root list size and `min` are correct; ensure both heaps remain valid after operation.  
4. **Edge cases**: single‑node heap, repeated `deleteMin` until empty, `decreaseKey` that *does not* trigger a cut, deleting a root vs. a non‑root.
5. **Instrumentation checks**: for randomized sequences, track `totalLinks/totalCuts` trends to see typical O(1)/O(log n) amortized behavior.

---

## 🔧 Implementation notes

- **Selective min updates**: internal helper `decreaseKey_selective_updating_min(...)` avoids redundant `min` recompute when the caller already knows whether `min` can change — useful during cascading cuts.
- **Safe helpers for deletion**: `delete_root` and `cutRoot` isolate the cases where the victim is already a root vs. internal node.
- **Bounds for consolidation array**: uses `⌊log₂ n⌋ + 1` which is simple, safe, and avoids floating‑point errors with `log_φ`.
- **Readable counters**: capitalized `TotalLinks`/`Totalcuts` are exposed via getters; ideal for performance plots or teaching demos.

---

## 📚 When to use a Fibonacci Heap

- Dijkstra’s / Prim’s algorithms with **many** `decreaseKey` operations.
- Amortized‑optimal priority queue when merges (`meld`) are frequent.
- Not always the fastest in practice for small inputs — but demonstrates mastery of **amortized analysis**, pointer‑heavy structures, and **careful invariants**.

---

## 👥 Credits

Implementation by **Gad Rozen** and **Hila Etziony** as part of a *Data Structures* course project.

---

## 📄 License

For educational and interview use. If you adapt this code, please attribute the original authors.
