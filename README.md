--- Abstract ---

This academic project provides a way to parallelize and distribute the comparison of DNA sequences to a given sequence.
This aims at reducing the time necessary to find which sequence is the closest to a given one among those stored in a database, which might be distributed over a network of computers.
The distance used to compare the sequences is the edit distance.

First, the computation of a single comparison is parallelized on a GPU, in order to take advantage of its numerous cores.
Second, the comparisons are distributed over a network of computers.


--- Languages ---

Java, Java-RMI, JCuda, Cuda
