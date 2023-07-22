(ns yerd.clojure.namespace
  "Functions for working with namespaces."
  (:require
   [clojure.core    :as core]
   [clojure.string  :as str]
   [hyperfiddle.rcf :as rcf])
  (:refer-clojure :exclude [name namespace])
  (:import
   [clojure.lang Namespace Named]))

(defn named?
  [x]
  (instance? Named x))
(rcf/tests
 (named? :foo ) := true
 (named? 'foo ) := true
 (named? nil  ) := false
 (named? "foo") := false)

(defn namespace?
  "Return true if `x` is a namespace."
  [x]
  (instance? Namespace x))

(rcf/tests
 "namespace?"
 (namespace? *ns*) := true
 (namespace? 'foo) := false)

(defn name
  "Like `clojure.core/name` but works for namespaces as well."
  [x]
  (let [name-fn (if (namespace? x)
                  (comp core/name ns-name)
                  core/name)]
    (some-> x name-fn)))

(rcf/tests
 "name"
 (name      nil) := nil
 (name     *ns*) := "yerd.clojure.namespace"
 (name :foo/bar) := "bar"
 (name 'foo/bar) := "bar"
 (name    "foo") := "foo")

(defn namespace
  "Like `clojure.core/namespace` but works for namespaces as well."
  [x]
  (cond
    (nil? x)       nil
    (string? x)    x
    (namespace? x) (name x)
    :else          (core/namespace x)))
(rcf/tests
 (namespace nil     ) := nil
 (namespace :foo    ) := nil
 (namespace 'foo    ) := nil
 (namespace *ns*    ) := "yerd.clojure.namespace"
 (namespace :foo/bar) := "foo"
 (namespace 'foo/bar) := "foo"
 (namespace "foo"   ) := "foo")

(defn split
  "Split a dot separated string, keyword, symbol, or namespace into parts."
  [x]
  (let [name-parts (cond
                     (nil? x)       nil
                     (namespace? x) [(name x)]
                     (named? x)     (->> [(namespace x) (name x)]
                                         (remove nil?)
                                         vec)
                     :else          [x])]
    (some->> name-parts
             (mapcat #(str/split % #"\."))
             (remove str/blank?)
             seq)))

(rcf/tests
 "split"
 (split nil          ) := nil
 (split ""           ) := nil
 (split :foo         ) := '("foo")
 (split 'foo         ) := '("foo")
 (split *ns*         ) := '("yerd" "clojure" "namespace")
 (split "foo.bar"    ) := '("foo" "bar")
 (split :foo.bar/baz ) := '("foo" "bar" "baz")
 (split 'foo.bar/baz ) := '("foo" "bar" "baz")
 (split :foo/bar.baz ) := '("foo" "bar" "baz"))

(defn join
  "Join namespace segments"
  [segments]
  (->> segments
       (remove nil?)
       (mapcat split)
       (str/join ".")
       (#(if (empty? %) nil %))))
(rcf/tests
 "join"
 (join []           ) := nil
 (join ["foo"]      ) := "foo"
 (join ["foo" "bar"]) := "foo.bar")


;; -? what can qualify be used for?
;; - symbol (bare or namespaced)
;; - keyword (bare or namespaced)
;; - ns-sym ?

;; these should just return parts

;; join parts
;; then have functions for turning parts to things:
;; - ->keyword    (namespace/name)
;; - ->symbol     (namespace/name)
;; - ->namespace  namespace

(defn ->keyword
  "Like `clojure.core/keyword` but takes more parts and returns a
  normalized keyword.

  Normalized keywords allow dots in the namespace but not in the name.

  Good Examples:
  - :foo
  - :foo/bar
  - :foo.bar/baz
  - :person/bar-baz  ; dash is ok in the name

  Bad Examples:
  - :foo.bar
  - :foo/bar.baz"
  [& xs]
  (when-let [segments (->> xs (mapcat split) seq)]
    (keyword (join (butlast segments)) (last segments))))

(rcf/tests
 "keyword"
 (->keyword                      ) := nil
 (->keyword "foo"                ) := :foo
 (->keyword :foo :bar            ) := :foo/bar
 (->keyword :foo :bar :baz       ) := :foo.bar/baz
 (->keyword 'foo "bar" :baz "qux") := :foo.bar.baz/qux
 (->keyword :foo/bar :baz/qux    ) := :foo.bar.baz/qux)

(defn ->symbol
  [& xs]
  (when-let [segments (->> xs (mapcat split) seq)]
    (symbol (join (butlast segments)) (last segments))))

(defn ->namespace
  [& xs]
  (->> xs
       (mapcat split)
       join))

(defn relativize
  [child parent]
  (let [child (-> child name split)
        parent (-> parent name split)]
    (->> (concat parent (repeat nil))
         (map vector child)
         (reduce (fn [v [x1 x2]]
                   (cond-> v
                     (not= x1 x2) (conj x1)))
                 [])
         join)))
(rcf/tests
 (relativize nil               nil      ) := nil
 (relativize :foo              nil      ) := "foo"
 (relativize :foo              :foo     ) := nil
 (relativize :foo.bar.baz      :foo     ) := "bar.baz"
 (relativize "foo.bar.baz.qux" "foo.bar") := "baz.qux")

(defn abbreviate
  "Shorten a namespace for concise display."
  [ns]
  (some->> ns
           namespace
           split
           reverse
           (take 2)
           reverse
           join))
(rcf/tests
 "shorten"
 (abbreviate nil          ) := nil
 (abbreviate *ns*         ) := "clojure.namespace"
 (abbreviate "foo"        ) := "foo"
 (abbreviate "foo.bar"    ) := "foo.bar"
 (abbreviate "foo.bar.baz") := "bar.baz")
