(ns tangrammer.dialects.postgres
  (:require [tangrammer.operators :as op]
            [tangrammer.value :as value]
            [clojure.string :as str]))

(defmethod op/and :postgres [dialect args]
  (str/join " AND " args))

(defmethod op/or :postgres [dialect args]
  (str/join " OR " args))

(defmethod op/not :postgres [dialect [v1]]
  (str "NOT " v1))

(defmethod op/> :postgres [dialect [v1 v2]]
  (str v1 " > " v2))

(defmethod op/< :postgres [dialect [v1 v2]]
  (str v1 " < " v2))

(defmethod op/= :postgres [dialect [v1 v2 & more]]
  (if (seq more)
    (str v1 " IN (" (str/join " " (conj more v2)) ")")
    (if (not= v2 "NULL")
      (str v1 " = " v2)
      (op/is-empty dialect [v1]))))

(defmethod op/!= :postgres [dialect [v1 v2 & more]]
  (if (seq more)
    (str v1 " NOT IN (" (str/join " " (conj more v2)) ")")
    (if (not= v2 "NULL")
      (str v1 " <> " v2)
      (op/not-empty dialect [v1]))))

(defmethod op/is-empty :postgres [dialect [v1]]
  (str v1 " IS NULL"))

(defmethod op/not-empty :postgres [dialect [v1]]
  (str v1 " IS NOT NULL"))

(defmethod value/print-nil :postgres [dialect]
  "NULL")

(defmethod value/print-long :postgres [dialect arg]
  arg)

(defmethod value/print-string :postgres [dialect arg]
  (format "'%s'" arg))

(defmethod value/print-where :postgres [dialect arg]
  arg)

(defmethod value/print-query :postgres [dialect table where limit]
  (condp = [(boolean where) (boolean limit)]
    [true true] (format "SELECT * FROM %s WHERE %s LIMIT %s" table where limit)
    [true false] (format "SELECT * FROM %s WHERE %s" table where)
    [false true] (format "SELECT * FROM %s LIMIT %s" table limit)))
