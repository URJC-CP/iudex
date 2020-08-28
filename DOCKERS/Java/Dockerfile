#La instalacion que vamos a utilizar. (de donde venimos)
FROM openjdk:15-alpine
WORKDIR /root
#RUN /bin/sh

RUN apk add --no-cache bash
#RUN sleep 300

#Creamos el archivo de salida del tiempo
RUN touch signalCompilador.txt
RUN touch signalEjecutor.txt
RUN touch time.txt
RUN touch salidaEstandar.ans
RUN touch salidaError.ans
RUN touch salidaCompilador.ans
#Compila y Ejecuta el proyecto
#ENTRYPOINT javac codigo.java >salidaCompilador.ans; timeout -t $EXECUTION_TIMEOUT /usr/bin/time -o time.txt -f "%e %M" java codigo <entrada.in >salidaEstandar.ans 2>salidaError.ans || echo TIMEOUT >timeout.txt;
#Primero compilamos  DESPUES miramos que el fichero exista y lo ejecutamos si no existe escribimos en en la salida del compilador q no existe
ENTRYPOINT  javac $FILENAME1.java 2>salidaCompilador.ans; echo $? >>signalCompilador.txt;  [ -f $FILENAME2.class ] && timeout -s SIGTERM  $EXECUTION_TIMEOUT /usr/bin/time -o time.txt -f "%U %M" java $MEMORYLIMIT $FILENAME2 <entrada.in >salidaEstandar.ans 2>salidaError.ans; echo $? >>signalEjecutor.txt




