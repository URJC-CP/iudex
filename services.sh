#!/bin/bash


if [ "$1" = "--help" ] || [ "$1" = "-h" ]; then

    echo  -e "
        -----------------------------------------------------------------------
        |                            DESCRIPTION                              |
        -----------------------------------------------------------------------

          Shell script to run, stop and/or remove Iudex RabbitMQ, MySQL and
        Keycloak service containers.

        -----------------------------------------------------------------------
        |                           HOW TO LAUNCH                             |
        -----------------------------------------------------------------------

        Call the command with
          ./services.sh [OPTIONS]

        Available options:
          -h,  --help    Shows information about running the script
          -r,  --run     Runs all three containers
          -s,  --stop    Stops all three containers
          -rm, --remove  Remove all three containers
    "

    exit 0
fi

while true; do
    if [ "$1" = "--run" ] || [ "$1" = "-r" ]; then
        docker run --name iudex-rabbitmq -p 5672:5672 -d rabbitmq:3
        docker run --name iudex-database -v db_data:/var/lib/mysql -p 3306:3306 -e MYSQL_HOST=bbdd -e MYSQL_RANDOM_ROOT_PASSWORD=yes -e MYSQL_DATABASE=testdb -e MYSQL_USER=iudexuser -e MYSQL_PASSWORD=iudexpassword -d mysql:8
        docker run --name iudex-keycloak -p 3000:8080 -e KEYCLOAK_ADMIN=iudexci -e KEYCLOAK_ADMIN_PASSWORD=p455w0rd -v $(pwd)/keycloak-ci-realm.json:/opt/keycloak/data/import/realm.json -d keycloak/keycloak:22.0 start-dev --import-realm --hostname-port=3000

        shift 1
    elif [ "$1" = "--stop" ] || [ "$1" = "-s" ]; then
        docker stop iudex-rabbitmq
        docker stop iudex-database
        docker stop iudex-keycloak

        shift 1
    elif [ "$1" = "--remove" ] || [ "$1" = "-rm" ]; then
        docker rm iudex-rabbitmq
        docker rm iudex-database
        docker rm iudex-keycloak

        shift 1
    else
        break
    fi
done