FROM mariadb
WORKDIR /root

#RUN apk add --no-cache bash

# variables del entorno
ENV MYSQL_USER="root"
ENV MYSQL_ROOT_PASSWORD="my-secret-pw"
ENV MYSQL_DATABASE="test"

#Creamos el archivo de salida del tiempo
#RUN touch signalCompilador.txt
RUN touch signalEjecutor.txt
RUN touch time.txt
RUN touch salidaEstandar.ans
RUN touch salidaError.ans
#RUN touch salidaCompilador.ans

#ENTRYPOINT mysql -h localhost -u root test <entrada.in && mysql -h localhost -u root test <$FILENAME2 > salidaEstandar.ans 2> salidaError.ans; echo $? >> signalEjecutor.txt