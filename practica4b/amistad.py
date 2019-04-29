# libraries
import matplotlib.pyplot as plt
import numpy as np
from scipy.stats import kde

file = "friends-stats.txt"

# create data
a = np.loadtxt(file)
x = a[:,0]
y = a[:,1]
maximo = np.max(a)
minimo = np.min(a)
# Evaluate a gaussian kde on a regular grid of nbins x nbins over data extents
nbins=300
k = kde.gaussian_kde([x,y])
xi, yi = np.mgrid[x.min():x.max():nbins*1j, y.min():y.max():nbins*1j]
zi = k(np.vstack([xi.flatten(), yi.flatten()]))

# Make the plot
plt.pcolormesh(xi, yi, zi.reshape(xi.shape))
plt.title("Densidad de X~(grado, grado medio de vecinos)")
plt.xlabel("Grado")
plt.ylabel("Grado medio de vecinos")

plt.style.use("seaborn")

plt.figure()
plt.hist(a[:,0])
plt.title("Histograma de grado")
plt.xlabel("Grado")
plt.ylabel("Frecuencia")

plt.figure()
plt.plot(np.sort(a[:,0]), np.arange(len(a)))
plt.title("Nodos ordenados por grado")
plt.xlabel("Grado")
plt.ylabel("Número de nodo")


print((a[:,0]>a[:,1]).sum(), "de", len(a),"tiene mas amigos que la "
      "media de sus amigos")

plt.show()
