import numpy as np
import pandas as pd
import matplotlib.pyplot as plt



# =============================================================================
# Construct Neural Network
class NeuralNetwork:
    def __init__(self, input_nodes, hidden_nodes, output_nodes, learning_rate):
        #sets the number of input, hidden, and output layers
        self.input_nodes = input_nodes
        self.hidden_nodes = hidden_nodes
        self.output_nodes = output_nodes
        
        #Initialize weights
        self.weights_input_to_hidden = np.random.normal(0.0, self.input_nodes**-0.5,
                                                        (self.input_nodes, self.hidden_nodes))
        self.weights_hidden_to_output = np.random.normal(0.0, self.hidden_nodes**-0.5,
                                                         (self.hidden_nodes, self.output_nodes))
        self.lr = learning_rate
        
        #Creation of sigmoid function
        #self.activation_function = lambda x: (1/(1+np.exp(-x)))
        def sigmoid(x):
            return 1/(1+np.exp(-x))
        self.activation_function = sigmoid
   
    def train(self, features, targets):
        #Train network on batch of features and targets
        #Features are 2d arrays, each row is an observatrion, with columns a feature
        #Targets are 1d arrays of target values
        n_records = features.shape[0]
        delta_weights_i_h = np.zeros(self.weights_input_to_hidden.shape)
        delta_weights_h_o = np.zeros(self.weights_hidden_to_output.shape)
        
        for X,y in zip(features, targets):
            
            final_outputs, hidden_outputs = self.forward_pass_train(X)
            delta_weights_i_h, delta_weights_h_o = self.backpropagation(final_outputs, hidden_outputs, X, y,
                                                                        delta_weights_i_h, delta_weights_h_o)
        self.update_weights(delta_weights_i_h, delta_weights_h_o, n_records)
      
    def forward_pass_train(self, X):
        #Args: X is the features batch
        #Calculate the dot product between X and the weights from the input for all hidden Edges
        hidden_inputs = np.dot(X, self.weights_input_to_hidden)
        hidden_outputs = self.activation_function(hidden_inputs)
        
        #Calculate the output layer
        final_inputs = np.dot(hidden_outputs, self.weights_hidden_to_output)
        final_outputs = final_inputs
        return final_outputs, hidden_outputs
        
    def backpropagation(self, final_outputs, hidden_outputs, X, y, delta_weights_i_h, delta_weights_h_o):
        error = y - final_outputs
        output_error_term = error 
        hidden_error = np.dot(self.weights_hidden_to_output, output_error_term)
        hidden_error_term = hidden_error * hidden_outputs *  (1 - hidden_outputs)
        delta_weights_i_h += hidden_error_term * X[:, None]
        delta_weights_h_o += output_error_term * hidden_outputs[:,None]
        return delta_weights_i_h, delta_weights_h_o
        
    def update_weights(self, delta_weights_i_h, delta_weights_h_o, n_records):
        self.weights_hidden_to_output += self.lr * delta_weights_h_o/n_records
        self.weights_input_to_hidden += self.lr * delta_weights_i_h/n_records
        
    def run(self, features):
        hidden_inputs = np.dot(features, self.weights_input_to_hidden)
        hidden_outputs = self.activation_function(hidden_inputs)
        
        final_inputs = np.dot(hidden_outputs, self.weights_hidden_to_output)
        final_outputs = final_inputs
        return final_outputs
    
# =============================================================================
iterations = 150
learning_rate = 0.06
hidden_nodes = 12
output_nodes = 30
