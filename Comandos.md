# Primero
![img.png](img.png)

# En el terminal
```
$env:Path = "$env:JAVA_HOME/bin;" + $env:Path
``` 
# Arrancamos eñ maven con esto 
```
./mvnw package
```

# Si queremos saltarnos los tests, usamos este comando
```
./mvnw package -DskipTests
```
# Una vez compilado, nos vamos a la carpeta target y ejecutamos el jar
```
cd target
```


# Ejecutamos el jar con este comando
```
java -jar .\AplicacionVideoJuegos.jar
```

# Para arrancar el docker
```
docker compose up
```

# Para el cocker de produccion
```
docker compose -f docker-compose-prod.yml up db 
```