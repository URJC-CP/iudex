#!/bin/bash


#Pausar todos los docker 
docker stop $(docker ps -a -q);



#Borrar todos los docker 
docker rm $(docker ps -a -q);


#Borrar todas las imagenes
docker rmi $(docker images -q);



#Construit imagen
docker build -t pavlo/pavlo .;



#Crear contenedor
docker create --name cont pavlo/pavlo;



#Copiar codigo
docker cp codigo.java cont:/root;


#Copiar entrada
docker cp entrada.txt cont:/root;


#Arrancar contenedor
docker start cont;
sleep 1;

#Copiar salida Estandar
docker cp cont:/root/salidaEstandar.txt .;

#Copiar salida error
docker cp cont:/root/salidaError.txt .;

#Copiar salida error
docker cp cont:/root/salidaCompilador.txt .;

#Borrar contenedor
docker rm cont;
