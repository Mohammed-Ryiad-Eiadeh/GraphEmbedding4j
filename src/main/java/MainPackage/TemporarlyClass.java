package MainPackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class TemporarlyClass {
    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\moham\\OneDrive\\Desktop\\Wiki_edgelist.txt"))) {
            var writer = new BufferedWriter(new FileWriter("C:\\Users\\moham\\OneDrive\\Desktop\\Wiki_Edges.txt"));

            br.lines().forEach(line -> {
                var preparedLine = line.split(" ");
                System.out.println(Arrays.toString(preparedLine));
                try {
                    writer.write((Integer.parseInt(preparedLine[0])+ 1) + " " + (Integer.parseInt(preparedLine[1])+ 1) + "\n");
                    writer.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
