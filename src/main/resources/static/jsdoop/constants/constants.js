class JSDConstants {
  static JOB_HOST_LOCAL = "http://localhost"
  static JOB_PORT_LOCAL = 8081
  static JOB_HOST_REMOTE = "http://localhost" // TODO -> "http://yourremotehost.com"
  static JOB_PORT_REMOTE = 8081 // TODO -> your remote port number
  static DEBUG = false

  //STATS DATABASE
    static SQL_LOCAL_HOST = "http://localhost"
    static SQL_LOCAL_PORT = 8082
    static SQL_REMOTE_HOST = "http://localhost" // TODO -> "http://yourremotehost.com"
    static SQL_REMOTE_PORT = 8082 // TODO -> your remote port number


  //DATASETS
  static MNIST = "mnist"

  //TOPOLOGIES
  static MNIST_CONV_28_28_1 = "mnist_conv_28_28_1"

  static EJSDNNOptimizers = {
    adadelta : 'adadelta', // Adadelta algorithm. See https://arxiv.org/abs/1212.5701
    adagrad : 'adagrad', // Adagrad algorithm. See http://www.jmlr.org/papers/volume12/duchi11a/duchi11a.pdf or http://ruder.io/optimizing-gradient-descent/index.html#adagrad
    adam : 'adam', // Adam algorithm. See https://arxiv.org/abs/1412.6980
    adamax : 'adamax', // Adamax algorithm. See https://arxiv.org/abs/1412.6980
    momentum : 'momentum', // Momentum gradient descent.
    rmsprop : 'rmsprop', // RMSProp gradient descent. This implementation uses plain momentum and is not centered version of RMSProp.
    sgd : 'sgd', // Stochastic gradient descent.
  }


  static EJSDNNMetrics = {
    binaryAccuracy : 'binaryAccuracy ', // Binary accuracy metric function.
    binaryCrossentropy : 'binaryCrossentropy', // Binary crossentropy metric function. Binary crossentropy is a loss function used on problems involving yes/no (binary) decisions. For instance, in multi-label problems, where an example can belong to multiple classes at the same time, the model tries to decide for each class whether the example belongs to that class or not.
    categoricalAccuracy : 'categoricalAccuracy', // Categorical accuracy metric function.
    categoricalCrossentropy : 'categoricalCrossentropy', // Categorical crossentropy between an output tensor and a target tensor. Categorical crossentropy is a loss function that is used for single label categorization. This is when only one category is applicable for each data point. In other words, an example can belong to one class only.
    cosineProximity : 'cosineProximity', // Loss or metric function: Cosine proximity. Mathematically, cosine proximity is defined as: -sum(l2Normalize(yTrue) * l2Normalize(yPred)), wherein l2Normalize() normalizes the L2 norm of the input to 1 and * represents element-wise multiplication.
    meanAbsoluteError : 'meanAbsoluteError', // Loss or metric function: Mean absolute error. Mathematically, mean absolute error is defined as: mean(abs(yPred - yTrue)), wherein the mean is applied over feature dimensions.
    meanAbsolutePercentageError : 'meanAbsolutePercentageError', // Loss or metric function: Mean absolute percentage error.
    meanSquaredError : 'meanSquaredError', // Loss or metric function: Mean squared error.
    precision : 'precision', // Computes the precision of the predictions with respect to the labels.
    recall : 'recall', // Computes the recall of the predictions with respect to the labels.
    sparseCategoricalAccuracy : 'sparseCategoricalAccuracy ', // Sparse categorical accuracy metric function.
  }

  static EJSDNNLosses = {
    absoluteDifference : 'absoluteDifference ', // Computes the absolute difference loss between two tensors.
    computeWeightedLoss : 'computeWeightedLoss', // Computes the weighted loss between two tensors..
    cosineDistance : 'cosineDistance', // Computes the cosine distance loss between two tensors.
    hingeLoss : 'hingeLoss', // Computes the Hinge loss between two tensors.
    huberLoss : 'huberLoss', // Computes the huber loss between two tensors.
    logLoss : 'logLoss', // Computes the log loss between two tensors.
    meanSquaredError : 'meanSquaredError', // Computes the mean squared error between two tensors..
    sigmoidCrossEntropy : 'sigmoidCrossEntropy', // Computes the sigmoid cross entropy loss between two tensors. If labelSmoothing is nonzero, smooth the labels towards 1/2: newMulticlassLabels = multiclassLabels * (1 - labelSmoothing) + 0.5 * labelSmoothing
    softmaxCrossEntropy : 'softmaxCrossEntropy', // Computes the softmax cross entropy loss between two tensors. If labelSmoothing is nonzero, smooth the labels towards 1/2: newOnehotLabels = onehotLabels * (1 - labelSmoothing) + labelSmoothing / numClasses
  }

}




