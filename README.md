# Source Connector Transformation
Single Message Transformations (SMTs) applied in source and sink connectors. Before transformation, message need to be in a desired format with respect to predefined transformers.

## Steps
### Install Maven
Install the maven to include dependencies in the pom.xml. Then simply run,
```shell
mvn install
```
Map-Key jar and other related files will be created in the key-map/target directory.

### Docker Build
Check the docker file before proceed the build. There are environment variables that need 
to be correct before deployment and also you can change those in docker run time. 

```shell
CONNECT_BOOTSTRAP_SERVERS=localhost:9092
CONNECT_GROUP_ID=localhost
CONNECT_CONFIG_STORAGE_TOPIC=config-storage
CONNECT_OFFSET_STORAGE_TOPIC=offset-storage
CONNECT_STATUS_STORAGE_TOPIC=status-storage
CONNECT_INTERNAL_KEY_CONVERTER=org.apache.kafka.connect.storage.StringConverter
CONNECT_INTERNAL_VALUE_CONVERTER=org.apache.kafka.connect.json.JsonConverter
CONNECT_KEY_CONVERTER=org.apache.kafka.connect.storage.StringConverter
CONNECT_VALUE_CONVERTER=org.apache.kafka.connect.json.JsonConverter
CONNECT_REST_ADVERTISED_HOST_NAME=localhost
CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR="1"
CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR="1"
CONNECT_STATUS_STORAGE_REPLICATION_FACTOR="1"
```
Mainly check on the **CONNECT_BOOTSTRAP_SERVERS** because it should be properly configure to connect with kafka.
If your kafka server is running on locally then try with ip of you local machine. 
```shell
docker build -t key-map:v0.0.0 .
```

### Run Docker 

Run docker image to execute the connector worker,
```shell
docker run -it -p 8083:8083 key-map:v0.0.0
```
If you need to dynamically configure environment variable,
```shell
docker run -it -p 8083:8083 -e CONNECT_BOOTSTRAP_SERVERS=192.168.8.141:9092 key-map:v0.0.0
```
Enjoy the custom connector behaviours by providing command to port **8083**