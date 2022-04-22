# Parallel-DFA

A large random string is generated. The DFA is configured to greedily pattern match floats in the string.

The program takes as input a single argument, the number of speculative threads.</br>
The program pattern matches the string via a normal thread and a specified number of speculative threads.

A speculative thread is executed in parallel on its own portion of the string. It executes all possible DFA start states and converges the DFA states when possible.

If a portion of the string is not considered to be part of a float, then that portion must be replaced with spaces.</br>

./q2 t</br>

Where t is the number of speculative threads. t >= 0</br>
