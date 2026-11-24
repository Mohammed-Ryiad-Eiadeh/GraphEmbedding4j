# GraphEmbedding4j

A modular Java research framework for graph representation learning, including DeepWalk, Node2Vec, Markov Blanket-based MB2Vec, and Truncated SVD.

---

## Overview

GraphEmbedding4j is an exploratory Java framework for learning node embeddings from graphs using random walks, Skip-Gram–style objectives, and matrix decomposition techniques.

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
* Sliding-window context extraction (Symmetric, Left, Right modes)
* Positive and negative sample generation
* Skip-Gram embedding learning
* CSV embedding export utilities

### Matrix-Based Embedding Pipeline

* Truncated SVD (Singular Value Decomposition) via EJML for low-rank adjacency matrix approximation

### Graph Visualization

* Interactive graph rendering via GraphStream (Swing)
* PNG image export at HD720 resolution
* Configurable node labels and edge weight display

### Downstream Evaluation (via Tribuo)

* Single-label node classification (FM classifier with cross-validation)
* Multi-label node classification (Linear SGD with cross-validation)
* Node clustering (K-Means++ with train/test split)

## Modular Learning Components

| Module Category | Currently Implemented | Planned Extensions |
|---|---|---|
| **Activation Functions** | Sigmoid, CLogLog | Tanh, ELU, Leaky ReLU |
| **Optimization Algorithms** | SGD, Momentum SGD, NAG, AdaGrad, RMSProp, AdaDelta, Sign SGD, AdaMax, AdamW | Adam |
| **Embedding Initialization** | Uniform, Gaussian, Exponential | Xavier Initialization, He Initialization |
| **Sampling Strategies** | Positive/Negative Sampling, Symmetric/Left/Right Sliding-Window Extraction, Uniform Negative Sampling | Degree-Biased Sampling, Adaptive Sampling, Adversarial Negative Sampling |
| **Walk Engines** | DeepWalk, Node2Vec biased walks (roulette-wheel sampling), MB2Vec Markov Blanket-biased walks (roulette-wheel sampling) | LINE (placeholder), Dynamic Graph Walks, Temporal Random Walks |
| **Graph Representations** | Adjacency List, Adjacency Matrix | — |

---

## Walk Strategies

| Strategy | Candidate nodes | Transition selection |
|---|---|---|
| **DeepWalk** | Graph neighbors | Uniform random walk |
| **Node2Vec** | Graph neighbors | Second-order walk biased by return and in/out parameters (`p`, `q`) |
| **MB2Vec** | Markov Blanket of the current node | Context-dependent second-order walk using four transition weights (`a1`–`a4`) |

### MB2Vec: Markov Blanket-Based Walks

For a node `v` in a directed graph, its Markov Blanket consists of its **parents**, **children**, and **co-parents** (other nodes sharing a child with `v`). MB2Vec expands the candidate walk neighborhood to include these structural dependencies, including co-parents that need not be connected by a direct edge.

Given the previous node `t`, current node `v`, and candidate next node `u` in `MB(v)`, the implementation assigns a transition weight according to the **first matching** condition:

| Weight | Condition |
|---|---|
| `a1` | `u == t` (return) |
| `a2` | `u` is a child of `t` |
| `a3` | `u` is a parent of `t` |
| `a4` | `u` is otherwise in `MB(t)` (e.g., a co-parent) |

The candidate is then selected using roulette-wheel sampling, with normalized probability

$$
P(u\mid t,v)=\frac{w(t,v,u)}{\sum_{z\in MB(v)}w(t,v,z)}.
$$

The **first step** from the starting node is sampled uniformly from its Markov Blanket. Subsequent steps use the weighted rule above. The four weights (`a1`–`a4`) should be nonnegative, with a positive total weight for the candidate set.

**Implementation:** `src/main/java/SamplingModel/MB2Vec.java`. See `src/main/java/MainPackage/LearnEmbeddingMain/LearnEmbeddings_MB2Vec.java` for an entry-point example.

---

## Design Philosophy and Diagram

<img width="1536" height="1024" alt="GraphEmbedding4j Pipeline Overview" src="https://github.com/user-attachments/assets/5d572326-6b9e-427f-8a0c-0a3384696873" />

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

**Requirements:** Java 21, Maven, and an IDE such as IntelliJ IDEA.

**Dependencies:**

