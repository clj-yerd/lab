(ns yerd.inspect.reveal
  (:require
   [vlaaad.reveal        :as r]
   [vlaaad.reveal.stream :as-alias r.stream]
   [vlaaad.reveal.ui     :as r.ui]))

(defn dispose!
  "Close the reveal window."
  []
  (tap> {::r/command '(dispose)}))

(defn add-tap!
  []
  (add-tap (r/ui)))

(defn with-stream-type
  [data k]
  (let [m (-> data
              meta
              (merge {::r.stream/type k}))]
    (with-meta data m)))

(defn ignore-action-result
  [x]
  (with-meta x {::r.ui/ignore-action-result true}))
