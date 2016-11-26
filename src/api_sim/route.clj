(ns api-sim.route)

(defn- split-route
  [route-string]
  (clojure.string/split route-string #"\{|\}"))

(defn- keywordify-route
  [route-coll]
  (doall (map #(if-not (re-find #"/" %) (keyword %) %) route-coll)))

(defn make-route
  [endpoint-name]
  (->> endpoint-name
       split-route
       (filter (partial not= "/"))
       keywordify-route
       (into [])))
