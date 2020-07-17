(ns notes.api
  (:require [ring.adapter.jetty :refer [run-jetty]])
  (:require [ring.middleware.defaults :refer [wrap-defaults api-defaults]])
  (:require [ring.util.response :refer [response]])
  (:require [ring.middleware.json :refer [wrap-json-response]])
  (:require [compojure.core :refer [routes GET]])
  (:require [integrant.core :as ig])
  (:gen-class))

(def config
  {:adapter/jetty {:port (Integer/valueOf (or (System/getenv "port") "3000"))
                   :handler (ig/ref :routes/foo)}
   :routes/foo {:base-handler (ig/ref :handler/base)}
   :handler/base {:name "foo"}})

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  (run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defmethod ig/init-key :handler/base [_ {:keys [name]}]
  (fn [] (response {"name" name})))

(defmethod ig/init-key :routes/foo [_ {:keys [base-handler]}]
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
