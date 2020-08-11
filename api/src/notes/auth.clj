(ns notes.auth
  (:require [buddy.auth :refer [authenticated?]]
           [ring.util.http-response :as resp]
           [buddy.auth.backends.session :refer [session-backend]]
           [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

;; I don't think I need this
(defn auth-middleware
  "Middleware used in routes that require authentication.
  Returns 401 if user is not authenticated."
  [handler]
  (fn [request]
    (if (authenticated? request)
      (handler request)
      (resp/unauthorized {:error "not authorized"}))))

(def session-auth-backend
  (session-backend {:unauthorized-handler (fn [_ _] (resp/unauthorized {:error "not authorized"}))}))

(defn session-authentication-middleware [handler] (wrap-authentication handler session-auth-backend))
(defn session-authorization-middleware [handler] (wrap-authorization handler session-auth-backend))
