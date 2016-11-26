(ns api-sim.endpoint-test
  (:require [clojure.test :refer :all]
            [api-sim.endpoint :refer :all]))

(deftest test-active?
  (is (= 1 (count (filter active? [{:active true} {:active false}]))))
  (is (= 2 (count (filter active? [{:active true} {:active true}]))))
  (is (= 0 (count (filter active? [{:active false} {:active false}]))))
  (is (= 1 (count (filter active? [{} {:active true}])))))
