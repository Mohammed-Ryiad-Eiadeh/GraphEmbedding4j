# GraphEmbedding4j

A modular Java library for random-walk–based graph representation learning, designed for research experimentation, algorithmic exploration, and extensible embedding pipelines.

---

## Overview

GraphEmbedding4j is an exploratory Java framework for learning node embeddings from graphs using random walks and Skip-Gram–style objectives.

The project focuses on building a clean and flexible architecture that separates the major stages of graph embedding pipelines, allowing individual components to be independently extended, replaced, or experimentally evaluated.

Rather than targeting production deployment, the library is intended to support:

* Graph ML research
* Rapid prototyping
* Educational experimentation
* Embedding pipeline design
* Adversarial and dynamic graph learning exploration

---

## Current Features

### Random Walk–Based Embedding Pipeline

* DeepWalk-style random walk generation
* Sliding-window context extraction
* Positive and negative sample generation
* Skip-Gram embedding learning
* CSV embedding export utilities

## Modular Learning Components

| Module Category | Currently Implemented | Planned Extensions |
|---|---|---|
| **Activation Functions** | Sigmoid, Tanh | ELU, Leaky ReLU |
| **Optimization Algorithms** | SGD, Momentum SGD, AdaGrad, RMSProp, AdaDelta, Sign SGD, AdaMax, AdamW | Adam |
| **Embedding Initialization** | Random Uniform Initialization | Xavier Initialization, He Initialization |
| **Sampling Strategies** | Positive/Negative Sampling, Symmetric Sliding-Window Extraction, Uniform Negative Sampling | Degree-Biased Sampling, Adaptive Sampling, Adversarial Negative Sampling |
| **Walk Engines** | DeepWalk | Node2Vec-Style Biased Walks, Dynamic Graph Walks, Temporal Random Walks |

---

## Design Philosophy and Diagram

<img width="1690" height="931" alt="30889662-bc16-4699-8e7f-cd6a4f768ce0" src="https://github.com/user-attachments/assets/4a3366a9-16c2-48a1-8d67-f67b4f61416a" />

The framework is organized around clear separation of concerns.

### Walk Generation

Responsible for generating node sequences from graphs.

### Context Modeling

Extracts target-context relationships using sliding-window strategies.

### Negative Sampling

Produces negative training samples from the global node space.

### Dataset Construction

Builds training-ready positive and negative sample datasets.

### Embedding Learning

Optimizes node representations using configurable learning objectives, activation functions, and optimization algorithms.

This modular design enables experimental combinations of embedding strategies without tightly coupling components.

---

## Building a Graph

The graph shown in the figure can be constructed manually using the GraphBuilder API.

<p align="center">
  <img width="500" height="250" alt="Screenshot (228)" src="https://github.com/user-attachments/assets/dd27acc0-828b-4c2d-9685-0f41163ddf7e" />
</p>

```java
        // Construct graph manually
        var graphBuilder = new GraphBuilder<String>(GraphType.Directed);
        
        graphBuilder.addConnection("v1", "v2", 1.0f);
        graphBuilder.addConnection("v2", "vm4", 1.0f);
        graphBuilder.addConnection("v2", "vm5", 1.0f);
        graphBuilder.addConnection("v3", "v1", 1.0f);
        graphBuilder.addConnection("v3", "vm4", 1.0f);
        graphBuilder.addConnection("vm4", "vm5", 1.0f);
        
        // Build immutable graph snapshot
        ImmutableGraphData<String> graph = graphBuilder.build();
```

## Example 1: Learning Node Embeddings on the Karate Graph

