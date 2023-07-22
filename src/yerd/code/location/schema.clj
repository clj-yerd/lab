(ns yerd.code.location.schema
  (:require
   [yerd.ontology :as on]))

(def Type
  (on/make-type))

(def Tap
  "hook event fired when coercing a location to a tap value"
  (on/make-event :tap-link))

(def Open
  "hook event fired when activating a location"
  (on/make-event :open))
