(ns yerd.system.log)

(def debug?
  "Atom that contols tapping of debug messages."
  (atom false))

;; toggle by tag and level
(defn debug-toggle!
  "Toggle the `debug?` atom."
  []
  (swap! debug? not))
#_ (debug-toggle!)

;; ? how to pass a tag?
;; ns
;; :plugin
;; :emacs
;; :reveal
(defn debug
  "Tap a debug message if `debug?`is true."
  [x]
  (when @debug?
    (tap> x)))
