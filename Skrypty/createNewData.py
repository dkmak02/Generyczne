import sympy
import numpy as np

problem_file = r'..\TinyGP-Java-master\problem.dat'

def readData(fileName, to_return = 0):
    fun_info = []
    with open(fileName, 'r') as file:
        for line in file:
            fun_info.append((line.strip()).replace('^', '**').replace(',', '.'))
    if to_return != 0:
        return fun_info
    else:
        createDate(fun_info[0], fun_info[1], fun_info[2])


def createDate(data, zakres, step):
    x = sympy.symbols('x')
    y = sympy.symbols('y')
    zakres = zakres.split(' ')
    step = float(step)
    zakres[0] = float(zakres[0])
    zakres[1] = float(zakres[1])
    numberOfPoints = int((zakres[1]/step - zakres[0]/step)) + 1
    points = np.linspace(zakres[0], zakres[1], numberOfPoints)
    with open(problem_file, 'w') as file:
        if 'y' in data:
            file.write(f"2 100 -5 5 {numberOfPoints**2}" + '\n')
            for i in points:
                for j in points:
                    result = sympy.sympify(data).subs(x, i).subs(y, j)
                    file.write(f"{round(i,2)}   {round(j,2)}   {round(result,3)}\n")
        else:
            file.write(f"1 100 -5 5 {numberOfPoints}" + '\n')
            for i in points:
                result = sympy.sympify(data).subs(x, i)
                file.write(f"{round(i,2)}   {round(result,3)}\n")


if __name__ == '__main__':
    fileName = 'data.txt'
    readData(fileName)
