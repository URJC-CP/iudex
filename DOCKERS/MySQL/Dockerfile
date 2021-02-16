FROM mariadb
WORKDIR /root

#RUN apk add --no-cache bash

# variables del entorno
#ENV MYSQL_USER="root"
ENV MYSQL_ROOT_PASSWORD="my-secret-pw"
#ENV MYSQL_USER_DB="test"

#Creamos el archivo de salida del tiempo
#RUN touch signalCompilador.txt
RUN touch signalEjecutor.txt
RUN touch time.txt
RUN touch salidaEstandar.ans
RUN touch salidaError.ans
#RUN touch salidaCompilador.ans

ENTRYPOINT -p 127.0.0.1:4306:3306 --rm --name some-mariadb -d mariadb;
#< $FILENAME1;
#< $FILENAME2 > salidaEstandar.ans 2> salidaError.ans; echo $? >> signalEjecutor.txt