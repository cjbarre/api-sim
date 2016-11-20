FROM openjdk:7
MAINTAINER Cameron Barre <cjbarre@gmail.com>

ARG api_spec

ENV LEIN_ROOT = "true"

RUN apt-get update && apt-get install -y wget

RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && mv lein /bin && chmod 755 /bin/lein && lein

COPY . /api-sim

COPY $api_spec /api-sim/api-spec.json

WORKDIR /api-sim

RUN lein deps

ENTRYPOINT ["lein", "run"]
