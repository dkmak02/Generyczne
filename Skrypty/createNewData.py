import sympy


def readData(fileName):
    with open(fileName, 'r') as file:
        for line in file:
            createDate((line.strip()).replace('^', '**'))


def createDate(data):
    x = sympy.symbols('x')
    y = sympy.symbols('y')
    with open(r'C:\Users\dkmak\OneDrive\Pulpit\Generyczne\TinyGP-Java-master\problem.dat', 'w') as file:
        if 'y' in data:
            file.write('2 100 -5 5 10000' + '\n')
            for i in range(0, 100):
                for j in range(0,100):
                #[-10, 10], [0,100], [-1, 1], [-1000, 1000]
                    x_value = i
                    result = sympy.sympify(data).subs(x, x_value).subs(y, j)
                    file.write(str(x_value) + '   '+ str(j)+'   ' + str(result) + '\n')
        else:
            file.write('1 100 -5 5 1000' + '\n')
            for i in range(-314, 314):
                x_value = i / 100
                result = sympy.sympify(data).subs(x, x_value)
                file.write(str(x_value) + '   ' + str(result) + '\n')


if __name__ == '__main__':
    fileName = 'data.txt'
    readData(fileName)
