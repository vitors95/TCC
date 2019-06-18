import pandas as pd
import numpy  as np
import matplotlib.pyplot as plt

sample = np.arange(0, 300) + 1

### PRIMEIRO CONJUNTO DE DADOS ###

normal = pd.read_csv('dataset_normal.csv')

normal_accx = normal['accx'].values 
normal_accy = normal['accy'].values 
normal_accz = normal['accz'].values 
normal_rmsx = normal['rmsx'].values 
normal_rmsy = normal['rmsy'].values 
normal_rmsz = normal['rmsz'].values 
normal_temp = normal['temp'].values

fig, pos = plt.subplots(3, 2, sharex=True, sharey=True, figsize=(10, 10))

pos[0][0].plot(sample, normal_accx[0:300]/8192, color="blue", label="Eixo X")
pos[1][0].plot(sample, normal_accy[0:300]/8192, color="red", label="Eixo Y")
pos[2][0].plot(sample, normal_accz[0:300]/8192, color="green", label="Eixo Z")
pos[0][0].set_title('Pico a Pico')
pos[0][0].set_ylabel('Eixo X')
pos[1][0].set_ylabel('Eixo Y')
pos[2][0].set_ylabel('Eixo Z')

pos[0][1].plot(sample, normal_rmsx[0:300]/8192, color="blue")
pos[1][1].plot(sample, normal_rmsy[0:300]/8192, color="red")
pos[2][1].plot(sample, normal_rmsz[0:300]/8192, color="green")
pos[0][1].set_title('RMS')

#fig.legend(loc='lower right')
fig.text(0.5, 0.04, 'Tempo (min)', ha='center')
fig.text(0.025, 0.5, 'Aceleração (g)', va='center', rotation='vertical')
plt.savefig('aceleracao_funcionamento_normal.png', dpi=300)

### SEGUNDO CONJUNTO DE DADOS ###

anormal = pd.read_csv('dataset_anormal.csv')

anormal_accx = anormal['accx'].values 
anormal_accy = anormal['accy'].values 
anormal_accz = anormal['accz'].values 
anormal_rmsx = anormal['rmsx'].values 
anormal_rmsy = anormal['rmsy'].values 
anormal_rmsz = anormal['rmsz'].values 
anormal_temp = anormal['temp'].values

fig, pos = plt.subplots(3, 2, sharex=True, sharey=True, figsize=(10, 10))

pos[0][0].plot(sample, anormal_accx[0:300]/8192, color="blue", label="Eixo X")
pos[1][0].plot(sample, anormal_accy[0:300]/8192, color="red", label="Eixo Y")
pos[2][0].plot(sample, anormal_accz[0:300]/8192, color="green", label="Eixo Z")
pos[0][0].set_title('Pico a Pico')
pos[0][0].set_ylabel('Eixo X')
pos[1][0].set_ylabel('Eixo Y')
pos[2][0].set_ylabel('Eixo Z')

pos[0][1].plot(sample, anormal_rmsx[0:300]/8192, color="blue")
pos[1][1].plot(sample, anormal_rmsy[0:300]/8192, color="red")
pos[2][1].plot(sample, anormal_rmsz[0:300]/8192, color="green")
pos[0][1].set_title('RMS')
pos[0][1].set_yticks([0.5, 1, 1.5, 2.0, 2.5, 3.0, 3.5, 4])

#fig.legend(loc='lower right')
fig.text(0.5, 0.04, 'Tempo (min)', ha='center')
fig.text(0.025, 0.5, 'Aceleração (g)', va='center', rotation='vertical')
plt.savefig('aceleracao_funcionamento_anormal.png', dpi=300)

### TERCEIRO CONJUNTO DE DADOS ###

fig, pos = plt.subplots(3, 1, sharex=True, sharey=True, figsize=(10, 10))

