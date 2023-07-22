(ns yerd.ontology
  "Functions for mapping the yerd hierarchy onto clojure namespaces in a consistent way."
  (:require
   [hyperfiddle.rcf        :as rcf]
   [yerd.clojure.core      :as y.cc]
   [yerd.clojure.namespace :as y.ns]))
#_ (remove-ns (ns-name *ns*))

;; TODO does this move to yerd? yerd.core? yerd.types? yerd.schema?

(def LibNS
  "namespace prefix to use for keywords that are exposed outside this library"
  :yerd)

(defn qualify-key
  "Qualify a keyword under this project's namespace."
  [k]
  (y.ns/->keyword LibNS k))

(def Type
  "attribute type key for entities"
  (qualify-key :type))

(defn with-type
  "Add type `type-` to entity `e` meta data."
  [e type-]
  (let [m (merge (meta e)
                 {Type type-})]
    (with-meta e m)))

;; key for entity type (in this library)
(defn type-key
  [ns]
  (->> ns
       ;; Drop the library namespace prefix.
       (#(y.ns/relativize % LibNS))
       y.ns/split
       (y.cc/truncate-while #{"schema"})
       seq
       (apply y.ns/->keyword)))
(rcf/tests
 "type-key"
 (type-key nil                          ) := nil
 (type-key *ns*                         ) := :ontology
 (type-key :yerd.inspect.location.schema) :inspect/location)

;; key for entity event (in this library)
(defn make-event*
  [ns k]
  (-> ns
      type-key
      (y.ns/->keyword :event k)))
(rcf/tests
 (make-event* *ns*                    :foo) := :ontology.event/foo
 (make-event* "yerd.inspect.location" :foo) := :inspect.location.event/foo)

(defmacro make-event
  [k]
  `(make-event* ~*ns* ~k))

(defmacro make-type
  []
  `(type-key ~*ns*))

(defn component-ns-segments
  [ns]
  (->> ns
       y.ns/split
       (take 3)
       (zipmap [:project :domain :component])))
#_ (component-ns-segments "yerd.test.rcf")
#_ (component-ns-segments 'yerd.test.rcf)

(defn ->namespace
  [x]
  (y.ns/->namespace LibNS x))

(defn entity-ns
  [entity-ns sub-ns]
  (-> entity-ns
      y.ns/split
      (->> (y.cc/truncate-while #{"core"}))
      (concat [sub-ns])
      y.ns/join))
(rcf/tests
 (entity-ns "yerd.inspect.tap"      :reveal) := "yerd.inspect.tap.reveal"
 (entity-ns "yerd.inspect.tap.core" :reveal) := "yerd.inspect.tap.reveal")

(defn ->donut*
  [donut ns]
  (let [{:keys [domain component]} (component-ns-segments ns)]
    {(keyword domain)
     {(keyword component)
      (-> donut (y.cc/qualify-keys 'donut.system))}}))

(defmacro ->donut
  [donut]
  `(->donut* ~donut *ns*))