```java
        // Load the graph from Graphs
        var graphDataFile = Paths.get(System.getProperty("user.dir"), "Graphs", "karate.txt");
        var graphReader = Files.newBufferedReader(graphDataFile);

        var graphBuilder = new GraphBuilder<Integer>(GraphType.Directed);
        var headerLineId = 1;

        graphReader.lines().skip(headerLineId).forEach(line -> {
            String[] currentLine = line.trim().split("\\s+");
            var source = Integer.parseInt(currentLine[0]);
            var destination = Integer.parseInt(currentLine[1]);
            var weight = currentLine.length >= 3 ? Float.parseFloat(currentLine[2]) : 1.0f;
            graphBuilder.addConnection(source, destination, weight);
        });
        graphReader.close();

        // Build a directed graph using GraphBuilder.
        var builder = graphBuilder
                .ifNotEmpty()
                .build();

        var numOfEdges = builder.edgeCount();
        var numOfVertices = builder.vertexCount();
        System.out.printf("Number of nodes: %s, Number of edges: %s\n",
                numOfVertices,
                numOfEdges);

        System.out.println();

        // Map each vertex to an internal index using VertexIndexMapping.
        var mapper = new VertexIndexMapping<>(builder);

        // Generate random walks using DeepWalk.
        var deepWalk = new DeepWalk<>(builder,
                mapper,
                10,
                4,
                12345L);

        // Create positive and negative samples using 1) right sliding window and 2) uniform negative sampling
        var positiveNegativeSample = new PositiveAndNegativeSamples<>(deepWalk,
                new SlidingWindow(WindowMode.Right, 5),
                new UniformNegativeSample<>(mapper),
                20,
                false,
                12345L);

        // Initialize node embeddings using random Gaussian initialization.
        var gaussianInitializer = new GaussianDistributionInitializer<>(builder,
                256,
                12345L);

        // Define a Skip-Gram model
        var skipGramModel = new SkipGram<>(gaussianInitializer,
                positiveNegativeSample,
                new AdamW(0.9, 0.999, 0.0001, 1e-8, 0.01),
                new SigmoidFunction(),
                100);

        // Train the model
        long startTime = System.currentTimeMillis();
        skipGramModel.trainModel();
        long endTime = System.currentTimeMillis();

        System.out.println("Training time: " + Util.formatDuration(startTime, endTime) + " ms");

        // Export the learned embeddings to Karate_embeddings.csv
        var embeddings = new HashMap<>(skipGramModel.getEmbeddings());
        var stringPath = "C:\\Users\\moham\\OneDrive\\Desktop\\karkar.csv";
        new EmbeddingExporter<Integer>().saveEmbeddings(Paths.get(stringPath),
                mapper,
                embeddings);
```

### Configuration

```text
Dataset: Karate graph
Graph type: Directed
Walk length: 10
Number of walks per node: 4
Window mode: Right
Window size: 5
Negative sampling: Uniform
Number of negative sample nodes: 20
Embedding dimension: 256
Optimizer: AdamW
beta1: 0.9
beta2: 0.999
Learning rate: 0.0001
epsilon: 1e-8
lambda: 0.01
Activation function: Sigmoid
Epochs: 100
Random seed: 12345
```

---

## Example 2: Evaluating Learned Embeddings via Tribuo ML library (please add the dependency of Tribuo before executing the code & drop the NodeId column from the embeddings data file)

```java
        // Read the embedding dataset.
        var dataPath = Paths.get(
                System.getProperty("user.dir"),
                "results",
                "Karate_embeddings_set.csv"
        );
        
        var dataSource = new CSVLoader<>(new LabelFactory())
                .loadDataSource(dataPath, "Class");
        
        var data = new MutableDataset<>(dataSource);
        
        // Define a Factorization Machine classifier.
        var fmTrainer = new FMClassificationTrainer(
                new Hinge(),
                new AdaGrad(0.01, 0.9),
                50,
                Trainer.DEFAULT_SEED,
                10,
                0.2D
        );
        
        // Perform 7-fold cross-validation.
        var crossValidation = new CrossValidation<>(
                fmTrainer,
                data,
                new LabelEvaluator(),
                7
        );
        
        // Compute evaluation metrics.
        var avgAcc = 0D;
        var avgRecall = 0D;
        var avgF1 = 0D;
        var avgPrecision = 0D;
        
        for (var performance : crossValidation.evaluate()) {
            avgAcc += performance.getA().accuracy();
            avgRecall += performance.getA().macroAveragedRecall();
            avgF1 += performance.getA().macroAveragedF1();
            avgPrecision += performance.getA().macroAveragedPrecision();
        }
        
        System.out.println("The Training_Testing duration time is : " + Util.formatDuration(sTrain, eTrain));
        System.out.println("Average accuracy: " + avgAcc / crossValidation.getK());
        System.out.println("Average recall: " + avgRecall / crossValidation.getK());
        System.out.println("Average F1-score: " + avgF1 / crossValidation.getK());
        System.out.println("Average precision: " + avgPrecision / crossValidation.getK());
```

### Experimental Results

Using 256-dimensional random-walk node embeddings and a Factorization Machine classifier with 7-fold cross-validation:

```text
The Training_Testing duration time is : (00:00:00:818)
The average accuracy is : 0.9714285714285714
The average recall is : 0.9285714285714286
The average F1-Score is : 0.9206349206349207
The average precision is : 0.9142857142857144
```

---

## Research Directions

Potential future directions include:

* Adversarial graph embedding learning
* FGSM-style embedding perturbations
* Dynamic and temporal graph embeddings
* Meta-learning for graph representation selection
* Robustness analysis under graph perturbations
* Embedding regularization using graph neighborhood consistency
* Markov Blanket–aware embedding objectives

---

## Project Status

🚧 Early research-oriented implementation.

The project currently prioritizes modularity, experimentation, and API design over large-scale optimization or production deployment.

APIs and internal implementations may evolve significantly.

---

## Disclaimer

This is a research and experimentation project intended for educational and exploratory purposes.

The implementation is provided as-is and may change without notice.

---

## License

Apache License 2.0