| Dependency | Purpose |
|---|---|
| [Tribuo](https://tribuo.org/) 4.3.2 | ML evaluation (classification, clustering, multi-label) |
| [GraphStream](https://graphstream-project.org/) 2.0 | Interactive graph visualization and PNG export |
| [EJML](http://ejml.org/) 0.46.1 | Matrix operations and SVD decomposition |

```bash
git clone https://github.com/Mohammed-Ryiad-Eiadeh/GraphEmbedding4j.git
cd GraphEmbedding4j
mvn compile
```

Open the cloned directory as a Maven project in IntelliJ IDEA. The example entry points are under `src/main/java/MainPackage/LearnEmbeddingMain/` for learning embeddings and `src/main/java/MainPackage/TestEmbeddingMain/` for downstream evaluation.

---

## Datasets

The project ships with 10 built-in graph datasets in the `Graphs/` directory, with corresponding labels/clusters in `GroundTruths/`:

| Dataset | Edges File | Labels File | Task |
|---|---|---|---|
| Karate Club | `karate_Edges.txt` | `Karate_Labels.csv` | Node classification |
| Cora | `cora_Edges.txt` | `cora_labels.csv` | Node classification |
| CiteSeer | `citeseer_Edges.txt` | `citeseer_Labels.csv` | Node classification |
| PubMed Blood Catalog | `BlodCatalog3_Edges.txt` | `BlodCatalog3_Labels.csv` | Multi-label classification |
| PolBooks | `polBooks_Edges.txt` | `polBooks_Labels.csv` | Node classification |
| Wikipedia | `Wiki_Edges.txt` | `Wiki_Labels.csv` | Node classification |
| Dolphins | `dolphins_Edges.txt` | `dolphins_Clusters.csv` | Node clustering |
| Email EU Core | `email-Eu-core_Edges.txt` | `email-Eu-core_Labels.csv` | Node classification |
| Musea Facebook | `musae_facebook_Edges.txt` | `musae_facebook_Labels.csv` | Node classification |
| PPI | `node2vec_PPI_Edges.txt` | `node2vec_PPI_Labels.csv` | Node classification |

---

## Building a Graph

The graph shown in the figure can be constructed manually using the GraphBuilder API.

<img width="973" height="670" alt="ToyGraph" src="https://github.com/user-attachments/assets/48358141-6fba-43ea-92e3-c734f4f11816" />

```java
        // Construct graph manually
        var graphBuilder = new GraphBuilder<String>(GraphType.Directed);
        graphBuilder.addConnection("v0", "v1", 1.0f);
        graphBuilder.addConnection("v0", "v2", 1.0f);
        graphBuilder.addConnection("v1", "v2", 1.0f);
        graphBuilder.addConnection("v1", "v3", 1.0f);
        graphBuilder.addConnection("v2", "v3", 1.0f);
        graphBuilder.addConnection("v3", "v4", 1.0f);
        graphBuilder.addConnection("v4", "v5", 1.0f);
        graphBuilder.addConnection("v4", "v6", 1.0f);
        graphBuilder.addConnection("v5", "v6", 1.0f);
        graphBuilder.addConnection("v6", "v7", 1.0f);
        
        // Build immutable graph snapshot
        ImmutableGraphData<String> graph = graphBuilder.build();
```

## Example 1: Learning Node Embeddings (via Node2Vec Walks) on the Karate Graph

See `src/main/java/MainPackage/LearnEmbeddingMain/LearnEmbeddings_Node2Vec.java` for the full source.

```java
        // Load the graph from Graphs
        var graphDataFile = Paths.get(System.getProperty("user.dir"), "\\Graphs\\karate_Edges.txt");

        // Load the graph into the passed builder (explicitly update in-place)
        var graphBuilder = new GraphBuilder<Integer>(GraphType.Directed);
        new CSVGraphLoader<>(graphBuilder).loadGraphIntoBuilder(graphDataFile,
                Integer::parseInt,
                1);       // Skip the header

        // Build a directed graph using GraphBuilder
        var builder = graphBuilder
                .ifNotEmpty()
                .build();

        // Visualize graph
        var graphVisualizer = new GraphVisualizer<>("Karate_Edges",
                builder,
                true,
                true,
                true);
        graphVisualizer.displaySingleGraph();

        var numOfEdges = builder.edgeCount();
        var numOfVertices = builder.vertexCount();
        System.out.printf("Number of nodes: %s, Number of edges: %s\n",
                numOfVertices,
                numOfEdges);

        System.out.println();

        // Map each vertex to an internal index using VertexIndexMapping.
        var mapper = new VertexIndexMapping<>(builder);

        // Generate walks using Node2Vec.
        var walk = new Node2Vec<>(builder,
                mapper,
                10,
                10,
                0.5,
                2.0,
                12345L,
                12345L);

        // Create positive and negative samples using 1) right sliding window and 2) uniform negative sampling
        var positiveNegativeSample = new PositiveNegativeSample<>(walk,
                new SlidingWindow(WindowMode.Right, 4),
                new UniformNegativeSample<>(mapper),
                20,
                true,
                12345L);

        // Initialize node embeddings using random Uniform initialization.
        var embeddingInitializer = new EmbeddingInitializer<>(builder,
                InitializerMode.Uniform,
                256,
                12345L);

        // Define a Skip-Gram model
        var skipGramModel = new SkipGram<>(embeddingInitializer,
                positiveNegativeSample,
                new SGD(0.001),
                new SigmoidFunction(),
                100);

        // Train the model
        long startTime = System.currentTimeMillis();
        skipGramModel.trainModel();
        long endTime = System.currentTimeMillis();

        System.out.println("Training time: " + Util.formatDuration(startTime, endTime) + " ms");

        // Export the learned embeddings to CSV
        var embeddings = new HashMap<>(skipGramModel.getEmbeddings());
        var stringPath = Paths.get("Node2Vec_karate_Edges.csv");
        new CSVNodeEmbeddingExporter<Integer>().saveNodeEmbeddings(stringPath,
                mapper,
                embeddings);
```

### Configuration

```text
Dataset: Karate graph
Graph type: Directed
Walk length: 10
Number of walks per node: 10
p: 0.5
q: 2.0
Window mode: Right
Window size: 4
Negative sampling: Uniform
Number of negative sample nodes: 20
Embedding dimension: 256
Optimizer: SGD
Learning rate: 0.001
Activation function: Sigmoid
Epochs: 100
Random seed: 12345
```

---

## Example 1b: Learning Node Embeddings via Truncated SVD

See `src/main/java/MainPackage/LearnEmbeddingMain/LearnEmbeddingTruncatedSVD.java` for the full source.

Instead of random walks, this approach learns embeddings by decomposing the graph's adjacency matrix via Singular Value Decomposition (SVD), producing low-rank node representations.

```java
        // Load the graph from Graphs
        var graphDataFile = Paths.get(System.getProperty("user.dir"), "\\Graphs\\karate_Edges.txt");

        var graphBuilder = new GraphBuilder<Integer>(GraphType.Directed);
        new CSVGraphLoader<>(graphBuilder).loadGraphIntoBuilder(graphDataFile,
                Integer::parseInt,
                1);

        var builder = graphBuilder.ifNotEmpty().build();
        var mapper = new VertexIndexMapping<>(builder);

        // Represent the graph as an adjacency matrix
        var representationAsMatrix = new ImmutableAdjacentMatrix<>(builder, mapper.getVertexToIndex());

        // Apply Truncated SVD with target rank k
        int k = 15;
        var lowApproximation = new SimpleSVDApproximation<>(representationAsMatrix, k);
        var embedding = lowApproximation.getEmbeddings();

        // Convert to HashMap and export
        var map = new HashMap<Integer, double[]>();
        var embeddingAsMatrix = new double[embedding.getNumRows()][embedding.getNumCols()];
        for (var i = 0; i < embedding.getNumRows(); i++) {
            for (var j = 0; j < embedding.getNumCols(); j++) {
                embeddingAsMatrix[i][j] = embedding.get(i, j);
            }
            map.put(i + 1, embeddingAsMatrix[i]);
        }

        new CSVNodeEmbeddingExporter<Integer>().saveNodeEmbeddings(
                Paths.get("TruncatedSVD_karate_Edges.csv"), map);
```

### Configuration

```text
Dataset: Karate graph
Graph type: Directed
Target rank (k): 15
```

---

## Example 2: Evaluating Embeddings with Tribuo

The project includes three downstream evaluation entry points under `src/main/java/MainPackage/TestEmbeddingMain/`, all using the **Tribuo ML library**:

| Entry Point | Task | Algorithm | Evaluation |
|---|---|---|---|
| `EmbeddingNodeClassificationTest` | Single-label node classification | FM classifier (Hinge loss + AdaGrad) | 3-fold cross-validation |
| `EmbeddingMultiLabelClassificationTest` | Multi-label node classification | Linear SGD (Binary Cross-Entropy + AdaGrad) | 3-fold cross-validation |
| `EmbeddingNodeClusteringTest` | Node clustering | K-Means++ (L2 distance) | 70/30 train/test split |

Prepare a labeled embedding CSV with a `Class` column before running any evaluation. See the source files for full configuration details.

### Single-Label Classification Example

```java
    // Read the embedding dataset
    var dataPath = "MB2Vec_Karate_Edges.csv";
    var dataSource = new CSVLoader<>(new LabelFactory()).loadDataSource(Paths.get(dataPath), "Class");
    var data = new MutableDataset<>(dataSource);

    // Use FM classifier
    var fmTrainer = new FMClassificationTrainer(new Hinge(),
            new AdaGrad(0.01, 0.9),
            100,
            Trainer.DEFAULT_SEED,
            10,
            0.2D);

    // Use 3-fold cross-validation
    var crossValidation = new CrossValidation<>(fmTrainer,
            data,
            new LabelEvaluator(),
            3);

    // Collect metrics
    var sumAcc = 0D;
    var sumRecall = 0D;
    var sumF1 = 0D;
    var sumPrecision = 0D;
    var listAcc = new ArrayList<Double>();
    var sTrain = System.currentTimeMillis();
    for (var performance : crossValidation.evaluate()) {
        sumAcc += performance.getA().accuracy();
        listAcc.add(performance.getA().accuracy());
        sumRecall += performance.getA().macroAveragedRecall();
        sumF1 += performance.getA().macroAveragedF1();
        sumPrecision += performance.getA().macroAveragedPrecision();
    }
    var eTrain = System.currentTimeMillis();

    var avgAcc = sumAcc / crossValidation.getK();
    var avgRecall = sumRecall / crossValidation.getK();
    var avgF1 = sumF1 / crossValidation.getK();
    var avgPrecision = sumPrecision / crossValidation.getK();

    // Standard deviation across folds
    var std = Math.sqrt(listAcc.stream()
            .mapToDouble(value -> Math.pow(value - avgAcc, 2))
            .sum() / (listAcc.size() - 1));

    System.out.println("The Training_Testing duration time is : " + Util.formatDuration(sTrain, eTrain));
    System.out.println("The average accuracy is : " + avgAcc);
    System.out.println("The average Std-accuracy is : " + std);
    System.out.println("The average recall is : " + avgRecall);
    System.out.println("The average F1-Score is : " + avgF1);
    System.out.println("The average precision is : " + avgPrecision);
```

### Experimental Results

Previously recorded example using 256-dimensional random-walk node embeddings and a Factorization Machine classifier with 7-fold cross-validation (the output below predates the addition of SD reporting):

```text
The Training_Testing duration time is : (00:00:00:524)
The average accuracy is : 0.8510101010101009
The average Std-accuracy is : 0.10722663507859667
The average recall is : 0.8595238095238096
The average F1-Score is : 0.8508158508158506
The average precision is : 0.8555555555555556
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

## Graph Visualization

The framework includes a `GraphVisualizer` class powered by GraphStream for interactive graph rendering and image export:

```java
        // Visualize graph interactively
        var graphVisualizer = new GraphVisualizer<>("Karate_Edges",
                builder,
                true,   // directed edges
                true,   // show node IDs
                true);  // show edge weights
        graphVisualizer.displaySingleGraph();

        // Save graph as PNG image (HD720 resolution)
        graphVisualizer.saveGraphToImage(Paths.get("karate_graph.png"));
```

**Note:** Visualization is limited to graphs with fewer than 5,000 vertices and 10,000 edges.

---

## Research Directions

Potential future directions include:

* LINE (Large-scale Information Network Embedding) — placeholder class exists
* Adversarial graph embedding learning
* FGSM-style embedding perturbations
* Dynamic and temporal graph embeddings
* Meta-learning for graph representation selection
* Robustness analysis under graph perturbations
* Embedding regularization using graph neighborhood consistency
* Markov Blanket–aware embedding objectives
* Adam optimizer implementation

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
