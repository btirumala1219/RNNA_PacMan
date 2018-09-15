/*
   UCF
   CAP4630 - Fall 2018
   Author: Barath Tirumala
*/

import java.awt.Point;
import java.util.*;
import pacsim.*;

//extend pac action as stated

public class PacSimRNNA implements PacAction
{
    //init all needed structures
    private List<Point> path;
    private int simTime;
    private static boolean plan = true;
    public static List<Point> targets;
    public static HashSet<Point> visited;

    public PacSimRNNA(String fname)
    {
        PacSim sim = new PacSim(fname);
        sim.init(this);
    }
    
    
    // MAIN METHOD
    public static void main(String [] args)
    {
        new PacSimRNNA(args[0]);
        printTitle();
        System.out.println("\nMaze : " + args[0] + "\n");
    }

    @Override
    public void init()
    {
        simTime = 0;
        path = new ArrayList();
    }

    private void printFoodArray(List<Point> food)
    {
        System.out.println("Food Array:\n");

        int len = food.size();

        for(int i = 0; i < len; i++)
        {
            // update the X and Y coordiantes for each new food pellet
            int x = (int)(food.get(i)).getX();
            int y = (int)(food.get(i)).getY();

            System.out.println(i + " : (" + x + "," + y + ")");
        }
        System.out.println();
    }

    private static void printTitle()
    {
        System.out.println();
        System.out.println();
        System.out.println("TSP using Repetitive Nearest Neighbor Algorithm by Barath Tirumala");
    }

    private static int[][] printCostTable(List<Point> food, Point pc, PacCell[][] grid)
    {
        System.out.println("Cost Table:");
        System.out.println();

        int foodSize = food.size();
        int [][] costTable = new int[foodSize+1][foodSize+1];

        for(int i = 0; i < foodSize; i++)
        {
            for(int j = 0; j < foodSize; j++)
            {

                Point x = food.get(i);
                Point y = food.get(j);
                List<Point> p = BFSPath.getPath(grid, pc, x);
                List<Point> p2 = BFSPath.getPath(grid, pc, y);
                int xLen = p.size();
                int yLen = p2.size();

                costTable[i+1][0] = xLen;
                costTable[0][j+1] = yLen;
            }
        }

        for(int i = 0; i < foodSize; i++)
        {
            for(int j = 0; j < foodSize; j++)
            {
                // calc distance between foods
                Point x = food.get(i);
                Point y = food.get(j);
                List<Point> p = BFSPath.getPath(grid, x, y);
                
                int len = p.size();

                //add distance to table
                costTable[i+1][j+1] = len;
            }
        }

        // prnt
        for(int i = 0; i < foodSize+1; i++)
        {
            for(int j = 0; j < foodSize+1; j++)
                System.out.format("%4d", costTable[i][j]);
            System.out.println();
        }
        System.out.println();
        return costTable;
    }

    //print time taken
    private void printTime(int a)
    {
        System.out.println("Time to generate plan: " + a + " msec");
        System.out.println();
        System.out.println();
    }

    private ArrayList<Point> nearFood(Point p, List<Point> food, List<Point> arr, PacCell[][] grid)
    {
        if(arr.size() <= 0)
        {
            return null;
        }
        ArrayList<Point> nearestPellets = new ArrayList<Point>();
        Point newLoc = arr.get(0);
        int cost = BFSPath.getPath(grid,p,newLoc).size();

        for(int i = 1; i < arr.size(); i++)
        {
            newLoc = arr.get(i);
            int newCost = BFSPath.getPath(grid,p,newLoc).size();
            if(newCost < cost)
            {
                cost = newCost;
            }
        }

        for(int i = 0; i < arr.size(); i++)
        {
            newLoc = arr.get(i);
            int pathCost = BFSPath.getPath(grid,p,newLoc).size();
            if(pathCost == cost)
            {
                nearestPellets.add(newLoc);
            }
        }

        return nearestPellets;
    }

