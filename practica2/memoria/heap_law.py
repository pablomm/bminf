

import matplotlib.pyplot as plt
import numpy as np




file = "./heapLaw10k.txt"
n_outliers = 5


if __name__ == "__main__":
    data = np.loadtxt(file)

    # Quitamos puntos aislados que distorsionan la grafica
    for i in range(n_outliers):
        idx = np.argmax(data, axis=0)
        data[idx] = data[0]

    plt.scatter(data[:, 1], data[:, 0])

    plt.title("docs10k")
    plt.ylabel("Longitud del documento (#palabras)")
    plt.xlabel("#Tokens")
    plt.show()
