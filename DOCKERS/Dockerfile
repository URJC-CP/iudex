#La instalacion que vamos a utilizar. (de donde venimos)
FROM openjdk:alpine
WORKDIR /root


#Compila y Ejecuta el proyecto
ENTRYPOINT javac codigo.java > salidaCompilador.txt; java codigo <entrada.txt >salidaEstandar.txt 2>salidaError.txt



