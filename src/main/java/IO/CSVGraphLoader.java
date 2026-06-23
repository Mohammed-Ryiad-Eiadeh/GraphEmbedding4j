package IO;

import Core.GraphBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * Loads graph edges from an edge-list file into an existing GraphBuilder.
 *
 * @param <V> vertex type
 */
public class CSVGraphLoader<V> {
    private final GraphBuilder<V> graphBuilder;

    /**
     * Creates a graph loader that writes loaded edges into the provided builder.
     *
     * @param graphBuilder the graph builder to update in-place
     */
    public CSVGraphLoader(GraphBuilder<V> graphBuilder) {
        this.graphBuilder = graphBuilder;
    }

    /**
     * Loads graph edges into the provided {@link GraphBuilder}.
     *
     * @param path the input file path
     * @param vertexParser converts vertex IDs from String to type V
     * @param skipLine number of header lines to skip
     * @throws IOException if reading the file fails
     */
    public void loadGraphIntoBuilder(Path path, Function<String, V> vertexParser, int skipLine) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.lines()
                    .skip(skipLine)
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {
                        String[] currentLine = line.trim().split("\\s+");

                        V source = vertexParser.apply(currentLine[0]);
                        V destination = vertexParser.apply(currentLine[1]);
                        float weight = currentLine.length >= 3
                                ? Float.parseFloat(currentLine[2])
                                : 1.0f;

                        graphBuilder.addConnection(source, destination, weight);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
