(ns notes.server
  (:require
    [ring.adapter.jetty :as jetty]
    [reitit.ring :as ring]
    [ring.util.response :refer [response]]
    [ring.middleware.json :refer [wrap-json-response]]
    [next.jdbc :as jdbc]
    [next.jdbc.sql :as sql]
    [integrant.core :as ig]
    [hikari-cp.core :refer [make-datasource close-datasource]])
  (:gen-class))

(def config
  {:notes/server {:port (Integer/valueOf (or (System/getenv "port") "3000"))
                  :host (or (System/getenv "host") "0.0.0.0")
                  :handler (ig/ref :notes/handler)}
   :notes/handler  {:db (ig/ref :db/pool)}
   :db/pool       {:adapter "postgresql"
                   :password "password"
                   :username "root"
                   :server-name "localhost"
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
  :notes/server
  [_ {:keys [handler] :as opts}]
  (jetty/run-jetty handler
    (-> opts
        (dissoc :notes/handler)
        (assoc :join? false))))

(defmethod ig/halt-key!
  :notes/server
  [_ server]
  (.stop server))

(defmethod ig/init-key
  :notes/handler
  [_ {:keys [db]}]
  (ring/ring-handler
    (ring/router
      ["/ping" {:get {:handler (fn [_] (response (sql/query db ["select * from notes"])))}
                :middleware [wrap-json-response]}])))

(comment
  (response "foo"))
