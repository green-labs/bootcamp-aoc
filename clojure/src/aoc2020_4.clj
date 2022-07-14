(ns aoc2020_4
  (:require [clojure.string :as s] [clojure.java.io :as io]))

(def passport-keys {"byr" {:require true}
                    "iyr" {:require true}
                    "eyr" {:require true}
                    "hgt" {:require true}
                    "hcl" {:require true}
                    "ecl" {:require true}
                    "pid" {:require true}
                    "cid" {:require false}})

(defn get-input [] (-> "2020_day4.txt"
                       (io/resource)
                       (slurp)
                       (s/split #"\n\n")))

(defn parsed-input-data [input]
  (->> input
       (map #(s/replace % #"\n" " "))
       (map #(s/split % #" "))
       (map (fn [data] (reduce (fn [acc kv] (let [[k v] (s/split kv #":")] (assoc acc k v))) {} data)))))

(defn filter-by-has-passport-require-key [parsed-input-data]
  (let [passport-require-keys (filter (fn [v] (:require (val v))) passport-keys)]
    (filter (fn [v] (every? v (keys passport-require-keys))) parsed-input-data)))

(comment
  "part1"
  (->> (get-input)
       parsed-input-data
       filter-by-has-passport-require-key
       count))

