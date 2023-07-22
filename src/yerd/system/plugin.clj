(ns yerd.system.plugin
  "Functions for working with plugins."
  (:require
   [clojure.core    :as core]
   [yerd.ontology   :as on]
   [yerd.system.log :refer [debug]])
  (:refer-clojure :exclude [require]))
#_(remove-ns (ns-name *ns*))

;; --------------------------------------------------
;; KNOWN PLUGINS

(def Reveal :reveal)
(def Emacs :emacs)

(def supported-plugins
  "Map of namespaces to load for each plugin."
  {Reveal {:detect-ns 'vlaaad.reveal}
   Emacs  {}})

;; -? how to dynamically load configured plugins?
;;    - e.g., :reveal - location and tap have plugins
;;      - ? maybe those register themselves as available for reveal?
;;        - then get loaded when reveal is enabled here?
;;        - and unloaded when reveal is disabled?
;;          -? how to unload plugins?
;;             - methods
;;             - remove-method

;; ? how to location and tap get loaded though?
;; - ? system start?
;; - ? damn, we do need a system?
;;   - plugins
;;   - logging

(defn available?
  "Return true if a plugin is available and false otherwise."
  [plugin-ns]
  (try
    (core/require plugin-ns)
    true
    (catch Exception _
      false)))
#_ (available? 'vlaaad.reveal)

;; ? how to model?
;; - manual registry required
;; - plugin must be a known type
#_(plug/activate :emacs)
#_(plug/activate :reveal)

:yerd.edit.emacs
;; -? how to detect?
;; -? detect or register?
;; -? should there be only one active?
;; - there can be only one
;; - ? do we need plugin types?
;;   - inspector
;;     - why would you want multiple of these
;;       - can detect on classpath
;;   - editor
;;     - ? how to detect?
;;       - EDITOR env var
;;       - SystemProperty
;;       - aero
;;       - set from user namespace
;;         yerd.config
;;           - set editor
;;           - set inspector
;;       - ~/.yerd/config.edn
;;

;; --------------------------------------------------
;; DETECTION

(defn detect
  "Detect which supported plugins are available."
  []
  (->> supported-plugins
       (reduce-kv (fn [m k v]
                    (cond-> m
                      (available? v) (assoc k true)))
                  {})))
#_ (detect)
;; => {:reveal true}

(def active-plugins
  "Map of active plugins that were detected."
  (atom (detect)))

(defn active?
  "Returns true if `plugin` is active."
  [plugin]
  (some? (@active-plugins plugin)))
#_ (active? :reveal)
;; => true

;; --------------------------------------------------
;; HOOKS

(defn dispatch
  "Shared multi method dispatch function for plugin hooks."
  [data event plugin]
  (let [type- (-> data meta on/Type)]
    (debug [::dispatch type- event plugin])
    [type- event plugin]))

(defmulti hook
  "General hook for that plugins can define for interesting events.
  Takes [`data` `event-key` `plugin-key`]"
  #'dispatch)

(defmethod hook :default
  [data _event _plugin]
  (debug [::hook :default :enter _event _plugin])
  data)

(defn run-hooks
  "Invoke hooks for `event` for all active plugins."
  [data event]
  (debug [::run-hooks {:data data :event event}])
  (let [plugins  (keys @active-plugins)
        run-hook (fn [data plugin]
                   (hook data event plugin))]
    (reduce run-hook data plugins)))

(defn require*
  "Require a plugin for an entity."
  [ns plugin]
  (when (active? plugin)
    (core/require (symbol (on/entity-ns ns plugin)))))

(defmacro require
  "Require a plugin for the entity based on the current namespace."
  [plugin]
  `(require* ~*ns* ~plugin))
