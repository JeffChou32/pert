# pert
This project implements the PERT (Program Evaluation and Review Technique) algorithm using a directed acyclic graph (DAG) to model tasks and their dependencies.

The algorithm calculates:

Earliest Start (ES) and Earliest Finish (EF) times
Latest Start (LS) and Latest Finish (LF) times
Slack for each task
The Critical Path, which determines the minimum project completion time.

Features
Topological Sorting: Ensures tasks are processed in dependency order using a depth-first search (DFS).
Forward and Backward Passes:
Calculates ES and EF in the forward pass.
Determines LS, LF, and slack in the backward pass.
Critical Path Identification:
Tasks with zero slack are identified as part of the critical path.
Graph Representation:
Built using a custom graph API (Graph.java), supporting directed edges and adjacency lists.
Validation:
Detects cycles to ensure the graph is a DAG, as required for PERT.
Usage
Input: Provide a directed graph with:
A list of tasks and their dependencies.
Duration of each task.
Output:
Earliest and latest completion times for all tasks.
Slack for each task.
Critical path length and critical tasks.
