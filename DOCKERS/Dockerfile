#La instalacion que vamos a utilizar. (de donde venimos)
FROM openjdk:alpine
WORKDIR /root
RUN /bin/sh

RUN apk add --no-cache bash
#RUN sleep 300

#Creamos el archivo de salida del tiempo
RUN touch time.txt

#Compila y Ejecuta el proyecto
ENTRYPOINT javac codigo.java >salidaCompilador.ans; /usr/bin/time -o time.txt -f "%e %M" java codigo <entrada.in >salidaEstandar.ans 2>salidaError.ans;




