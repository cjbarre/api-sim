(ns api-sim.endpoint)

(defn active?
  [endpoint]
  (let [active-flag (:active endpoint)]
    (= true active-flag)))
