import sympy


def readData(fileName):
    with open(fileName, 'r') as file:
        for line in file:
            createDate((line.strip()).replace('^', '**'))


def createDate(data):
    x = sympy.symbols('x')
    y = sympy.symbols('y')
    with open(r'C:\Users\dkmak\Desktop\Genetyczne\TinyGP-Java-master\problem.dat', 'w') as file:
        file.write('1 100 -5 5 2000' + '\n')
        for i in range(-1000, 1000):
            x_value = i / 10.0
            result = sympy.sympify(data).subs(x, x_value).subs(y, x_value)
            file.write(str(x_value) + '   ' + str(result) + '\n')


if __name__ == '__main__':
    fileName = 'data.txt'
    readData(fileName)
