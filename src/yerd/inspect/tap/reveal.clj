(ns yerd.inspect.tap.reveal
  "Reveal plugin for the tap entity."
  (:require
   [vlaaad.reveal           :as rev]
   [yerd.system.plugin         :as plug]
   [yerd.inspect.reveal     :as y.rev]
   [yerd.inspect.tap.schema :refer [Type Tap]]
   [yerd.ontology           :as on]))
#_ (remove-ns (ns-name *ns*))

(def StreamType
  (on/qualify-key Type))

(defmethod plug/hook [Type Tap plug/Reveal]
  [data _event _plugin]
  data
  (y.rev/with-stream-type data StreamType))

(rev/defstream StreamType [tap-value]
  (rev/horizontally
   tap-value))
