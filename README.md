# A-Star-Algorithm-solving-8-Puzzle

This code is made to solve a variant of the 8-puzzle where there are 19 tiles plus a blank, arranged in the shape of a cross.
This code uses the A* algorithm, or Iterative Deepening Search with cutoff.

An example-input would be:
------ 2  1 -----        
------ 6  3 -----         
 4  5 12  7  8  9    
10 11 13 17 14 15    
 ------0 16------        
 -----18 19------
      
The goal state which we are searching for is:
        0  1      
        2  3      
  4  5  6  7  8  9
 10 11 12 13 14 15
       16 17      
       18 19   
