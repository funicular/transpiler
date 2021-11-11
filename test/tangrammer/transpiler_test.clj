(ns tangrammer.transpiler-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [tangrammer.dialects.mysql]
   [tangrammer.dialects.postgres]
   [tangrammer.dialects.sqlserver]
   [tangrammer.macro :as m]
   [tangrammer.operators :as op]
   [tangrammer.transpiler :refer :all]))

(deftest mysql-ops
  (testing "and"
    (is (= "1 AND 1" (op/and :mysql ["1" "1"])))
    (is (= "1 AND 2 AND 3" (op/and :mysql ["1" "2" "3"]))))
  (testing "or"
    (is (= "1 OR 1" (op/or :mysql ["1" "1"])))
    (is (= "1 OR 2 OR 3" (op/or :mysql ["1" "2" "3"]))))
  (testing "not"
    (is (= "NOT 1" (op/not :mysql ["1"]))))
  (testing "<"
    (is (= "1 < 2" (op/< :mysql ["1" "2"]))))
  (testing ">"
    (is (= "1 > 2" (op/> :mysql ["1" "2"]))))
  (testing "="
    (is (= "1 = 2" (op/= :mysql ["1" "2"])))
    (is (= "1 IN (2,3)" (op/= :mysql ["1" "2" "3"]))))
  (testing "!="
    (is (= "1 != 2" (op/!= :mysql ["1" "2"])))
    (is (= "1 NOT IN (2,3)" (op/!= :mysql ["1" "2" "3"]))))
  (testing "is-empty"
    (is (= "1 IS NULL" (op/is-empty :mysql ["1"]))))
  (testing "not-empty"
    (is (= "1 IS NOT NULL" (op/not-empty :mysql ["1"])))))

(deftest process-where
  (let [fields {1 "id"
                2 "name"
                3 "date_joined"
                4 "age"}]
    (process-where-clause :mysql fields ["=" ["field" 3] nil] {}))
  )

(deftest generate-sqls
  (let [fields {1 "id"
                2 "name"
                3 "date_joined"
                4 "age"}]
    (is (= (generate-sql "postgres" fields {"where" ["=" ["field" 3] nil]})
           "SELECT * FROM data WHERE date_joined IS NULL"))

    (is (= (generate-sql  "postgres" fields {"where" [">" ["field" 4] 35]})
           "SELECT * FROM data WHERE age > 35"))

    (is (= (generate-sql  "postgres" fields {"where" ["and" ["<" ["field" 1] 5] ["=" ["field" 2] "joe"]]})
           "SELECT * FROM data WHERE id < 5 AND name = 'joe'"))

    (is (= (generate-sql  "postgres" fields {"where" ["or" ["!=" ["field" 3] "2015-11-01"] ["=" ["field" 1] 456]]})
           "SELECT * FROM data WHERE date_joined <> '2015-11-01' OR id = 456"))

    (is (= (generate-sql  "postgres" fields {"where" ["and"
                                                      ["!=" ["field" 3] nil]
                                                      ["or" [">" ["field" 4] 25] ["=" ["field" 2] "Jerry"]]]})
           "SELECT * FROM data WHERE date_joined IS NOT NULL AND (age > 25 OR name = 'Jerry')"))

    (is (= (generate-sql  "postgres" fields {"where" ["=" ["field" 3] 25 26 27]})
           "SELECT * FROM data WHERE date_joined IN (25 26 27)"))

    (is (= (generate-sql  "postgres" fields {"where" ["=" ["field" 2] "cam"]})
           "SELECT * FROM data WHERE name = 'cam'"))

    (is (= (generate-sql  "mysql" fields {"where" ["=" ["field" 2] "cam"] "limit" 10})
           "SELECT * FROM data WHERE name = 'cam' LIMIT 10"))
    (is (= (generate-sql  "postgres" fields {"limit" 20}) "SELECT * FROM data LIMIT 20"))
    (is (= (generate-sql  "sqlserver" fields {"limit" 20}) "SELECT TOP 20 * FROM data")  )))

(deftest macro-circular-dependency
  (is (= :valid
         (m/check-circular-macros {"is_joe" ["=" ["field" 2] "joe"],
                                   "is_adult" [">" ["field" 4] 18],
                                   "Is_old_joe" ["and" ["macro" "is_joe"] ["macro" "is_adult"]]})))
  (is (= :invalid
         (m/check-circular-macros {"is_good"  ["and", ["macro", "is_decent"], [">", ["field", 4], 18]]
                                   "is_decent"  ["and", ["macro", "is_good"], ["<", ["field", 5], 5]]}))))

(deftest check-circular-deps
  (is (= :valid
         (m/check-circular-deps [["D" []]
                                 ["C" []]
                                 ["B" ["D"]]
                                 ["A" ["B" "C"]]])))
  (is (= :valid
         (m/check-circular-deps [["is_joe" []]
                                 ["is_adult" []]
                                 ["Is_old_joe" ["is_joe" "is_adult"]]])))

  (is (= :invalid
         (m/check-circular-deps [["is_joe" []]
                                 ["is_adult" ["Is_old_joe"]]
                                 ["Is_old_joe" ["is_joe" "is_adult"]]])))

  )
