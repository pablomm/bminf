

import matplotlib.pyplot as plt
import numpy as np
from scipy.optimize import curve_fit

def func_powerlaw(x, K, b):
    return (x**b) * K


file = "./heap10k.txt"
title = "docs10k"

if __name__ == "__main__":
    data = np.loadtxt(file)


    x = data[:, 0]
    y = data[:, 1]

    sol = curve_fit(func_powerlaw, x, y, maxfev=2000)[0]


    t = np.linspace(0, np.max(x), 1000)


    plt.plot(x, y)
    label = "$" + str(round(sol[0],2)) +"x^{" + str(round(sol[1],2)) + "}$"
    plt.plot(t, func_powerlaw(t, sol[0], sol[1]), c="red", linestyle="--", label=label)
    plt.title(title)
    plt.xlabel("#Tokens")
    plt.ylabel("#Palabras")
    plt.legend()
    plt.show()
