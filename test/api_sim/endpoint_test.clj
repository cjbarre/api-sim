(ns api-sim.endpoint-test
  (:require [clojure.test :refer :all]
            [api-sim.endpoint :refer :all]))

(deftest test-make-route
  (is (= ["user/" :name] (make-route "user/{name}")))

  (is (= ["user/" :id "/name"] (make-route "user/{id}/name")))

  (is (= ["user/" :id :name "/beep"] (make-route "user/{id}/{name}/beep")))

  (is (= ["user/" :id :address :city "/beep"] (make-route "user/{id}/{address}/{city}/beep")))

  (is (= ["user/" :id "/address/city"] (make-route "user/{id}/address/city"))))
