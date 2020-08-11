# Notes API - WORK IN PROGRESS

## FAQ

### Start the REPL

`clj -A:cider-clj:dev`

### `Unable to resolve symbol: go in this context`

I should learn Clojure namespaces properly. I don't have all symbols from all namespaces available, so do a `(ns user)` first and then I should have everything available.

### Make a Request

`curl http://localhost:3000/ping`
