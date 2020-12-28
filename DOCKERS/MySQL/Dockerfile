FROM woahbase/alpine-mysql
WORKDIR /root
RUN apk add --no-cache bash

#Creamos el archivo de salida del tiempo
#RUN touch signalCompilador.txt
RUN touch signalEjecutor.txt
#RUN touch time.txt
RUN touch salidaEstandar.ans
RUN touch salidaError.ans
#RUN touch salidaCompilador.ans

ENTRYPOINT mysql -u $USER -p $PASSWORD test < $FILENAME1.sql > salidaEstandar.ans 2> salidaError.ans; echo $? >> signalEjecutor.txt