pos[0].scatter(normal_rmsx/8192, normal_accx/8192, marker='o', color='blue', label="Funcionamento normal")
pos[1].scatter(normal_rmsy/8192, normal_accy/8192, marker='o', color='blue')
pos[2].scatter(normal_rmsz/8192, normal_accz/8192, marker='o', color='blue')
pos[0].scatter(anormal_rmsx/8192, anormal_accx/8192, marker='x', color='red', label="Funcionamento anormal")
pos[1].scatter(anormal_rmsy/8192, anormal_accy/8192, marker='x', color='red')
pos[2].scatter(anormal_rmsz/8192, anormal_accz/8192, marker='x', color='red')

pos[0].set_ylabel('Eixo X')
pos[1].set_ylabel('Eixo Y')
pos[2].set_ylabel('Eixo Z')

fig.legend(loc='lower right')
fig.text(0.5, 0.04, 'RMS (g)', ha='center')
fig.text(0.025, 0.5, 'Pico a Pico (g)', va='center', rotation='vertical')
plt.savefig('dispersao.png', dpi=300)

### TEMPERATURA ###

fig, pos = plt.subplots(2, 1, sharex=True, sharey=True, figsize=(10, 10))

pos[0].plot(sample, (normal_temp[0:300]/340)+36.53, color="blue")
pos[1].plot(sample, (anormal_temp[0:300]/340)+36.53, color="red")

pos[0].set_ylabel('Funcionamento normal')
pos[1].set_ylabel('Funcionamento anormal')

fig.text(0.5, 0.04, 'Tempo (min)', ha='center')
fig.text(0.025, 0.5, 'Temperatura (°C)', va='center', rotation='vertical')
plt.savefig('temperatura.png', dpi=300)

### EIXO X (Anormal e normal) ###

fig, pos = plt.subplots(2, 1, sharex=True, sharey=True, figsize=(10, 10))

pos[0].plot(sample, normal_accx[0:300]/8192, color="blue", marker="o")
pos[0].plot(sample, anormal_accx[0:300]/8192, color="red", marker="x")
pos[0].set_ylabel('Pico a Pico (g)')

pos[1].plot(sample, normal_rmsx[0:300]/8192, color="blue", marker="o")
pos[1].plot(sample, anormal_rmsx[0:300]/8192, color="red", marker="x")
pos[1].set_ylabel('RMS (g)')

fig.text(0.5, 0.04, 'Tempo (min)', ha='center')
fig.text(0.025, 0.5, 'Temperatura (°C)', va='center', rotation='vertical')
plt.savefig('eixoX.png', dpi=300)

### EIXO Y (Anormal e normal) ###

fig, pos = plt.subplots(2, 1, sharex=True, sharey=True, figsize=(10, 10))

pos[0].plot(sample, normal_accy[0:300]/8192, color="blue", marker="o")
pos[0].plot(sample, anormal_accy[0:300]/8192, color="red", marker="x")
pos[0].set_ylabel('Pico a Pico (g)')

pos[1].plot(sample, normal_rmsy[0:300]/8192, color="blue", marker="o")
pos[1].plot(sample, anormal_rmsy[0:300]/8192, color="red", marker="x")
pos[1].set_ylabel('RMS (g)')

fig.text(0.5, 0.04, 'Tempo (min)', ha='center')
fig.text(0.025, 0.5, 'Temperatura (°C)', va='center', rotation='vertical')
plt.savefig('eixoY.png', dpi=300)

### EIXO Z (Anormal e normal) ###

fig, pos = plt.subplots(2, 1, sharex=True, sharey=True, figsize=(10, 10))

pos[0].plot(sample, normal_accz[0:300]/8192, color="blue", marker="o")
pos[0].plot(sample, anormal_accz[0:300]/8192, color="red", marker="x")
pos[0].set_ylabel('Pico a Pico (g)')

pos[1].plot(sample, normal_rmsz[0:300]/8192, color="blue", marker="o")
pos[1].plot(sample, anormal_rmsz[0:300]/8192, color="red", marker="x")
pos[1].set_ylabel('RMS (g)')

fig.text(0.5, 0.04, 'Tempo (min)', ha='center')
fig.text(0.025, 0.5, 'Temperatura (°C)', va='center', rotation='vertical')
plt.savefig('eixoZ.png', dpi=300)
