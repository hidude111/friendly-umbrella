import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
#expand pandas to see all rows and columns
pd.set_option('display.max_rows', 500)
pd.set_option('display.max_columns', 500)
pd.set_option('display.width', 1000)

# =============================================================================
#Pre-processing the data and split to train test validation

#read the data
ridedata = pd.read_csv("C:/Users/WAIFU/Desktop/school/deep-learning-v2-pytorch-master/project-bikesharing/Bike-Sharing-Dataset/hour.csv")
ridedata.head()

#plot the data 
#first 10 days with 24 entries
ridedata[:10*24].plot(x = 'dteday', y = ['cnt','registered', 'casual'])

#Create dummy variables
dummy_features = ['season', 'weathersit', 'mnth', 'hr', 'weekday']
for var in dummy_features:
    dummies = pd.get_dummies(ridedata[var], prefix = var, drop_first = False)
    ridedata = pd.concat([ridedata, dummies], axis = 1)

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