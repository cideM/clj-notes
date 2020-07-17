# TODO: Add layer for deps.edn
FROM clojure:tools-deps as builder
WORKDIR /usr/src/app
COPY deps.edn /usr/src/app/deps.edn
# Download dependencies
RUN clj

EXPOSE 3000

FROM builder
CMD ["clj", "-A:cider-clj", "-A:dev"]
