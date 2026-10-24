#!/bin/bash
# Compila, prueba y empaqueta CalculadoraCalculo.jar (requiere JDK 17 o superior)
set -e
cd "$(dirname "$0")"
rm -rf out main_out && mkdir -p out main_out dist
javac -encoding UTF-8 -d main_out $(find src -name "*.java")
javac -encoding UTF-8 -cp main_out -d out $(find test -name "*.java")
java -Dstdout.encoding=UTF-8 -cp main_out:out calculadora.PruebasMotor
printf "Main-Class: calculadora.Main\n" > main_out/MANIFEST.MF
jar cfm dist/CalculadoraCalculo.jar main_out/MANIFEST.MF -C main_out calculadora
echo "JAR generado: dist/CalculadoraCalculo.jar  (ejecutar: java -jar dist/CalculadoraCalculo.jar)"
