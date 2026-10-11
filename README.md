# GraphEmbedding4j

A modular Java research framework for random-walk-based graph representation learning, including DeepWalk, Node2Vec, and Markov Blanket-based MB2Vec.

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

* DeepWalk, Node2Vec, and MB2Vec walk generation
* Sliding-window context extraction
* Positive and negative sample generation
* Skip-Gram embedding learning
* CSV embedding export utilities

## Modular Learning Components

| Module Category | Currently Implemented | Planned Extensions |
|---|---|---|
| **Activation Functions** | Sigmoid, Tanh | ELU, Leaky ReLU |
| **Optimization Algorithms** | SGD, Momentum SGD, AdaGrad, RMSProp, AdaDelta, Sign SGD, AdaMax, AdamW | Adam |
| **Embedding Initialization** | Uniform random, Gaussian | Xavier Initialization, He Initialization |
| **Sampling Strategies** | Positive/Negative Sampling, Symmetric Sliding-Window Extraction, Uniform Negative Sampling | Degree-Biased Sampling, Adaptive Sampling, Adversarial Negative Sampling |
| **Walk Engines** | DeepWalk, Node2Vec biased walks (roulette-wheel sampling), MB2Vec Markov Blanket-biased walks (roulette-wheel sampling) | Dynamic Graph Walks, Temporal Random Walks |

---

## Walk Strategies

| Strategy | Candidate nodes | Transition selection |
|---|---|---|
| **DeepWalk** | Graph neighbors | Uniform random walk |
| **Node2Vec** | Graph neighbors | Second-order walk biased by return and in/out parameters (`p`, `q`) |
| **MB2Vec** | Markov Blanket of the current node | Context-dependent second-order walk using five transition weights (`a1`–`a5`) |

### MB2Vec: Markov Blanket-Based Walks

For a node `v` in a directed graph, its Markov Blanket consists of its **parents**, **children**, and **co-parents** (other nodes sharing a child with `v`). MB2Vec expands the candidate walk neighborhood to include these structural dependencies, including co-parents that need not be connected by a direct edge.

Given the previous node `t`, current node `v`, and candidate next node `u` in `MB(v)`, the implementation assigns a transition weight according to the **first matching** condition:

| Weight | Condition |
|---|---|
| `a1` | `u == t` (return) |
| `a2` | `u` is a child of `t` |
| `a3` | `u` is a parent of `t` |
| `a4` | `u` is otherwise in `MB(t)` (e.g., a co-parent) |
| `a5` | `u` is outside `MB(t)` |

The candidate is then selected using roulette-wheel sampling, with normalized probability

$$
P(u\mid t,v)=\frac{w(t,v,u)}{\sum_{z\in MB(v)}w(t,v,z)}.
$$

The **first step** from the starting node is sampled uniformly from its Markov Blanket. Subsequent steps use the weighted rule above. The weights should be nonnegative, with a positive total weight for the candidate set.

**Implementation:** `src/main/java/WalkModel/MB2Vec.java`. See `src/main/java/MainPackage/LearnEmbeddings_MB2Vec.java` for an entry-point example.

---

## Design Philosophy and Diagram



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

## Getting Started

**Requirements:** Java JDK compatible with the project’s `pom.xml`, Maven, and an IDE such as IntelliJ IDEA. Check the Maven configuration for the exact Java version and dependencies.

```bash
git clone https://github.com/Mohammed-Ryiad-Eiadeh/GraphEmbedding4j.git
cd GraphEmbedding4j
mvn compile
```

Open the cloned directory as a Maven project in IntelliJ IDEA. The example entry points are under `src/main/java/MainPackage/`. Dataset files are stored in `Graphs/` and labels in `GroundTruths/`.

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

## Example 1: Learning Node Embeddings (via Node2Vec Walks) on the Karate Graph

