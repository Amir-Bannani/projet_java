$ErrorActionPreference = "Stop"

echo "Compiling..."
Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName } > sources.txt
javac -cp ".;..\lib\mysql-connector-j-9.6.0.jar" @sources.txt
Remove-Item sources.txt

echo "Running Application..."
java -cp ".;..\lib\mysql-connector-j-9.6.0.jar" com.pharmacy.main.Main
