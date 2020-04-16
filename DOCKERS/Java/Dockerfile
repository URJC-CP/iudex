#La instalacion que vamos a utilizar. (de donde venimos)
FROM openjdk:alpine
WORKDIR /root
#RUN /bin/sh

RUN apk add --no-cache bash
#RUN sleep 300

#Creamos el archivo de salida del tiempo
RUN touch timeout.txt
RUN touch time.txt
RUN touch salidaEstandar.ans
RUN touch salidaError.ans
RUN touch salidaCompilador.ans
#Compila y Ejecuta el proyecto
#ENTRYPOINT javac codigo.java >salidaCompilador.ans; timeout -t $EXECUTION_TIMEOUT /usr/bin/time -o time.txt -f "%e %M" java codigo <entrada.in >salidaEstandar.ans 2>salidaError.ans || echo TIMEOUT >timeout.txt;
ENTRYPOINT javac $FILENAME.java 2>salidaCompilador.ans; timeout -t $EXECUTION_TIMEOUT /usr/bin/time -o time.txt -f "%e %M" java $FILENAME <entrada.in >salidaEstandar.ans 2>salidaError.ans || echo TIMEOUT >timeout.txt;




