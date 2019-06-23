import numpy as np
import pandas as pd
import matplotlib 
import matplotlib.pyplot as plt 
import matplotlib.patches as mpatches
from sklearn.cluster import KMeans
from sklearn.metrics import classification_report

dataset = pd.read_csv('dataset_normalized.csv')

### Eixo X ###

X = dataset[['rmsx' , 'accx']].iloc[: , :].values

algorithm = (KMeans(n_clusters = 2,init='k-means++', n_init = 10 ,max_iter=300, tol=0.0001, random_state= 1, algorithm='elkan'))
preds = algorithm.fit(X)
x_pred_status = algorithm.labels_
x_centroids = algorithm.cluster_centers_

h = 0.001
x_min, x_max = X[:, 0].min(), X[:, 0].max()
y_min, y_max = X[:, 1].min(), X[:, 1].max()
xx, yy = np.meshgrid(np.arange(x_min, x_max, h), np.arange(y_min, y_max, h))
P = algorithm.predict(np.c_[xx.ravel(), yy.ravel()]) 
P = P.reshape(xx.shape)

fig, pos = plt.subplots(3, 1, sharex=True, sharey=True, figsize=(10, 10))

pos[0].imshow(P, interpolation='nearest', 
           extent=(xx.min(), xx.max(), yy.min(), yy.max()),
           cmap = matplotlib.colors.ListedColormap([(0, 0, 1), (1, 0, 0)]), alpha=0.1, aspect = 'auto', origin='lower')

pos[0].scatter(x = 'rmsx', y = 'accx', data = dataset, c = x_pred_status, cmap = matplotlib.colors.ListedColormap([(0, 0, 1), (1, 0, 0)]))
pos[0].scatter(x = x_centroids[: , 0], y =  x_centroids[: , 1], s = 100, c = 'black', alpha = 1)

### Eixo Y ###

Y = dataset[['rmsy' , 'accy']].iloc[: , :].values
status = dataset['status']

algorithm.fit(Y)
y_pred_status = algorithm.labels_
y_centroids = algorithm.cluster_centers_

h = 0.001
x_min, x_max = Y[:, 0].min(), Y[:, 0].max()
y_min, y_max = Y[:, 1].min(), Y[:, 1].max()
xx, yy = np.meshgrid(np.arange(x_min, x_max, h), np.arange(y_min, y_max, h))
P = algorithm.predict(np.c_[xx.ravel(), yy.ravel()]) 
P = P.reshape(xx.shape)

pos[1].imshow(P, interpolation='nearest', 
           extent=(xx.min(), xx.max(), yy.min(), yy.max()),
           cmap = matplotlib.colors.ListedColormap([(0, 0, 1), (1, 0, 0)]), alpha=0.1, aspect = 'auto', origin='lower')

pos[1].scatter(x = 'rmsy', y = 'accy', data = dataset, c = y_pred_status, cmap = matplotlib.colors.ListedColormap([(0, 0, 1), (1, 0, 0)]))
pos[1].scatter(x = y_centroids[: , 0], y =  y_centroids[: , 1], s = 100, c = 'black', alpha = 1)

### Eixo Z ###

Z = dataset[['rmsz' , 'accz']].iloc[: , :].values

algorithm.fit(Z)
z_pred_status = algorithm.labels_
z_centroids = algorithm.cluster_centers_

h = 0.001
x_min, x_max = Z[:, 0].min(), Z[:, 0].max()
y_min, y_max = Z[:, 1].min(), Z[:, 1].max()
xx, yy = np.meshgrid(np.arange(x_min, x_max, h), np.arange(y_min, y_max, h))
P = algorithm.predict(np.c_[xx.ravel(), yy.ravel()]) 
P = P.reshape(xx.shape)

pos[2].imshow(P, interpolation='nearest', 
           extent=(xx.min(), xx.max(), yy.min(), yy.max()),
           cmap = matplotlib.colors.ListedColormap([(0, 0, 1), (1, 0, 0)]), alpha=0.1, aspect = 'auto', origin='lower')

pos[2].scatter(x = 'rmsz', y = 'accz', data = dataset, c = z_pred_status, cmap = matplotlib.colors.ListedColormap([(0, 0, 1), (1, 0, 0)]))
pos[2].scatter(x = z_centroids[: , 0], y =  z_centroids[: , 1], s = 100, c = 'black', alpha = 1)

pos[0].set_ylabel('Eixo X')
pos[1].set_ylabel('Eixo Y')
pos[2].set_ylabel('Eixo Z')

blue_legend = mpatches.Patch(color='blue', label='Primeiro cluster')
red_legend = mpatches.Patch(color='red', label='Segundo cluster')
black_legend = mpatches.Patch(color='black', label='Centroides')

fig.legend(handles=[blue_legend, red_legend, black_legend], loc='lower right')
fig.text(0.5, 0.04, 'RMS (g)', ha='center')
fig.text(0.025, 0.5, 'Pico a Pico (g)', va='center', rotation='vertical')

plt.savefig('kmeans.png', bbox_inches='tight', dpi=300)

### Verificação dos resultados ###

print("Eixo X:")
print(classification_report(status, x_pred_status))
print("Eixo Y:")
print(classification_report(status, y_pred_status))
print("Eixo Z:")
print(classification_report(status, z_pred_status))