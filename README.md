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

### Modular Learning Components

#### Activation Functions

* Sigmoid
* Tanh

Planned:

* ReLU
* GELU
* Softmax

#### Optimization Algorithms

* SGD
* Momentum SGD

Planned:

* Adam
* AdaGrad
* RMSProp
* AdamW

#### Embedding Initialization

* Random Uniform Initialization

Planned:

* Xavier Initialization
* He Initialization

#### Sampling Strategies

* Positive/Negative sampling
* Symmetric sliding-window extraction
* Uniform negative sampling

Planned:

* Degree-biased sampling
* Adaptive sampling
* Adversarial negative sampling

#### Walk Engines

* DeepWalk

Planned:

* Node2Vec-style biased walks
* Dynamic graph walks
* Temporal random walks

---

## Design Philosophy

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

## Example 1: Learning Node Embeddings on the Karate Graph

```java
// Load the graph from Graphs/Karate.txt.
var graphDataFile = Paths.get(System.getProperty("user.dir"), "Graphs", "Karate.txt");
var graphReader = Files.newBufferedReader(graphDataFile);

var graphBuilder = new GraphBuilder<Integer>(GraphType.Directed);
var headerLineId = 1;

graphReader.lines().skip(headerLineId).forEach(line -> {
    String[] currentLine = line.trim().split("\\s+");

    var source = Integer.parseInt(currentLine[0]);
    var destination = Integer.parseInt(currentLine[1]);
    var weight = currentLine.length >= 3
            ? Float.parseFloat(currentLine[2])
            : 1.0f;

    graphBuilder.addConnection(source, destination, weight);
});

graphReader.close();

// Build the graph.
var graph = graphBuilder
        .ifNotEmpty()
        .build();

// Create node-index mappings.
var mapper = new VertexIndexMapping<>(graph);

// Generate random walks.
var deepWalk = new DeepWalk<>(
        graph,
        mapper,
        10,
        4,
        12345L
);

// Generate positive and negative samples.
var positiveNegativeSample = new PositiveAndNegativeSamples<>(
        deepWalk,
        new SlidingWindow(WindowMode.Symmetric, 3),
        new UniformNegativeSample<>(mapper, 12345L),
        false,
        12345L
);

// Initialize node embeddings.
var initializer = new RandomUniformInitializer<>(
        graph,
        256,
        12345L
);

// Define the Skip-Gram model.
var skipGramModel = new SkipGram<>(
        initializer,
        positiveNegativeSample,
        new SGD(0.01),
        new SigmoidFunction(),
        100
);

// Train the model.
skipGramModel.trainModel();

// Export embeddings.
var embeddings = new HashMap<>(skipGramModel.getEmbeddings());

var outputPath = Paths.get(
        System.getProperty("user.dir"),
        "results",
        "Karate_embeddings.csv"
);

new EmbeddingExporter<Integer>().saveEmbeddings(
        outputPath,
        mapper,
        embeddings
);
```

### Configuration

```text
Dataset: Karate graph
Graph type: Directed
Walk length: 10
Number of walks per node: 4
Window mode: Symmetric
Window size: 3
Negative sampling: Uniform
Embedding dimension: 256
Optimizer: SGD
Learning rate: 0.01
Activation function: Sigmoid
Epochs: 100
Random seed: 12345
```

---

## Example 2: Evaluating Learned Embeddings via Tribuo ML library (add the dependency)

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
Accuracy  : 94.29%
Recall    : 95.83%
Precision : 94.05%
F1-Score  : 93.74%
Runtime   : 00:00:00:720
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
