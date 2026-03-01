FROM registry.access.redhat.com/ubi9/openjdk-21:1.20 AS builder
USER root
RUN microdnf install -y gzip && microdnf clean all
USER 185
COPY --chown=185 . /code
WORKDIR /code
RUN ./mvnw package -DskipTests

FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.20

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

COPY --from=builder --chown=185 /code/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder --chown=185 /code/target/quarkus-app/*.jar /deployments/
COPY --from=builder --chown=185 /code/target/quarkus-app/app/ /deployments/app/
COPY --from=builder --chown=185 /code/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8005
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"
