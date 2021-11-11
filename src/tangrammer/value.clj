(ns tangrammer.value)

(defmulti print-nil (fn [dialect] dialect))

(defmethod print-nil :default [dialect]
  :unsopported-op)

(defmulti print-long (fn [dialect arg] dialect))

(defmethod print-long :default [dialect arg]
  :unsopported-op)

(defmulti print-string (fn [dialect arg] dialect))

(defmethod print-string :default [dialect arg]
  :unsopported-op)

(defmulti print-where (fn [dialect arg] dialect))

(defmethod print-where :default [dialect arg]
  :unsopported-op)

(defmulti print-query (fn [dialect table where limit] dialect))

(defmethod print-query :default [dialect table where limit]
  :unsopported-op)
