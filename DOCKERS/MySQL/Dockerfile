FROM mariadb:10
WORKDIR /root

#RUN apk add --no-cache bash

# variables del entorno
ENV MYSQL_ROOT_PASSWORD="root"
ENV MYSQL_DATABASE="test"
ENV MYSQL_USER="user"
ENV MYSQL_PASSWORD="pass"

#Creamos el archivo de salida del tiempo
#RUN touch signalCompilador.txt
RUN touch signalEjecutor.txt
RUN touch time.txt
RUN touch salidaEstandar.ans
RUN touch salidaError.ans
#RUN touch salidaCompilador.ans

#ENTRYPOINT mysql -h localhost -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE <$FILENAME1 && mysql -h localhost -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE <$FILENAME2 >salidaEstandar.ans 2>salidaError.ans; echo $? >> signalEjecutor.txt