(ns user
  (:require [integrant.repl :refer [clear go halt prep init reset reset-all]])
  (:require [notes.server :refer [config]]))

(integrant.repl/set-prep! (fn [] config))
