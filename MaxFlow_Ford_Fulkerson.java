/*
@file MaxFlow_Ford_Fulkerson
@author thmain (Ford Fulkerson) : https://gist.github.com/thmain
@author Jeremy Mednik (Main File)


Description:
This program reads a txt file created by the user which follows the format:
First line: n number of Players
Second line: Amount of wins for the n Players
Next n lines: The nextnlines of input represent the 2-D array of remaining games
to be played. Thejthinteger of theith row will represent the number of remaining
games to be played betweeniandj. This input array will be symmetric, and will have
0s along the diagonal.

The program then determines if it is possible for team A to win given their current
win schedule. It also considers the matchups between teams not including A where
some team will win each game.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;

public class MaxFlow_Ford_Fulkerson {
    static class Graph {
        int vertices;
        int graph[][];

        public Graph(int vertex, int[][] graph) {
            this.vertices = vertex;
            this.graph = graph;
        }

        public int findMaxFlow(int source, int sink) {
            //residual graph
            int[][] residualGraph = new int[vertices][vertices];

            //initialize residual graph same as original graph
            for (int i = 0; i <vertices ; i++) {
                for (int j = 0; j <vertices ; j++) {
                    residualGraph[i][j] = graph[i][j];
                }
            }

            //initialize parent [] to store the path Source to destination
            int [] parent = new int[vertices];

            int max_flow = 0; //initialize the max flow

            while(isPathExist_BFS(residualGraph, source, sink, parent)){
                //if here means still path exist from source to destination

                //parent [] will have the path from source to destination
                //find the capacity which can be passed though the path (in parent[])

                int flow_capacity = Integer.MAX_VALUE;

                int t = sink;
                while(t!=source){
                    int s = parent[t];
                    flow_capacity = Math.min(flow_capacity, residualGraph[s][t]);
                    t = s;
                }

                //update the residual graph
                //reduce the capacity on fwd edge by flow_capacity
                //add the capacity on back edge by flow_capacity
                t = sink;
                while(t!=source){
                    int s = parent[t];
                    residualGraph[s][t]-=flow_capacity;
                    residualGraph[t][s]+=flow_capacity;
                    t = s;
                }

                //add flow_capacity to max value
                max_flow+=flow_capacity;
            }
            return max_flow;
        }
        public static int factorial(int n) {
          if (n == 0) {
            return 1;
          }
          return n*factorial(n-1);
        }


        public boolean isPathExist_BFS(int [][] residualGraph, int src, int dest, int [] parent){
            boolean pathFound = false;

            //create visited array [] to
            //keep track of visited vertices
            boolean [] visited = new boolean[vertices];

            //Create a queue for BFS
            Queue<Integer> queue = new LinkedList<>();

            //insert the source vertex, mark it visited
            queue.add(src);
            parent[src] = -1;
            visited[src] = true;

            while(queue.isEmpty()==false){
                int u = queue.poll();

                //visit all the adjacent vertices
                for (int v = 0; v <vertices ; v++) {
                    //if vertex is not already visited and u-v edge weight >0
                    if(visited[v]==false && residualGraph[u][v]>0) {
                        queue.add(v);
                        parent[v] = u;
                        visited[v] = true;
                    }
                }
            }
            //check if dest is reached during BFS
            pathFound = visited[dest];
            return pathFound;
        }
    }


    public static void main(String[] args) {
      File file = new File(args[0]);
      try {
        //Scanner Class
        Scanner sc = new Scanner(file);
        //Number of players i
        int i = sc.nextInt();
        //Wins of Each Player
        int[] wins = new int[i];
        //Schedule Between teams
        int[][] schedule = new int[i][i];
        //Remaining Games for each team
        int[] remainingGames = new int[i];
        //Best possible final record considering the current placement of A
        int targetScoreA;



        //General Information about txt file import
        System.out.println();
        System.out.println("Number of Players -> " + i);

        System.out.println("--------------------------");

        System.out.println("Wins of Following Players");
        for (int j = 0; j < i; j++) {
          //Imports the wins of the players into an array
          wins[j] = sc.nextInt();
          System.out.print(wins[j] + " ");
        }
        System.out.println();
        System.out.println("--------------------------");

        System.out.println("Schedule: ");
        System.out.println();
        for (int k = 0; k < i; k++) {
          for (int l = 0; l < i; l++) {
            //Imports the schedule of games to be played between all players in 3d array
            schedule[k][l] = sc.nextInt();
            System.out.print(schedule[k][l] + " ");
            //Sum of the remaining games for all players
            remainingGames[k] += schedule[k][l];
          }
          System.out.println();
        }
        System.out.println();
        System.out.println("--------------------------");

        System.out.println("Remaining Games for all players (Alphabetical Order): ");
        for (int z = 0; z < i; z ++) {
          System.out.print(remainingGames[z] + " ");
        }
        System.out.println();
        System.out.println("--------------------------");

        //Best ammount of wins for player A assuming they win all other games
        targetScoreA = wins[0] + remainingGames[0];
        System.out.println("Target Score A: " + targetScoreA);
        System.out.println("--------------------------");

        int[] maxNumOfOpponentWins = new int[i-1];
        System.out.println("Maximum Number of Additional Opponent Wins (Alphabetical Order): ");
        for (int x = 0; x < i - 1; x++) {
          //The maximum amount of wins for all other players where A will still win or tie
          maxNumOfOpponentWins[x] = targetScoreA - wins[x+1];
          System.out.print(maxNumOfOpponentWins[x] + " ");
        }
        System.out.println();

        //Different combination of matchups between teams without A present
        //i-2 choose 2
        int nFact = 1; //factorial(i-2)/(2*(i-4)) ;
        for (int i2 = 2; i2 <= i-1; i2++){
          nFact = nFact * i2;
        }
        int nMinuskFact = 1;
        for (int i3 = 2; i3 <= i-3; i3++){
          nMinuskFact = nMinuskFact * i3;
        }

        int matchups = 0;
        if (nFact != nMinuskFact) {
          matchups = nFact/(2*nMinuskFact);
        }

        /*
        Checks to see if there are only two players competing
        */
        if (matchups  < 1) {
          if (targetScoreA > wins[1]) {
            System.out.println("YES");
          }
          else {
            System.out.println("No");
          }
        }
        /*
        Conducts MaxFlow if there are more than two players
        */
        else {
          //Games to be played without A playing converted to array
          int[] UniqueGamesNoA = new int[matchups];
          int index = 0;
          for (int m = 1; m < i - 2; m++) {
            for (int m2 = m; m2 < i - 1 ; m2 ++) {
              UniqueGamesNoA[index] = schedule[m][m2 + 1];
              index++;
            }
          }
          UniqueGamesNoA[index] = schedule[i-2][i-1];

          //Number of players + Number of Matchups (not including A) + Sink and Source
          int vertices = i + matchups + 2;
          int graph[][] = new int[vertices][vertices];


          //Constructs graph to all 0s
          for (int c = 0; c < vertices; c++) {
            for (int c2 = 0; c2 < vertices; c2++) {
              graph[c][c2] = 0;
            }
          }

          //First Line of graph
          for (int r1 = 0; r1 < matchups; r1++) {
            graph[0][r1 + 1] =  UniqueGamesNoA[r1];
          }

          //Next matchup lines of graph
          int ColIncrease = 1;
          int RowIncrease = 1;
          int ind = 1;
          for (int c2 = i - 2; c2 > 0; c2 --) {
            for (int c3 = 0; c3 < c2; c3++) {
              graph[RowIncrease][matchups+ColIncrease] = 10000;
              graph[RowIncrease][matchups+ColIncrease + ind] = 10000;
              RowIncrease++;
              ind++;
            }
            ind = 1;
            ColIncrease++;
          }
          //Next i - 1 rows
          for (int r3 = matchups + 1; r3 < matchups + i; r3 ++) {
            graph[r3][vertices - 1] = targetScoreA - wins[r3 - matchups];
          }



          //Graph implementation
          Graph g = new Graph(vertices, graph);
          int source = 0;
          int destination = vertices - 1;
          //Finds max flow of the graph
          int max_flow = g.findMaxFlow(source,destination);
          System.out.println("---------------------------");
          System.out.println("Maximum flow from source: " + source + " to destination: " + destination
                  + " is: " + max_flow);
          int noOfUniqueWins = 0;
          //Amount of games between players (not A) where there will be a winner
          for (int p = 1; p < i; p++) {
            for (int q = p; q < i; q++) {
              noOfUniqueWins += schedule[p][q];
            }
          }
          System.out.println("---------------------------");
          System.out.println("Number of Unique Wins for games without A: " + noOfUniqueWins);
          System.out.println("---------------------------");

          /*
          If the number of unique wins between players (not including A) is greater
          than max flow which determines how many games opponents can win with player
          A still being the winner, than it is not possible.
          */
          if (max_flow < noOfUniqueWins) {
            System.out.println("NO");
          }
          else {
            System.out.println("YES");
          }
        }


      }
      //Try Catch Block Exception
      catch(Exception ex) {
        ex.printStackTrace();
      }

    }
}
