(ns yerd.edit.core)
#_(ns yerd.inspect.tap.core
  "Entity and functions for tapping a value with a locaiton."
  (:require
   [clojure.core            :as core]
   [yerd.app.plugin         :as plug]
   [yerd.code.location.core :as loc]
   [yerd.inspect.tap.schema :refer [Type Tap]]
   [yerd.ontology           :as sc])
  (:refer-clojure :exclude [intern]))
#_ (remove-ns (ns-name *ns*))


;; - handle open event
;; - load plugin from config
;;   - do this at startup
;;   - specify configuration
;;   - load plugins
;; - ? is a system component needed?
;;   - config, plugins
;;   - helpful for testing different sets of plugins
;;
;; yerd.system.config (is this the domain of the app? is it only for plugins?)
;; yerd.system.
;;
;; ? malli spec on configuration?

(defn create
  "Return a tap entity that has a `location` and a `value`."
  [location value]
  (-> [location value]
      (sc/with-type Type)))


;; Disabled for now
#_ (plug/require plug/Emacs)
