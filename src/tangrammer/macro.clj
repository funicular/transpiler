(ns tangrammer.macro
  (:require [clojure.set :as set]))

(defn check-circular-deps
  "if a dependency doesn't exist as dependant then returns also :invalid"
  [data]
  (loop [no-deps (set (map first (filter (comp empty? last) data)))
         data (filter (comp seq last) data)]
    (if (seq data)
      (let [valids (into no-deps (map first (filter #(empty? (set/difference (set (last %)) no-deps)) data)))]
        (if (= (count valids ) (count no-deps))
          :invalid
          (recur valids (filter (comp not (partial contains? valids) first) data))))
      :valid)))

(defn macro-deps [where-query]
  (mapv last (filter (comp (partial = "macro") first)
                     (partition 2 1 (flatten where-query)))))

(defn check-circular-macros [macro-graph]
  (->> (seq macro-graph)
       (mapv (fn [[k v]] [k (macro-deps v)]))
       (check-circular-deps)))
