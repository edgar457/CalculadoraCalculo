# CalculadoraCalculo — Proyecto #3 (Programación II)

Calculadora de derivadas e integrales (indefinidas y definidas) de polinomios en **x** o **y**,
en Java (Swing / JFrame) con programación orientada a objetos.

## Estructura
```
src/calculadora/modelo   Fraccion, Termino, Polinomio   (objetos que se derivan/integran a sí mismos)
src/calculadora/logica   ParserPolinomio, MotorCalculo, Resultado
src/calculadora/gui      PanelCalculadora, VentanaPrincipal (interfaz Swing)
src/calculadora/Main.java
test/                    PruebasMotor (28 pruebas), CapturaPantalla
docs/                    Documentación, manual de usuario, diagramas UML (docs/img)
dist/                    CalculadoraCalculo.jar
```

## Compilar y ejecutar
```
./build.sh                                  # compila, corre pruebas y genera el JAR (requiere JDK 17+)
java -jar dist/CalculadoraCalculo.jar
```
En NetBeans: *File → New Project → Java with Existing Sources* y seleccionar la carpeta `src`
(clase principal: `calculadora.Main`).

## Documentación
- `docs/Documentacion_CalculadoraCalculo.docx` (documento formal con diagramas UML)
- `docs/MANUAL_USUARIO.md`
