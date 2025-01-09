FROM eclipse-temurin:21-jre-ubi9-minimal
WORKDIR /home/container

LABEL org.opencontainers.image.source="https://github.com/casterlabs/tcp-proxy"

# code
COPY ./docker_launch.sh /home/container
COPY ./target/tcp-proxy.jar /home/container
RUN chmod +x docker_launch.sh

# entrypoint
CMD [ "./docker_launch.sh" ]
EXPOSE 8000/tcp