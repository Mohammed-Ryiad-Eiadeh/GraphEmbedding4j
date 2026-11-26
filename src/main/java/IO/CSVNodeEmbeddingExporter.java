package IO;

import Core.VertexIndexMapping;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Serializes node embedding vectors as one feature row per node in a CSV file.
 *
 * @param <V> the vertex type
 */
public class CSVNodeEmbeddingExporter<V> {

    /**
     * Exports vectors while translating internal indices back to original vertex labels.
     *
     * @param path the output file path
     * @param vertexIndexMapping maps node indices back to original vertices
     * @param embeddings the learned node embeddings
     * @throws IOException if writing fails
     */
    public void saveNodeEmbeddings(Path path, VertexIndexMapping<V> vertexIndexMapping, Map<Integer, double[]> embeddings) throws IOException {
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
            throw new IOException(e);
        }
    }

    /**
     * Exports vectors using their integer node indices as CSV identifiers.
     *
     * @param path the output file path
     * @param embeddings the learned node embeddings keyed by integer node ID
     * @throws IOException if writing fails
     */
    public void saveNodeEmbeddings(Path path, Map<Integer, double[]> embeddings) throws IOException {
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
                writer.write(embedding.getKey() + "," + features + "\n");
            }
            writer.flush();
            System.out.println("Successfully wrote the Embeddings to the file.");
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
