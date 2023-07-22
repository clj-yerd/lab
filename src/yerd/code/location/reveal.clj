(ns yerd.code.location.reveal
  "Reveal plugin for the location entity."
  (:require
   [clojure.string            :as str]
   [vlaaad.reveal             :as r]
   [yerd.clojure.namespace    :as ns]
   [yerd.code.location.schema :refer [Type Tap Open]]
   [yerd.inspect.reveal       :as y.r]
   [yerd.ontology             :as on]
   [yerd.system.log           :refer [debug]]
   [yerd.system.plugin        :as plug]))
#_ (remove-ns (ns-name *ns*))

(def StreamType
  (on/qualify-key Type))

(defmethod plug/hook [Type Tap plug/Reveal]
  [data _event _plugin]
  (debug [::hook :enter])
  (y.r/with-stream-type data StreamType))

(defn loc->sym
  "Return an abbreviation for `location` as a symbol to display in reveal."
  [location]
  (let [{:keys [ns line], :as m} (meta location)]
    (-> ns
        ns/abbreviate
        vector
        (cond->
            line (conj line))
        (->> (str/join ":"))
        symbol
        (with-meta m))))

;; TODO Fix kondo linting or use defmethod directly.
(r/defstream StreamType [location]
  (let [s (loc->sym location)]
    (r/as s
          (r/raw-string s {:fill :util}))))

#_(require '[vlaaad.reveal.stream :as r.s])
#_(defmethod r.s/stream-dispatch StreamType
  [location ann]
  (let [s (loc->sym location)]
    (->> (r/raw-string s {:fill :util})
         (r/as s)
         (r.s/with-value location ann))))

;; - Trigger the hook here but handle in the editor component.
#_ (defmethod plug/hook [Type Tap plug/Reveal]
  [data _event _plugin]
  (plug/debug [::hook :enter])
  (y.r/with-stream-type data StreamType))

;; TODO move to emacs plugin
#_(require '[babashka.process :refer [sh]])

;; TODO move to emacs plugin
#_(defn emacs-edit-location
  [{:keys [line column file] :as _location}]
  #_ (debug :emacs :reveal)
  #_ (prn "edit" _location)
  (sh "emacsclient" (format "+%d:%d" line column) file))

;; TODO Fix kondo linting.
;; TODO ? how to set the name of the action?
(r/defaction ::edit [location]
  (y.r/ignore-action-result
   #(plug/run-hooks location Open)
   #_#(-> location meta emacs-edit-location)))



(comment
  (let [a 1
        b 2]
    (tap (+ a b)))
  :end)
