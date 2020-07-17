(ns notes.api
  (:require [ring.adapter.jetty :refer [run-jetty]])
  (:require [ring.middleware.defaults :refer [wrap-defaults api-defaults]])
  (:require [ring.util.response :refer [response]])
  (:require [ring.middleware.json :refer [wrap-json-response]])
  (:require [compojure.core :refer [routes GET]])
  (:require [next.jdbc :as jdbc])
  (:require [next.jdbc.sql :as sql])
  (:require [integrant.core :as ig])
  (:require [hikari-cp.core :refer [make-datasource close-datasource]])
  (:gen-class))

(def config
  {:adapter/jetty {:port (Integer/valueOf (or (System/getenv "port") "3000"))
                   :host (or (System/getenv "host") "0.0.0.0")
                   :handler (ig/ref :routes/foo)}
   :routes/foo    {:base-handler (ig/ref :handler/base)}
   :handler/base  {:name "foo"
                   :db (ig/ref :db/pool)}
   :db/pool       {:adapter "postgresql"
                   :password "password"
                   :username "root"
                   :server-name "db"
                   :port-number "5432"
                   :database-name "notes"}})

(defmethod ig/init-key
  :db/pool
  [_ opts]
  (let [ds (make-datasource opts)]
    (do (jdbc/execute! ds ["
         create table if not exists notes (
           id SERIAL,
           title varchar(100),
           body varchar(500)
         )"])
        ds)))

(defmethod ig/halt-key! :db/pool [_ opts] (close-datasource opts))

(defmethod ig/init-key
  :adapter/jetty
  [_ {:keys [handler] :as opts}]
  (run-jetty handler
    (-> opts
        (dissoc :handler)
        (assoc :join? false))))

(defmethod ig/halt-key!
  :adapter/jetty
  [_ server]
  (.stop server))

(defmethod ig/init-key
  :handler/base
  [_ {:keys [name db]}]
  (fn
    []
    (response (sql/query db ["select * from notes"]))))

(defmethod ig/init-key
  :routes/foo
  [_ {:keys [base-handler]}]
  (-> (routes (GET "/" [] (base-handler)))
      (wrap-json-response)
      (wrap-defaults api-defaults)))

;; TODO: Bring back -main
; (def system
;   (ig/init config))

; (comment (ig/halt! system))

; (defn -main
;   [& args]
;   (system))
