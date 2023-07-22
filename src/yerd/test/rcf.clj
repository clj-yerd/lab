(ns yerd.test.rcf
  (:require
   [hyperfiddle.rcf :as rcf]
   [yerd.ontology   :as on]))

(defn start [_]
  (rcf/enable! true))

(defn stop [_]
  (rcf/enable! false))

(def Donut
  (on/->donut
   {:start start
    :stop  stop}))
