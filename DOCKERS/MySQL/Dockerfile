FROM woahbase/alpine-mysql
WORKDIR /root

RUN apk add --no-cache bash

# variables del entorno
ENV MYSQL_USER=root
ENV MYSQL_USER_PWD=password
ENV MYSQL_USER_DB=test

#Creamos el archivo de salida del tiempo
#RUN touch signalCompilador.txt
RUN touch signalEjecutor.txt
RUN touch time.txt
RUN touch salidaEstandar.ans
RUN touch salidaError.ans
#RUN touch salidaCompilador.ans

ENTRYPOINT mysql -p 3306:3306 < $FILENAME2.in; mysql < $FILENAME1.in > salidaEstandar.ans 2> salidaError.ans; echo $? >> signalEjecutor.txt