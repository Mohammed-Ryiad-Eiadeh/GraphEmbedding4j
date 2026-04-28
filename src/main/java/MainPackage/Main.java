package MainPackage;

import ContextModel.ContextStrategy.WindowMode;
import ContextModel.SlidingWindow;
import Core.GraphBuilder;
import Core.GraphType;
import Core.VertexIndexMapping;
import NegativeSamplingModel.UniformNegativeSample;
import SampleDataset.PositiveAndNegativeSamples;
import WalkModel.DeepWalk;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        var graphDataFile = Paths.get(System.getProperty("user.dir"), "Graphs", "ca-citeseer.txt");
        var graphReader = Files.newBufferedReader(graphDataFile);

        var graphBuilder = new GraphBuilder<Integer>(GraphType.Directed);
        var headerLineId = 1;

        graphReader.lines().skip(headerLineId).forEach(line -> {
            String[] currentLine = line.trim().split(" ");
            var source = Integer.parseInt(currentLine[0]);
            var destination = Integer.parseInt(currentLine[1]);
            var weight = currentLine.length >= 3 ? Float.parseFloat(currentLine[2]) : 1.0f;
            graphBuilder.addConnection(source, destination, weight);
        });
        graphReader.close();

        var builder = graphBuilder
                .ifNotEmpty()
                .build();

        var numOfEdges = builder.edgeCount();
        var numOfVertices = builder.vertexCount();
        System.out.printf("Number of nodes: %s, Number of edges: %s\n",
                numOfVertices,
                numOfEdges);

        var mapper = new VertexIndexMapping<>(builder);

        var deepWalk = new DeepWalk<>(builder,
                mapper,
                100,
                4,
                12345L);

        var positiveNegativeSample = new PositiveAndNegativeSamples<>(deepWalk,
                new SlidingWindow(WindowMode.Symmetric, 3),
                new UniformNegativeSample<>(mapper, 12345L),
                Runtime.getRuntime().availableProcessors(),
                false,
                12345L);

        var sdate = System.currentTimeMillis();
        var positiveNegativeSampleDatasets = positiveNegativeSample
                .generatePositiveNegativeSampleDataset();
        var edate = System.currentTimeMillis();

        positiveNegativeSampleDatasets.forEach(System.out::println);
        System.out.println(edate - sdate + " is the duration time ");
    }
}
