(ns yerd.code.location.core
  "Entity representing a location in code."
  (:require
   [yerd.clojure.namespace    :as ns]
   [yerd.code.location.schema :refer [Type Tap]]
   [yerd.ontology             :as on]
   [yerd.system.log           :refer [debug]]
   [yerd.system.plugin        :as plug]))
#_ (remove-ns (ns-name *ns*))

;; TODO action to jump to source in editor
;; -? how to load editor plugin?
;;    - ? manually?
;;    - ? system property? jam.editor? jam.config={:editor :emacs}
;;    - ? EDITOR env var?

(defn create
  "Return a new location based on `file` `ns` and `form-meta` from a
  macro."
  [file ns form-meta]
  (-> form-meta
      (merge {:file file, :ns ns})
      (on/with-type Type)))

(defn tap-link
  "Return `loc` as a link suitable for tapping.
  Return is a symbol so that meta data can be attached."
  [{:keys [ns line] :as loc}]
  (debug [:tap-link loc])
  (-> ns
      ns/abbreviate
      vector

      ;; Add line number if present.
      ;; - no line for e.g., tagged literals
      (cond->
          line (conj line))

      ;; Move remaining data to meta data.
      (with-meta (merge loc (meta loc)))

      ;; Run hooks
      (plug/run-hooks Tap)))

(plug/require plug/Reveal)
