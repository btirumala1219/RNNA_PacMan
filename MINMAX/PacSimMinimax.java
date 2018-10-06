import java.awt.Point;
import java.util.*;
import pacsim.*;

public class PacSimMinimax implements PacAction {

  // class and instance variables

  public PacSimMinimax (int depth, String fname, int te, int gran, int max){

    // init some variables

    PacSim sim = new PacSim(fname, te, gran, max);
    sim.init(this);

  }

  public static void main(String[] args) {
    String fname = args[0];
    int depth = Integer.parseInt(args[1]);

    int te = 0;
    int gr = 0;
    int ml = 0;

    if(args.length == 5){
      te = Integer.parseInt(args[2]);
      gr = Integer.parseInt(args[3]);
      ml = Integer.parseInt(args[4]);
    }

    new PacSimMinimax(depth, fname, te, gr, ml);

    System.out.println("\nAdversarial Search using Minimax by Barath Tirumala: ");
    System.out.println("\n  Game Board  : " + fname);
    System.out.println("  Search Depth  : " + depth + "\n");

    if(te>0){
      System.out.println("  Preliminary runs : " + te
      +"\n  Granularity    : " + gr
      +"\n  Max move limit : " + ml
      +"\n\nPreliminary run results :\n");
    }
  }

  @Override
  public void init() {}

  @Override
  public PacFace action(Object state){
    PacCell[][] grid = (PacCell[][]) state;
    PacFace newFace = null;

      //our code here

    return newFace;
  }
}
