import openpyxl as openpyxl
import sympy
import os

def refactorData():
    with open(r'C:\Users\dkmak\Desktop\Genetyczne\TinyGP-Java-master\best.txt', 'r') as file:
        best = file.read()
        best = best.replace('.', ',')
        best = best.replace('X1', 'A1')
        with open(r'C:\Users\dkmak\Desktop\Genetyczne\TinyGP-Java-master\best.txt', 'w') as file:
            file.write(best)
def writeToExcel():
    workbook = openpyxl.load_workbook("Wykresy.xlsx")
    worksheet = workbook.active
    data = []
    with open(r'C:\Users\dkmak\Desktop\Genetyczne\TinyGP-Java-master\problem.dat', 'r') as file:
        lines = file.readlines()
        for line in lines[1:]:
            data.append(line.strip().split(' '))
    with open(r'C:\Users\dkmak\Desktop\Genetyczne\TinyGP-Java-master\best.txt', 'r') as file:
        best = file.read()
        for i, row in enumerate(data):
            worksheet.cell(row=i+1, column=1, value=row[0].replace('.', ','))
            value_as_number = float(row[3])
            worksheet.cell(row=i + 1, column=3, value=value_as_number)
            toSave = best.replace('A1', str(row[0]))
            worksheet.cell(row=i + 1, column=2, value=float(sympy.sympify(toSave.replace(',', '.'))))
    fileName = 'example.xlsx'
    i=0
    while os.path.exists(fileName):
        fileName = 'example' + str(i) + '.xlsx'
    workbook.save(fileName)
    workbook.close()
if __name__ == '__main__':
    refactorData()
    writeToExcel()