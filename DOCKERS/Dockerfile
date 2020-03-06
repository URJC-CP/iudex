#La instalacion que vamos a utilizar. (de donde venimos)
FROM openjdk:alpine
WORKDIR /root
RUN /bin/sh

RUN apk add --no-cache bash
#RUN sleep 30

#Compila y Ejecuta el proyecto
ENTRYPOINT javac codigo.java >salidaCompilador.ans; java codigo <entrada.in >salidaEstandar.ans 2>salidaError.ans;



