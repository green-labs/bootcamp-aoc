(ns day3
  (:require [clojure.java.io :as io]))

(def input (-> "day3.sample.txt"
               (io/resource)
               (slurp)
               (clojure.string/split-lines)))

(count input)
