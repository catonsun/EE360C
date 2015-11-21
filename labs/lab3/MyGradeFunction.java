import java.util.Random;

public class MyGradeFunction implements GradeFunction{

    private final int MAX_GRADE = 10;
    private int numClasses;
    private int maxGrade;
    private Random rng;

    public MyGradeFunction(int n, int g) {
        this.rng = new Random(4);
        this.numClasses = n;
        this.maxGrade = g;
    }

    public int grade(int classID, int hours) {
        return rng.nextInt(MAX_GRADE + 1);
    }
}
