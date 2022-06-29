import java.util.*;

public class Eight {

    // declaring field for 19 puzzle
    final static int SIZE = 35;
    final static int LENGTH = 6; // Sqrt(SIZE+1)
    int tiles[];
    int blankPos;
    //hashSet holding positions of invalid fields;  fields which are not allowed to use-> to achieve cross form.
    final static HashSet<Integer> invalidPositions = new HashSet<Integer>(Arrays.asList(0,1,4,5,6,7,10,11,24,25,28,29,30,31,34,35));

    // constructor to take input and convert it into cross shape
    // invalid tiles are filled with -1
    public Eight(int[] x) {
        tiles = Arrays.copyOf(x, x.length);
        for (int i = 0; i <= SIZE; i++){
            if (tiles[i] == 0 && !invalidPositions.contains(i)) {
                blankPos = i;
                return;
            } else {
                blankPos = -1;
            }
        }
    }

    public Eight(int tiles[], int blankPos) {
        this.tiles = Arrays.copyOf(tiles, tiles.length);
        this.blankPos = blankPos;
    }


    public String toString() {
        String s = "";
        for(int i=0; i<LENGTH; i++) {
            for(int j=0; j<LENGTH; j++) {
                String input ="-1";
               if(tiles[i * LENGTH + j] != -1){ // to ensure that the -1 Flags will not be printed
                   s += String.format(" %2d", tiles[i * LENGTH + j]);
               }
               else{
                   s += String.format(" %2s", " ");
               }

            }
            s += "\n";
        }
        return s;
    }

    public boolean equals(Object o) {
        Eight r = (Eight)o;
        return blankPos == r.blankPos && Arrays.equals(tiles, r.tiles);
    }

    public int hashCode() { return Arrays.hashCode(tiles); }

    interface MoveAction { boolean valid(); void move(); }

    // added check to skip move if move would be "outside" the field
    // if it tries to move outside the field: valid returns false
    private MoveAction[] moveActions = new MoveAction[] {

            new MoveAction() { // move up
                public boolean valid() {  return blankPos > LENGTH-1 && tiles[blankPos-LENGTH] != -1;}
                public void move() { tiles[blankPos] = tiles[blankPos-LENGTH]; blankPos -= LENGTH; tiles[blankPos] = 0;}
            },
            new MoveAction() { // move down
                public boolean valid() { return blankPos < SIZE-LENGTH+1 && tiles[blankPos+LENGTH] != -1; }
                public void move() { tiles[blankPos] = tiles[blankPos+LENGTH]; blankPos += LENGTH; tiles[blankPos] = 0;}
            },
            new MoveAction() { // move left
                public boolean valid() { return blankPos % LENGTH != 0 && tiles[blankPos-1] != -1; }
                public void move() { tiles[blankPos] = tiles[blankPos-1]; blankPos -= 1; tiles[blankPos] = 0;}
            },
            new MoveAction() { // move right
                public boolean valid() { return blankPos % LENGTH != LENGTH-1 && tiles[blankPos+1]!= -1;}
                public void move() { tiles[blankPos] = tiles[blankPos+1]; blankPos += 1; tiles[blankPos] = 0;}
            }
    };

    // opposite
    private static int[] opp = {1, 0, 3, 2};

    static class Node implements Comparable<Node>, Denumerable {
        public Eight state;
        public Node parent;
        public int g, h;
        public boolean inFrontier;
        public int x;
        Node(Eight state, Node parent, int g, int h) {
            this.state = state;
            this.parent = parent;
            this.g = g;
            this.h = h;
            inFrontier = true;
            x = 0;
        }
        public int compareTo(Node a) {
            return g + h - a.g - a.h;
        }
        public int getNumber() { return x; }
        public void setNumber(int x) { this.x = x; }
        public String toString() { return state + ""; }
    }

