(ns yerd.clojure.core
  "Functions to tweak or work with clojure.core."
  (:require
   [clojure.core    :as core]
   [hyperfiddle.rcf :as rcf]
   [yerd.clojure.namespace :as ns]))
#_ (remove-ns (ns-name *ns*))


(defn taps
  "Return the set of registered tap functions in `clojure.core`."
  []
  @@#'core/tapset)
#_ (taps)

(defn clear-taps!
  "Clear the `clojure.core` tap registry."
  []
  (reset! @#'core/tapset #{}))
#_ (clear-taps!)

(defn truncate-while
  [pred coll]
  (->> coll
       reverse
       (drop-while pred)
       reverse))

(defn qualify-keys
  [m n]
  (reduce-kv (fn [acc k v]
               (let [new-kw (if (and (keyword? k)
                                     (not (qualified-keyword? k)))
                              (keyword (str n) (name k))
                              k) ]
                 (assoc acc new-kw v)))
             {} m))

(defn deep-merge [& maps]
  (letfn [(reconcile-keys [val-in-result val-in-latter]
            (if (and (map? val-in-result)
                     (map? val-in-latter))
              (merge-with reconcile-keys val-in-result val-in-latter)
              val-in-latter))
          (reconcile-maps [result latter]
            (merge-with reconcile-keys result latter))]
    (reduce reconcile-maps maps)))
