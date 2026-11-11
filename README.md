Closest Pair of Points

1. Project Overview
This project implements and compares three algorithms for solving the Closest Pair of Points problem using Java.
The goal is to find two points in a 2D plane with the minimum Euclidean distance between them.

2. Algorithms
Three approaches are implemented:

#Brute Force
Compares every possible pair of points.
Time Complexity: O(n²).

#Divide and Conquer (Version 1.0)
Sorts points by their x-coordinates.
Recursively divides the points into two halves.
Checks points near the dividing line.
Sorts the strip by y-coordinates at each recursive level.
Time Complexity: O(n log² n).

#Optimized Divide and Conquer (Version 2.0)
Sorts points by x-coordinates and y-coordinates before recursion.
Divides the y-sorted points while preserving their order.
Reduces repeated sorting during recursive calls.
Time Complexity: O(n log n).


3. Implementation
The project is implemented in Java.
The main components include:

#ClosestPair: Main program and algorithm implementations.

#Point: Represents a point with x and y coordinates.

#ClosestPairResult: Stores the closest pair and its distance.


4. Testing
The program supports two input modes:

#FIXED: Uses manually defined points for testing.

#RANDOM: Generates random points using a fixed seed.

The default configuration uses 500 randomly generated points.
The program compares the results of all three algorithms to check whether they produce the same minimum distance.

5. Performance Comparison
The benchmark includes:

5 warm-up runs.
30 measurement runs for each algorithm.
Average running time in milliseconds.
Correctness comparison between the three implementations.

The results can be used to study the relationship between theoretical time complexity and actual execution performance.

6.How to Run

Requirements:
Java 8 or later.

Compile:
javac src/ClosestPair.java

Run:
java -cp src ClosestPair

7. Technologies

Java
Algorithms and Data Structures
Computational Geometry

