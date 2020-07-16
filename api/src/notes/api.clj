(ns notes.api
  (:require [ring.adapter.jetty :refer [run-jetty]])
  (:require [ring.middleware.defaults :refer [wrap-defaults api-defaults]])
  (:require [ring.util.response :refer [response]])
  (:require [ring.middleware.json :refer [wrap-json-response]])
  (:require [compojure.core :refer [defroutes GET]])
  (:gen-class))

(defroutes handler
  (wrap-json-response
    (GET "/" [] (response { "a" 1 }))))

(def api
  (wrap-defaults handler api-defaults))

(defn -main
  [& args]
  (run-jetty api {:port (Integer/valueOf (or (System/getenv "port") "3000"))}))
