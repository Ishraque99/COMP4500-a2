import matplotlib.pyplot as plt
import csv

x1 = [] # problem size
r1 = [] # recursion time
d1 = [] # dynamic time

with open('out2.txt','r') as csvfile:
    lines = csv.reader(csvfile, delimiter=',')
    for row in lines:
        x1.append(int(row[0]))
        r1.append(int(row[1]) * int(row[1]))
        d1.append(int(row[2]) / 100)
        
plt.plot(x1, r1, label='Recursion')
plt.plot(x1, d1, label='Dynamic')

plt.xlabel('prob_size')
plt.ylabel('runtime')
plt.legend()
plt.show()