

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from scipy.stats import linregress

# Configuracion de estilo de matplotlib
plt.style.use("seaborn")

# Paths con frecuencias
freq_file = "termfreq.txt"
doc_freq_file = "termdocfreq.txt"

def total_freqs_plot(freqs, title):

    plt.figure()
    plt.title(title)
    plt.loglog(freqs)
    plt.xlabel("Log(Posicion)")
    plt.ylabel("Log(Frecuencia)")
    plt.legend(["Frecuencia"])

def top_plot(words, freqs, title, top=20):

    plt.figure()
    plt.title(title)
    plt.plot(freqs[:top])
    plt.xticks(np.arange(top), words[:top], rotation=50)

    plt.xlabel("Palabra")
    plt.ylabel("Frecuencia")
    plt.legend(["Frecuencia"])


if __name__ == "__main__":

    data = pd.read_csv(freq_file, sep="\t")
    words = data.iloc[:, 0].values
    freqs = data.iloc[:, 1].values
    total_freqs_plot(freqs, "Frecuencias totales")
    top_plot(words, freqs, "Top de frecuencias totales")

    data = pd.read_csv(doc_freq_file, sep="\t")
    words = data.iloc[:, 0].values
    freqs = data.iloc[:, 1].values
    total_freqs_plot(freqs, "Frecuencias de documento")
    top_plot(words, freqs, "Top de frecuencias de documento")

    plt.show()
