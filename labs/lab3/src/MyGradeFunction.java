import java.util.Random;

public class MyGradeFunction implements GradeFunction{

    private int numClasses;
    private int maxGrade;

    public MyGradeFunction(int n, int g) {
        this.numClasses = n;
        this.maxGrade = g;
    }

    public int grade(int classID, int hours) {
        Random rng = new Random(classID);
        int grade = 0;
        for (int i = 0; i < hours; i++) {
            grade += rng.nextInt(2);
        }
        return grade;
    }
}