```java
        // Load the graph from Graphs
        var graphDataFile = Paths.get(System.getProperty("user.dir"), "Graphs", "karate_Edges.txt");

        // Load the graph into the passed builder (explicitly update in-place)
        var graphBuilder = new GraphBuilder<Integer>(GraphType.Directed);
        new CSVGraphLoader<>(graphBuilder).loadGraphIntoBuilder(graphDataFile,
                Integer::parseInt,
                1);

        // Build a directed graph using GraphBuilder
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

        // Generate walks using Node2Vec.
        var Walk = new Node2Vec<>(builder,
                mapper,
                10,
                3,
                0.5,
                2.0,
                12345L,
                12345L);

        // Create positive and negative samples using 1) right sliding window and 2) uniform negative sampling
        var positiveNegativeSample = new PositiveNegativeSamples<>(Walk,
                new SlidingWindow(WindowMode.Right, 4),
                new UniformNegativeSampler<>(mapper),
                20,
                false,
                12345L);

        // Initialize node embeddings using random Gaussian initialization.
        var embeddingInitializer = new EmbeddingInitializer<>(builder,
                InitializerMode.Gaussian,
                256,
                12345L);

        // Define a Skip-Gram model
        var skipGramModel = new SkipGram<>(embeddingInitializer,
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
        var stringPath = Paths.get(System.getProperty("user.dir"), "Karate_embeddings.csv");
        new CSVNodeEmbeddingExporter<Integer>().saveNodeEmbeddings(stringPath,
                mapper,
                embeddings);
```

### Configuration

```text
Dataset: Karate graph
Graph type: Directed
Walk length: 10
Number of walks per node: 3
p: 0.5
q: 2.0
Window mode: Right
Window size: 4
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

## Example 2: Evaluating Embeddings with Tribuo

This example uses the **Tribuo ML library** (add the relevant Tribuo dependencies to `pom.xml`). Prepare a labeled embedding CSV with a `Class` column and remove the `NodeId` column from the feature inputs before running the example. The example assumes the labeled CSV exists at `results/Karate_embeddings_set.csv`.

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
        
        // Evaluate the model across cross-validation folds.
        long sTrain = System.currentTimeMillis();
        var performances = crossValidation.evaluate();
        long eTrain = System.currentTimeMillis();

        var listAcc = new java.util.ArrayList<Double>();
        double avgAcc = 0D;
        double avgRecall = 0D;
        double avgF1 = 0D;
        double avgPrecision = 0D;

        for (var performance : performances) {
            double accuracy = performance.getA().accuracy();
            listAcc.add(accuracy);
            avgAcc += accuracy;
            avgRecall += performance.getA().macroAveragedRecall();
            avgF1 += performance.getA().macroAveragedF1();
            avgPrecision += performance.getA().macroAveragedPrecision();
        }

        int k = listAcc.size();
        if (k < 2) {
            throw new IllegalStateException("At least two folds are required for sample SD.");
        }

        double meanAcc = avgAcc / k;
        double stdAcc = Math.sqrt(listAcc.stream()
                .mapToDouble(acc -> Math.pow(acc - meanAcc, 2))
                .sum() / (k - 1));

        System.out.println("Training/testing time: " + Util.formatDuration(sTrain, eTrain));
        System.out.printf("Accuracy (mean +/- SD): %.4f +/- %.4f%n", meanAcc, stdAcc);
        System.out.println("Average recall: " + avgRecall / k);
        System.out.println("Average F1-score: " + avgF1 / k);
        System.out.println("Average precision: " + avgPrecision / k);
```

### Experimental Results

Previously recorded example using 256-dimensional random-walk node embeddings and a Factorization Machine classifier with 7-fold cross-validation (the output below predates the addition of SD reporting):

```text
The Training_Testing duration time is : (00:00:00:600)
The average accuracy is : 0.9642857142857143
The average recall is : 0.9761904761904762
The average F1-Score is : 0.9619047619047619
The average precision is : 0.9642857142857143
```

---

### Interpreting Cross-Validation Results

The **mean accuracy** summarizes average accuracy across the folds. The **sample standard deviation** describes variation in fold accuracies:

$$
\bar A = \frac{1}{k}\sum_{i=1}^{k} A_i, \qquad
s_A = \sqrt{\frac{1}{k-1}\sum_{i=1}^{k}(A_i-\bar A)^2}.
$$

Report accuracy as `mean ± SD`, clearly identifying the dataset, walk method, classifier, and number of folds. Fold-to-fold SD is not the same as variability across independent random-seed runs.

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

Apache License 2.0. See [`LICENSE`](LICENSE).
