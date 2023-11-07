import re

import matplotlib
matplotlib.use('TkAgg')  # Use the Agg backend
import matplotlib.pyplot as plt
import numpy as np
import sympy
import createNewData as cnd

bestPath = 'best.txt'
probPath = r'..\TinyGP-Java-master\problem.dat'
generationDataFile = 'generation_data.txt'
def newDataNoY(x_values,new_data):
    x = sympy.symbols('X1')
    with open(bestPath, 'r') as file:
        best = file.read()
        best = best.replace(',','.')
        for i in x_values:
            new_data.append(sympy.sympify(best).subs(x, i))
    return new_data
def symplify():
    best = ''
    with open(bestPath, 'r') as file:
        best = file.read()
        best = best.replace(',', '.')
        best = sympy.sympify(best)
    with open(bestPath, 'w') as file:
        file.write(str(best))
def create2DPlot(x, value, new_value, name, label1= "Orginal", label2= "TinyGp"):
    x_data = [float(x_f) for x_f in x]
    y_data = [float(y_f) for y_f in value]
    new_y_data = [float(n_y_f) for n_y_f in new_value]
    plt.figure()
    plt.plot(x_data, y_data , color='b', label=label1)
    plt.plot(x_data, new_y_data, color='r', label=label2)
    plt.xlabel('X-axis Label')
    plt.ylabel('Y-axis Label')
    plt.title('Line Plot')
    plt.legend()
    plt.savefig(name)
def create3DPlot(x_d,y_d,fun, tinyGP,name):
    x = sympy.symbols('x')
    y = sympy.symbols('y')
    x_data = [float(x_f) for x_f in x_d]
    y_data = [float(y_f) for y_f in y_d]
    x_data = np.array(sorted(set(x_data)))
    y_data = np.array(sorted(set(y_data)))
    X, Y = np.meshgrid(x_data, y_data)
    fun = sympy.sympify(fun)
    Z = np.array([[fun.subs({x: xi, y: yi}) for xi, yi in zip(x_row, y_row)] for x_row, y_row in zip(X, Y)])
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    ax.plot_surface(X, Y, Z, color='b', label='Orginal')
    fun = sympy.sympify(tinyGP)
    x = sympy.symbols('X1')
    y = sympy.symbols('X2')
    Z = np.array([[fun.subs({x: xi, y: yi}) for xi, yi in zip(x_row, y_row)] for x_row, y_row in zip(X, Y)])
    ax.plot_surface(X, Y, Z, color='r', label='TinyGp')
    plt.savefig(name)
def newDataY():
    return None
def orginal_data():
    data = []
    with open(probPath, 'r') as file:
        lines = file.readlines()
        for line in lines[1:]:
            data.append(line.strip().split(' '))
    constains_y = len(data[0])>4
    samples = (lines[0].split(' '))[-1]
    if constains_y:
        samples = np.sqrt(int(samples))
    return data, constains_y, int(samples)

symplify()
orginal, has_y, samples = orginal_data()
x = [sublist[0] for sublist in orginal]
y = []
new_data = []
fun_info = cnd.readData('data.txt',1)
fun_info[0] = re.sub(r'[^\w\s-]', '', fun_info[0])
fun_info[0] = re.sub(r'\s+', ' ', fun_info[0])
nameTosave = f"{fun_info[0]} {fun_info[1]}.png"
if has_y:
    y = [sublist[3] for sublist in orginal]
    fun = []
    with open("data.txt", 'r') as line:
        fun.append(line.readline().replace('^', '**'))
    with open(bestPath, 'r') as file:
        fun.append(file.readline())
    create3DPlot(x, y, fun[0], fun[1], nameTosave)
else:
    new_data = newDataNoY(x, new_data)
    orginal = [sublist[-1] for sublist in orginal]
    create2DPlot(x, orginal, new_data, nameTosave)
with open(generationDataFile, 'r') as file:
    nameTosave = f"{fun_info[0]} {fun_info[1]}_generation.png"
    x = []
    value = []
    new_value = []
    data = file.readlines()
    data = data[2:]
    for d in data:
        d = d.split(',')
        x.append(d[0])
        value.append(d[1])
        new_value.append(d[2].replace('\n',''))
    create2DPlot(x, value, new_value, nameTosave, "Avg", "Best")

