package IO;

import Core.GraphBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * Converts rows from an edge-list file into weighted connections in a graph builder.
 *
 * @param <V> vertex type
 */
public class CSVGraphLoader<V> {
    private final GraphBuilder<V> graphBuilder;

    /**
     * Associates the loader with the builder that will receive parsed edges.
     *
     * @param graphBuilder the graph builder to update in-place
     */
    public CSVGraphLoader(GraphBuilder<V> graphBuilder) {
        this.graphBuilder = graphBuilder;
    }

    /**
     * Parses edge rows and adds their endpoints and weights to the associated builder.
     *
     * @param path the input file path
     * @param vertexParser converts vertex IDs from String to type V
     * @param skipLine number of header lines to skip
     */
    public void loadGraphIntoBuilder(Path path, Function<String, V> vertexParser, int skipLine) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.lines()
                    .skip(skipLine)
                    .filter(line -> !line.isBlank())
                    .filter(line -> !line.startsWith("L"))
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
