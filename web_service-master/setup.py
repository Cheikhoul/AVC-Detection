# File that installs automatically the dependancies needed to launch the project
# The setup file is configured to work with Linux based systems. For windows operating systems use IDE
# or other alternative method to install the libraries listed in requirements.txt

import subprocess
process = subprocess.Popen(['pip3 install -r', 'requirements.txt'],
                           stdout=subprocess.PIPE,
                           stderr=subprocess.PIPE)
stdout, stderr = process.communicate()
print(stdout, stderr)
