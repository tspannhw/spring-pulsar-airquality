source ../env.sh
mvn spring-boot:run -Dspring-boot.run.arguments=--auth-plugin=org.apache.pulsar.client.impl.auth.oauth2.AuthenticationOAuth2,--auth-params='{"privateKey":"file:///Users/tspann/Downloads/sndevadvocate-tspann.json", "issuerUrl":"https://auth.streamnative.cloud/", "audience":"urn:sn:pulsar:sndevadvocate:my-instance"}'
