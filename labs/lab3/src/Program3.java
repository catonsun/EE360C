import java.util.HashMap;

/*
 * Anthony Weems
 * amw3647
 */

public class Program3 implements IProgram3 {

    private int numClasses;
    private int maxGrade;
    GradeFunction gf;
    private HashMap<Integer, int[]> cache;


    public Program3() {
        this.numClasses = 0;
        this.maxGrade = 0;
        this.gf = null;
        this.cache = new HashMap<Integer, int[]>();
    }

    public void initialize(int n, int g, GradeFunction gf) {
        this.numClasses = n;
        this.maxGrade = g;
        this.gf = gf;
    }

    /*
    G[H,j] = grade after H hours using j classes
    G[H, 0] = 0
    G[H, j] = max(G[H-i, j-1]+f_j(i)) for 0 <= i <= H

    O(H^2 * n)
    */
    public int[] computeHours(int totalHours) {
        int[] computeHours = new int[numClasses];
        int[][] G = new int[totalHours+1][numClasses+1];
        int[][] M = new int[totalHours+1][numClasses+1];

        if (cache.get(totalHours) != null) {
            return cache.get(totalHours);
        }

        for (int j = 1; j <= numClasses; j++) {
            for (int H = 0; H <= totalHours; H++) {
                int maxG = 0;
                int hour = 0;
                for (int i = 0; i <= H; i++) {
                    int g = G[H-i][j-1] + gf.grade(j-1, i);
                    if (g >= maxG) {
                        maxG = g;
                        hour = i;
                    }
                }
                if (maxG > G[H][j-1]) {
                    G[H][j] = maxG;
                    M[H][j] = hour;
                } else {
                    G[H][j] = G[H][j-1];
                    M[H][j] = -1;
                }
            }
        }

        int H = totalHours;
        int j = numClasses;

        while (j >= 0) {
            if (M[H][j] >= 0) {
                break;
            }
            j--;
        }

        do {
            computeHours[j-1] = M[H][j];
            H = H - M[H][j];
            j--;
        } while (j > 0 && M[H][j] >= 0);

        cache.put(totalHours, computeHours);
        return computeHours;
    }

    public int[] computeGrades(int totalHours) {
        int[] computeHours = computeHours(totalHours);
        int[] computeGrades = new int[numClasses];

        for(int j = 0; j < numClasses; j++) {
            computeGrades[j] = gf.grade(j, computeHours[j]);
        }        

        return computeGrades;
    }

}