    public static void main(String[] args) {

        int[] x = new int[SIZE+1];
        int[] goalArr = new int[SIZE+1];
        int count=0;

        for(int i=0; i<=SIZE; i++){
            if(invalidPositions.contains(i)){
                goalArr[i]=-1;
            }
            else{
                goalArr[i]= count++;
            }
        }

        Eight goal = new Eight(goalArr);
        // finished with computing Goal-State

        Scanner in = new Scanner(System.in);

        for(int j =0; j<=35; j++){ //
           if (invalidPositions.contains(j)) { // skipping placeholders -1
               x[j]=-1;
           }
           else{ // fill array with given tiles
               x[j] = in.nextInt();
           }
        }
        Eight startState = new Eight(x);

       astar(startState, goal);
       ids(startState, goal);
    }

    public static int ids(Eight r, Eight goal) {
        for(int limit=0;;limit++) {
            int result = bdfs(r, goal, limit);
            if(result != 1) {
                System.out.println("In depth search with cutoff found a solution with limit/moves = "+limit);
                return result;
            }
        }
    }

    public static int bdfs(Eight r, Eight goal, int limit) {
        // returns 0: failure, 1: cutoff, 2: success
        if(r.equals(goal))
            return 2;
        else if(limit == 0)
            return 1;
        else {
            boolean cutoff = false;
            for(int i=0; i<4; i++) {
                if(r.moveActions[i].valid()) {
                    r.moveActions[i].move();
                    switch(bdfs(r, goal, limit-1)) {
                        case 1: cutoff = true; break;
                        case 2: return 2;
                        default:
                    }
                    r.moveActions[opp[i]].move();
                }
            }
            return (cutoff ? 1 : 0);
        }
    }

    public static int h(Eight r, Eight goal) { // manhatten distance
        int[] rev = new int[SIZE+1];
        int total = 0;
        for(int i=0; i<=SIZE; i++) {
            if (goal.tiles[i] == -1) continue; // continue: stops one iteration in a loop and continues to the next
            rev[goal.tiles[i]] = i;
        }
        for(int i=0; i<=SIZE; i++)
            if(r.tiles[i] != 0 && r.tiles[i] != -1) {
                total += Math.abs(i % LENGTH - rev[r.tiles[i]] % LENGTH) + Math.abs(i / LENGTH - rev[r.tiles[i]] / LENGTH);
            }
        return total;
    }

    public static void printAnswer(Node x) {
        Stack<Node> stack = new Stack<>();
        int numMoves = 0;
        for(Node y = x; y != null; y = y.parent) {
            stack.push(y);
            numMoves++;
        }
        while(!stack.isEmpty()) {
            System.out.println(stack.pop());// printing the moves the program made!
        }
        System.out.println((numMoves-1) + " moves.");
    }

    public static int astar(Eight start, Eight goal) {
        // returns 0: failure, 2: success
        System.out.println();

        System.out.println("  f    |frontier|  |explored|");
        int maxF = 0;
        Node z = new Node(start, null, 0, h(start, goal));
        IndexMinPQ<Node> frontier = new IndexMinPQ<>();
        frontier.add(z);
        HashMap<Eight,Node> explored = new HashMap<>();
        explored.put(start, z);

        while(true) {
            if(frontier.isEmpty()) {return 0;}
            Node x = frontier.remove();
            x.inFrontier = false;
            if(x.g + x.h > maxF) { maxF = x.g + x.h; System.out.printf("%3d %10d %10d\n", maxF, frontier.size(), explored.size()); }
            if(x.state.equals(goal)) {
                printAnswer(x);
                return 2;
            }
            for(int i=0; i<4; i++) {
                if(x.state.moveActions[i].valid()) {
                    x.state.moveActions[i].move();
                    Node n = explored.get(x.state);
                    if(n == null) {
                        Eight s = new Eight(x.state.tiles, x.state.blankPos);
                        n = new Node(s, x, x.g+1, h(x.state,goal));
                        explored.put(s, n);
                        frontier.add(n);
                    }
                    else if(n.inFrontier) {
                        if(x.g+1 < n.g) {
                            n.parent = x;
                            n.g = x.g + 1;
                            frontier.update(n);
                        }
                    }
                    x.state.moveActions[opp[i]].move();
                }
            }
        }
    }
}