(ns yerd.inspect.tap.schema
  (:require
   [yerd.ontology :as on]))

(def Type
  (on/make-type))

(def Tap
  "hook event fired when tapping a value"
  (on/make-event :tap))

;; -? how does next.jdbc get away with just using the table name?
;;    - the table is a clear anchor

;; clojure namespaces are used in hierarchies
;; - mapped on to file systems
;; - easy to build human intuition
;;
;; values can exist anywhere
;; context is a cognitive mapping humans apply
;;
;; maps are trees
;; namespace dependencies are DAGs
;; neither are fully free graphs
;;
;; namespaces
;; - are hierarchical in practice
;; - have to be anchored in some context
;;    - database
;;      - <table>/column
;;      - <schema>.<table>/column
;;    - clojure program
;;      - <project>.<domain>.<component>
;;      - <company>.components.<brick>
;;
;; keys in maps
;; - attach a value at a place
;; - ? does the key imply a type?
;;   - ? how is the type of a value determined? (note: type is context dependent)
;;     - embedded directly in the value (sub key)
;;     - may be implied based on the position inside the larger shape (bare keyword)
;;     - may be explicit based on the namespace of the key (fully qualified)
;;       - useful to overlay other type hierarchies onto a map shape
;;         - e.g., extend a value type with attributes specific to some library or context
;;           :donut.system/start - start function but specific to donut
;;
;; map entries are attributes [k v] tuple
;;
;; reserve "." for use in namespaces and not names
;;
;; overlay/project/embed/nest
;; - embed / nest are for shapes
;; - overlay is for other ns hierarchy onto a shape
;;
;; namespaced keys work with aliases
;; vars are useful for tooling, linting, refactoring

;; EAV model - richer graph
;; -? attribute first model?
;;    - again needs a context
