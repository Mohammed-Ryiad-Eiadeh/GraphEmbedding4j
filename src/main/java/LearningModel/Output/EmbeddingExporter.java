package LearningModel.Output;

import Core.VertexIndexMapping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Exports learned node embeddings to a CSV file.
 *
 * @param <V> the vertex type
 */
public class EmbeddingExporter<V> {

    /**
     * Saves embeddings as:
     * NodeID,Feature1,Feature2,...
     *
     * @param path output file path
     * @param vertexIndexMapping maps node indices back to original vertices
     * @param embeddings learned node embeddings
     * @throws IOException if writing fails
     */
    public void saveEmbeddings(Path path, VertexIndexMapping<V> vertexIndexMapping, HashMap<Integer, double[]> embeddings) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            String header = "NodeID".concat(",");
            for (int i = 0; i < embeddings.values().stream().toList().getFirst().length; i++) {
                header = header.concat("Feature" + (i + 1) + ",");
            }
            writer.write(header + "\n");
            for (var embedding : embeddings.entrySet()) {
                String features = "";
                for (double embeddingValue : embedding.getValue()) {
                    features = features.concat(embeddingValue + ",");
                }
                writer.write(vertexIndexMapping.getVertex(embedding.getKey()) + "," + features + "\n");
            }
            writer.flush();
            System.out.println("Successfully wrote the Embeddings to the file.");
        } catch (IOException e) {
            throw  new IOException(e);
        }
    }
}
