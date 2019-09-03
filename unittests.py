from my_answers import NeuralNetwork
import unittest
import numpy as np
import pandas as pd
import sys

#expand pandas to see all rows and columns
pd.set_option('display.max_rows', 500)
pd.set_option('display.max_columns', 500)
pd.set_option('display.width', 1000)

# =============================================================================
#Pre-processing the data and split to train test validation

#read the data
data_path = 'C:/Users/WAIFU/Desktop/school/deep-learning-v2-pytorch-master/project-bikesharing/Bike-Sharing-Dataset/hour.csv'
rides = pd.read_csv(data_path)

#plot the data 
#first 10 days with 24 entries
#ridedata[:10*24].plot(x = 'dteday', y = ['cnt','registered', 'casual'])

#Create dummy variables
dummy_features = ['season', 'weathersit', 'mnth', 'hr', 'weekday']
for var in dummy_features:
    dummies = pd.get_dummies(rides[var], prefix = var, drop_first = False)
    ridedata = pd.concat([rides, dummies], axis = 1)

drop_features = ['instant', 'dteday', 'season', 'weathersit','weekday','atemp','mnth','workingday','hr']
newdata = ridedata.drop(drop_features, axis = 1)

#scale target vars (follow a standard normal dist)
quant_features = ['casual','registered','cnt','temp','hum','windspeed']
scaled_features = {}
for var in quant_features:
    mean, std = newdata[var].mean(), newdata[var].std()
    scaled_features[var] = [mean,std]
    newdata.loc[:,var] = (newdata[var] - mean)/std
    
    
#Construct train test validation split
    
#Test split
test_data = newdata[-21*24:]
newdata = newdata[:-21*24]

target_features = ['cnt', 'casual', 'registered']
features, targets = newdata.drop(target_features, axis = 1), newdata[target_features]
test_features, test_targets = test_data.drop(target_features, axis = 1), test_data[target_features]

#Train and Validation
train_features, train_targets = features[:-60*24], targets[:-60*24]
val_features, val_targets = features[-60*24:], targets[-60*24:]




def MSE(y, Y):
    return np.mean((y-Y)**2)


inputs = np.array([[0.5, -0.2, 0.1]])
targets = np.array([[0.4]])
test_w_i_h = np.array([[0.1, -0.2],
                       [0.4, 0.5],
                       [-0.3, 0.2]])
test_w_h_o = np.array([[0.3],
                       [-0.1]])

class TestMethods(unittest.TestCase):
    
    ##########
    # Unit tests for data loading
    ##########
    
    def test_data_path(self):
#       Test that file path to dataset has been unaltered
        self.assertTrue(data_path == 'C:/Users/WAIFU/Desktop/school/deep-learning-v2-pytorch-master/project-bikesharing/Bike-Sharing-Dataset/hour.csv')
        
    def test_data_loaded(self):
        # Test that data frame loaded
        self.assertTrue(isinstance(rides, pd.DataFrame))
    
    ##########
    # Unit tests for network functionality
    ##########

    def test_activation(self):
        network = NeuralNetwork(3, 2, 1, 0.5)
        # Test that the activation function is a sigmoid
        self.assertTrue(np.all(network.activation_function(0.5) == 1/(1+np.exp(-0.5))))

    def test_train(self):
        # Test that weights are updated correctly on training
        network = NeuralNetwork(3, 2, 1, 0.5)
        network.weights_input_to_hidden = test_w_i_h.copy()
        network.weights_hidden_to_output = test_w_h_o.copy()
        
        network.train(inputs, targets)
        self.assertTrue(np.allclose(network.weights_hidden_to_output, 
                                    np.array([[ 0.37275328], 
                                              [-0.03172939]])))
        self.assertTrue(np.allclose(network.weights_input_to_hidden,
                                    np.array([[ 0.10562014, -0.20185996], 
                                              [0.39775194, 0.50074398], 
                                              [-0.29887597, 0.19962801]])))

    def test_run(self):
        # Test correctness of run method
        network = NeuralNetwork(3, 2, 1, 0.5)
        network.weights_input_to_hidden = test_w_i_h.copy()
        network.weights_hidden_to_output = test_w_h_o.copy()

        self.assertTrue(np.allclose(network.run(inputs), 0.09998924))

suite = unittest.TestLoader().loadTestsFromModule(TestMethods())
unittest.TextTestRunner().run(suite)


N_i = train_features.shape[1]
network = NeuralNetwork(N_i, hidden_nodes, output_nodes, learning_rate)

#find accuracy
losses = {'train':[], 'validation':[]}
for ii in range(iterations):
    # Go through a random batch of 128 records from the training data set
    batch = np.random.choice(train_features.index, size=128)
    X, y = train_features.ix[batch].values, train_targets.ix[batch]['cnt']
                             
    network.train(X, y)
    
    # Printing out the training progress
    train_loss = MSE(network.run(train_features).T, train_targets['cnt'].values)
    val_loss = MSE(network.run(val_features).T, val_targets['cnt'].values)
    sys.stdout.write("\rProgress: {:2.1f}".format(100 * ii/float(iterations)) \
                     + "% ... Training loss: " + str(train_loss)[:5] \
                     + " ... Validation loss: " + str(val_loss)[:5])
    sys.stdout.flush()
    
    losses['train'].append(train_loss)
    losses['validation'].append(val_loss)
    
    
    
#plot train and validation loss
plt.plot(losses['train'], label='Training loss')
plt.plot(losses['validation'], label='Validation loss')
plt.legend()
_ = plt.ylim()



#plot predictions
fig, ax = plt.subplots(figsize=(8,4))

mean, std = scaled_features['cnt']
predictions = network.run(test_features).T*std + mean
ax.plot(predictions[0], label='Prediction')
ax.plot((test_targets['cnt']*std + mean).values, label='Data')
ax.set_xlim(right=len(predictions))
ax.legend()

dates = pd.to_datetime(rides.ix[test_data.index]['dteday'])
dates = dates.apply(lambda d: d.strftime('%b %d'))
ax.set_xticks(np.arange(len(dates))[12::24])
_ = ax.set_xticklabels(dates[12::24], rotation=45)
