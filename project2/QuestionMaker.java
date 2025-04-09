package project2;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/*
 * Created by Kyle Macdonald
 * Skims through txt file and compiles an array containing all questions present. each element in each line should be separated by #
 */

public class QuestionMaker {

    private static final String fName = "project2\\Questions.txt";
    private static Question[] questList = new Question[20];

    public static final Question[] makeQuestions(){
        try{
            File file = new File(fName);
            Scanner reader = new Scanner(file);
            int i = 0;
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                String[] lineSeg = line.split("#");

                questList[i] = new Question(lineSeg[0], Arrays.asList(lineSeg[1], lineSeg[2], lineSeg[3], lineSeg[4]), Integer.valueOf(lineSeg[5]));
                i++;
            }
            reader.close();
            return questList;
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }
}
