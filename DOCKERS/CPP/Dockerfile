FROM --platform=linux/x86_64 frolvlad/alpine-gxx
WORKDIR /root
#RUN !/bin/bash

RUN apk add --no-cache bash
#RUN sleep 300

#Creamos el archivo de salida del tiempo
RUN touch signalCompilador.txt
RUN touch signalEjecutor.txt
RUN touch time.txt
RUN touch salidaEstandar.ans
RUN touch salidaError.ans
RUN touch salidaCompilador.ans

#Primero compilamos  DESPUES miramos que el fichero exista y lo ejecutamos si no existe escribimos en en la salida del compilador q no existe
ENTRYPOINT g++ $FILENAME2 -o $FILENAME1 > salidaCompilador.ans; echo $? >> signalCompilador.txt; timeout -s SIGTERM  $EXECUTION_TIMEOUT /usr/bin/time -o time.txt -f "%U %M" $MEMORYLIMIT ./$FILENAME1 < entrada.in > salidaEstandar.ans 2> salidaError.ans; echo $? >> signalEjecutor.txt