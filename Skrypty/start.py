import subprocess
python_script_command = "python createNewData.py"
pytonhscript = subprocess.Popen(python_script_command, shell=True)
pytonhscript.wait()
compile_command = "javac ..\\TinyGP-Java-master\\tiny_gp.java"
subprocess.run(compile_command, shell=True)
java_command = "java -cp ..\\TinyGP-Java-master tiny_gp"
java_process = subprocess.Popen(java_command, shell=True)
java_process.wait()
python_script_command2 = "python createExcelFile.py"
pytonhscript = subprocess.Popen(python_script_command2, shell=True)
pytonhscript.wait()
print("Done")