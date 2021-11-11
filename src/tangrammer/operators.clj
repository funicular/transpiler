(ns tangrammer.operators
  (:refer-clojure :exclude [and or not < > = not-empty]))

(defmulti and (fn [dialect args] dialect))

(defmethod and :default [dialect args]
  :unsopported-op)

(defmulti or (fn [dialect args] dialect))

(defmethod or :default [dialect args]
  :unsopported-op)

(defmulti not (fn [dialect args] dialect))

(defmethod not :default [dialect args]
  :unsopported-op)

(defmulti < (fn [dialect args] dialect))

(defmethod < :default [dialect args]
  :unsopported-op)

(defmulti > (fn [dialect args] dialect))

(defmethod < :default [dialect args]
  :unsopported-op)

(defmulti = (fn [dialect args] dialect))

(defmethod = :default [dialect args]
  :unsopported-op)

(defmulti != (fn [dialect args] dialect))

(defmethod != :default [dialect args]
  :unsopported-op)

(defmulti is-empty (fn [dialect args] dialect))

(defmethod is-empty :default [dialect args]
  :unsopported-op)

(defmulti not-empty (fn [dialect args] dialect))

(defmethod not-empty :default [dialect args]
  :unsopported-op)
