(ns yerd.system.core
  (:require
   [donut.system      :as ds]
   [yerd.clojure.core :as y.cc]
   [yerd.ontology     :as on]))
#_ (remove-ns (ns-name *ns*))

(def ^:dynamic *system*
  "The donut system controlling all features and plugins."
  nil)

(defn running?
  "Return true if the system is running."
  []
  (some? *system*))

(defn assert-started
  []
  (or (running?) (throw (ex-info "can't stop a system that isn't running" {}))))

(defn assert-stopped
  []
  (and (running?) (throw (ex-info "can't start an already running system" {}))))

;; HOW DO HOOKS WORK
;; - component starts
;; - component registers hooks
;; - plugin starts
;; - plugin manager calls hook setup for hooks registered by components
;;   - hook/enable

;; DEFINE THE FOLLOWING (with sequence diagram)

;; COMPONENT LIFECYCLE (managed by system)
;; - start
;; - stop

;; PLUGIN LIFECYCLE (managed by plug in manager)
;; - start
;; - stop

;; HOOK LIFECYCLE (managed by plug in manager)
;; - register (components that support hooks register them on component start)
;; - start (plug in manager enables registered hooks on plugin start)
;; - stop (plug in manager disabled registered hoks on plugin stop)
;; - un-register (components that support hooks un register them on component start)
;;   remove
;;   delete

;; EVENT FLOW
;; - component that supports hooks triggers hook
;; - plug in manager dispatches hook based on what is enabled

;; ? Where to define the list of known components?
;;   - need their ns to require
;;   - ? could be passed in for custom once?

(def FEATURES
  {:rcf    :test/rcf
   :tap    :inspect/tap
   :reveal :inspect/reveal
   :emacs  :edit/emacs})

(defn load-feature!
  "Require the component namespace implementing `feature`. Resolve and return the component."
  [feature]
  (let [feature-ns (FEATURES feature)]
    (or feature-ns
        (throw (ex-info "Unknown feature" {:feature feature :supported-features (keys FEATURES)})))
    @(requiring-resolve (symbol (on/->namespace feature-ns) "Donut"))))

(defn start
  "Start the system with the provided `features`."
  [features]
  (assert-stopped)
  (let [donuts (->> features
                    (map load-feature!)
                    (apply y.cc/deep-merge))
        system {::ds/defs donuts}]

    ;; Start the system
    (locking (Object.)
      (let [system (ds/signal system ::ds/start)]
        ;; TODO load plugins
        (alter-var-root #'*system* (constantly system))))

    *system*))

(defn stop
  "Stop the system."
  []
  (assert-started)
  (locking (Object.)
    (ds/signal *system* ::ds/stop)
    (alter-var-root #'*system* (constantly nil))))

(comment
  ;; test starting and stopping the system
  (running?)
  (start #{:rcf})
  (running?)
  (stop)

  :end)
