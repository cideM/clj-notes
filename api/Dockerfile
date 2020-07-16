FROM clojure:tools-deps
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY . /usr/src/app
EXPOSE 3000
CMD ["clj", "-m", "notes.api"]
