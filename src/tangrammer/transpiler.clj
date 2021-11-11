(ns tangrammer.transpiler
  (:require [clojure.string :as str]
            [clojure.walk :as w]
            [clojure.set :as set]
            [tangrammer.operators  :as op]
            [tangrammer.value  :as value]
            [tangrammer.dialects.mysql]
            [tangrammer.dialects.postgres]
            [tangrammer.dialects.sqlserver]
            [tangrammer.macro :as macro]))

(def dialects #{:sqlserver :postgres :mysql})

(def query-options #{:limit :where})

(declare process-where-clause)

(defn query-fun [dialect fields fun-element args macros]
  (condp = (keyword fun-element)
    :and       (op/and dialect args)
    :or        (op/or dialect args)
    :not       (op/not dialect args)
    :<         (op/< dialect args)
    :>         (op/> dialect args)
    :=         (op/= dialect args)
    :!=        (op/!= dialect args)
    :is-empty  (op/is-empty dialect args)
    :not-empty (op/not-empty dialect args)
    :macro     (get macros (first args))
    :field     (get fields (first args)  :no-field-available)))

(defn query-arg [dialect fields query-element macros]
  (condp = (type query-element)
    nil                           (value/print-nil dialect)
    java.lang.Long                (value/print-long dialect query-element)
    java.lang.String              (value/print-string dialect query-element)
    clojure.lang.PersistentVector (value/print-where dialect (process-where-clause dialect fields query-element macros))))

(defn process-where-clause [dialect fields where-clause & [macros]]
  (when where-clause
    (let [[fun & args] where-clause
          args         (mapv #(query-arg dialect fields % macros) args)]
      (query-fun dialect fields fun args macros))))

(defn generate-sql [dialect fields query & [macros]]
  (let [dialect (keyword dialect)
        query (w/keywordize-keys query)
        macros (reduce (fn [c [k v]]
                           (assoc c k (process-where-clause dialect fields v))) {} macros)]
    (value/print-query dialect "data"
                       (process-where-clause dialect fields (:where query) macros)
                       (:limit query))))