     public List<Point> PacPlanner(PacCell [][] grid, PacmanCell pc)
     {
        System.out.println();
        List<Point> food = PacUtils.findFood(grid);
        int size = PacUtils.numFood(grid);
        Point pacman = pc.getLoc();

        int [][] costValues = printCostTable(food, pacman, grid);
        printFoodArray(food);
        int lowest = 100;
        int cost = 0;

        ArrayList<Node> costTable = new ArrayList<Node>(size);
        // init table
        for(int row = 0; row < size; row++)
        {   
            Point position = food.get(row);
            
            cost = BFSPath.getPath(grid,pacman,position).size();
            Node n = new Node(position, cost, PacUtils.cloneGrid(grid), costValues);
            costTable.add(n);
        }

        cost = 0;
        List<Point> optimalPath = new ArrayList<Point>();
        int nodeIndex = 0;
        int table = food.size();
        int stepNumber = 0;
        for(int f = 0; f < food.size(); f++)
        {
            //print
            System.out.println();
            System.out.println("Population at step "+ stepNumber++ +" : ");
            System.out.println();
            table = costTable.size();
            nodeIndex = 0;
            ArrayList<Node> tempCostTable = new ArrayList<Node>();

            for(int i = 0; i < costTable.size(); i++)
            {
                Node n = costTable.get(i);
                Point loc = n.getLocation();
                ArrayList<Point> nearestPellets = nearFood(loc, food ,n.getLeftOvers(),grid);
                if(nearestPellets != null)
                {
                    for(int j = 1; j < nearestPellets.size(); j++)
                    {
                        Point newLoc = nearestPellets.remove(j);
                        List<Point> newGrid = new ArrayList<Point>();
                        ArrayList<Point> newPath = new ArrayList<Point>();
                        newGrid = PacUtils.clonePointList(n.getLeftOvers());
                        newPath.addAll(n.getPath());
                        Node temp = new Node (newPath, n.getCost(), newGrid, PacUtils.cloneGrid(grid), costValues);

                        // calculate the new cost
                        int newCost = BFSPath.getPath(grid,loc,newLoc).size();
                        temp.setCost(newCost);
                        temp.addLocation(newLoc);
                        tempCostTable.add(temp);
                    }
                    int newCost = BFSPath.getPath(grid,loc,nearestPellets.get(0)).size();
                    n.setCost(newCost);
                    n.addLocation(nearestPellets.get(0));
                }
            }

            Collections.sort(costTable);

            System.out.print(nodeIndex++);
            costTable.get(0).print(stepNumber, grid);

            for(int i = 1; i < costTable.size(); i++)
            {
                System.out.print(nodeIndex++);
                costTable.get(i).print(stepNumber, grid);
            }

            costTable.addAll(tempCostTable);
        }

        for(int i = 0; i < costTable.size(); i++)
        {
            if(i == 0)
            {
                cost = costTable.get(0).getCost();
                optimalPath = costTable.get(0).getPath();
            }
            else if(costTable.get(i).getCost() < cost)
            {
                cost = costTable.get(i).getCost();
                optimalPath = costTable.get(i).getPath();
            }
        }
        return optimalPath;
     }

     @Override
     public PacFace action(Object state)
     {

        PacCell[][] grid = (PacCell[][]) state;
        PacmanCell pc = PacUtils.findPacman(grid);

        if(pc == null) return null;

        if(plan) 
        {
            long before = System.currentTimeMillis();
            targets = PacPlanner(grid, pc);
            long after = System.currentTimeMillis();
            plan = false;
            printTime((int)(after - before));
            System.out.println("Solution moves: ");
            System.out.println();
            System.out.println();
            visited = new HashSet<Point>();
        }

        if(path.isEmpty())
        {
            Point tgt = targets.remove(0);
            path = BFSPath.getPath(grid, pc.getLoc(), tgt);
        }

         Point next = path.remove(0);
         visited.add(next);
         PacFace face = PacUtils.direction( pc.getLoc(), next );

         System.out.printf( "%5d : From [ %2d, %2d ] go %s%n",
               ++simTime, pc.getLoc().x, pc.getLoc().y, face );

         return face;
     }
}


class Node implements Comparable<Node>
{
    public int size;
    public int cost;
    public ArrayList<Point> path;
    public List<Point> notEaten;
    private HashMap<Point,Integer> primaryCosts;

    Node(Point position, int c, PacCell[][] grid, int[][] costValues)
    { 
        path = new ArrayList<Point>();
        notEaten = new ArrayList<Point>();
        path.add(position);
        notEaten = PacUtils.findFood(grid);
        generateHashMap(costValues, notEaten);
        notEaten.remove(position);
        cost = c;
        size = 1;
    }

    Node(ArrayList<Point> path, int cost, List<Point>grid, PacCell[][] gr, int[][] costValues)
    {
        this.path = new ArrayList<Point>();
        notEaten = grid;
        this.cost = cost;
        size = path.size();
        List<Point> newGrid = new ArrayList<Point>();
        this.path.addAll(path);
        newGrid.addAll(grid);
        generateHashMap(costValues,PacUtils.findFood(gr));
    }

    public void addLocation(Point loc)
    {
        path.add(loc);
        notEaten.remove(loc);
        size++;
    }

    public Point getLocation()
    {
        return path.get(size-1);
    }

    public ArrayList<Point> getPath()
    {
        return path;
    }

    public void setCost(int c)
    {
        cost += c;
    }

    public int getCost()
    {
        return cost;
    }

    private void generateHashMap(int[][] costValues, List<Point> food)
    { 
        primaryCosts = new HashMap<Point,Integer>();
        for(int i = 0 ; i < food.size(); i++)
        {
            primaryCosts.put(food.get(i),costValues[0][i+1]);
        }
    }

    public void print(int size, PacCell[][] grid)
    {
        if(size > path.size() + notEaten.size()){size--;}

        System.out.print(" :  cost=" + cost + " : " );
        Point p = path.get(0);
        int x = (int)p.getX();
        int y = (int)p.getY();
        int c = primaryCosts.get(p);
        System.out.print("[(" + x + "," + y + "),"+ c +"] ");

        for(int i = 1; i < size; i++)
        {
            p = path.get(i);
            x = (int)p.getX();
            y = (int)p.getY();
            c = BFSPath.getPath(grid,path.get(i),path.get(i-1)).size();
            System.out.print("[(" + x + "," + y + "),"+ c +"] ");
        }
        System.out.println();
    }

    public List<Point> getLeftOvers()
    {
        return this.notEaten;
    }
    @Override
    public int compareTo(Node comparesTu)
    {
        int comp =((Node)comparesTu).getCost();
        return this.cost - comp;
    }
}

