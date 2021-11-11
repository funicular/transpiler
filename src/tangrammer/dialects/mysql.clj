(ns tangrammer.dialects.mysql
  (:require [tangrammer.operators :as op]
            [tangrammer.value :as value]
            [clojure.string :as str]))

(defmethod op/and :mysql [dialect args]
  (str/join " AND " args))

(defmethod op/or :mysql [dialect args]
  (str/join " OR " args))

(defmethod op/not :mysql [dialect [v1]]
  (str "NOT " v1))

(defmethod op/> :mysql [dialect [v1 v2]]
  (str v1 " > " v2))

(defmethod op/< :mysql [dialect [v1 v2]]
  (str v1 " < " v2))

(defmethod op/= :mysql [dialect [v1 v2 & more]]
  (if (seq more)
    (str v1 " IN (" (str/join "," (conj more v2)) ")")
    (str v1 " = " v2)))

(defmethod op/!= :mysql [dialect [v1 v2 & more]]
  (if (seq more)
    (str v1 " NOT IN (" (str/join "," (conj more v2)) ")")
    (str v1 " != " v2)))

(defmethod op/is-empty :mysql [dialect [v1]]
  (str v1 " IS NULL"))

(defmethod op/not-empty :mysql [dialect [v1]]
  (str v1 " IS NOT NULL"))

(defmethod value/print-nil :mysql [dialect]
  "NULL")

(defmethod value/print-long :mysql [dialect arg]
  arg)

(defmethod value/print-string :mysql [dialect arg]
  (format "'%s'" arg))

(defmethod value/print-where :mysql [dialect arg]
  arg)

(defmethod value/print-query :mysql [dialect table where limit]
  (condp = [(boolean where) (boolean limit)]
    [true true] (format "SELECT * FROM %s WHERE %s LIMIT %s" table where limit)
    [true false] (format "SELECT * FROM %s WHERE %s" table where)
    [false true] (format "SELECT * FROM %s LIMIT %s" table limit)))
