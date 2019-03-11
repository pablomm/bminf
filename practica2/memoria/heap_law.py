

import matplotlib.pyplot as plt
import numpy as np
from scipy.optimize import curve_fit

def func_powerlaw(x, K, b):
    return (x**b) * K



file = "./heapLaw10k.txt"
title = "docs10k"
n_outliers = 0


if __name__ == "__main__":
    data = np.loadtxt(file)

    # Quitamos puntos aislados que distorsionan la grafica
    for i in range(n_outliers):
        idx = np.argmax(data, axis=0)
        data[idx] = data[0]

    x = data[:, 1]
    y = data[:, 0]

    sol = curve_fit(func_powerlaw, x, y, maxfev=2000)[0]


    t = np.linspace(0, np.max(x), 1000)


    plt.scatter(x, y)
    label = "$" + str(round(sol[0],2)) +"x^{" + str(round(sol[1],2)) + "}$"
    plt.plot(t, func_powerlaw(t, sol[0], sol[1]), c="red", linestyle="--", label=label)
    plt.title(title)
    plt.ylabel("Longitud del documento (#palabras)")
    plt.xlabel("#Tokens")
    plt.legend()
    plt.show()
