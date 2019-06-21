import numpy as np # linear algebra
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv)
import matplotlib.pyplot as plt 
import seaborn as sns 
from sklearn.cluster import KMeans
import warnings
import os
warnings.filterwarnings("ignore")

dataset = pd.read_csv('dataset_all_data.csv')

dataset['accx'] = dataset['accx'].values/8192
dataset['accy'] = dataset['accy'].values/8192
dataset['accz'] = dataset['accz'].values/8192
dataset['rmsx'] = dataset['rmsx'].values/8192
dataset['rmsy'] = dataset['rmsy'].values/8192
dataset['rmsz'] = dataset['rmsz'].values/8192
dataset['temp'] = (dataset['temp'].values/340)+36.53

dataset.to_csv('dataset_normalized.csv')
