import matplotlib.pyplot as plt
import csv

x1 = [] # problem size
r1 = [] # recursion time
d1 = [] # dynamic time
g = []

with open('out2.txt','r') as csvfile:
    lines = csv.reader(csvfile, delimiter=',')
    for row in lines:
        x1.append(int(row[0]))
        r1.append(int(row[1])**2)
        d1.append(int(row[2])/100)
        g.append(int(row[2]))

base = g[0] 
for idx, val in enumerate(x1):
    g[idx] = base*(x1[idx]**2)
plt.plot(x1, r1, label='N^2')
# plt.plot(x1, g, label="base n2")
plt.plot(x1, d1, label='Dynamic')

plt.xlabel('prob_size')
plt.ylabel('runtime')
plt.legend()
plt.show()