(ns tangrammer.dialects.sqlserver
  (:require [tangrammer.operators :as op]
            [tangrammer.value :as value]
            [clojure.string :as str]))

(defmethod value/print-query :sqlserver [dialect table where limit]
  (condp = [(boolean where) (boolean limit)]
;;    [true true] (format "SELECT * FROM %s WHERE %s LIMIT %s" table where limit)
;;    [true false] (format "SELECT * FROM %s WHERE %s" table where)
    [false true] (format "SELECT TOP %s * FROM %s" limit table)))
