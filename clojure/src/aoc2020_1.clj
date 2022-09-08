(ns aoc2020_1
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))


;;part 1
(defn parse-file-to-str
  "parse file input to string
   input: string file
   output: string"
  [input]
  (->  input
       io/resource
       slurp
       string/split-lines))


(defn make-sum-multiply
  "make a map of sum and multiply of input
   input: 1721 979 366 299 675 1456 1010 1010
   output: 
   ({:sum 2700, :mul 1684859}
   {:sum 2435, :mul 1425424}
   {:sum 2087, :mul 629886}
   {:sum 1345, :mul 358314}.."
  [input]
  (for [num1 input
        num2 input
        :when (< num1 num2)] ;;not= num1 num2  (< ) nth % (range (count input))
    {:sum (+ num1 num2)
     :mul (* num1 num2)}))

;;찾아보기 ! 100c2 combination library

(comment
  (->> "aoc2020_1_sample.txt"

       ;parse
       parse-file-to-str
       (map #(Integer/parseInt %))

       ;preprocess
       make-sum-multiply

       ;aggregation and print
       (filter #(= (:sum %) 2020)) ;;some, get.. get by value
       ;first
       (map :mul)
  )
)

;;part 2

(defn make-sum-multiply-three
  "make a map of sum and multiply of three numbers input
   input: 1721 979 366 299 675 1456
   output: 
   ({:sum 2700, :mul 1684859}
   {:sum 2435, :mul 1425424}
   {:sum 2087, :mul 629886}
   {:sum 1345, :mul 358314}.."
  [input]
  (for [num1 input
        num2 input
        num3 input
        :when (< num1 num2 num3)] ;중복피하기 조건 (< num1 num2 num3)
    {:sum (+ num1 num2 num3)
     :mul (* num1 num2 num3)}))

(comment
  (->> "aoc2020_1_sample.txt"

       ;parse
       parse-file-to-str
       (map #(Integer/parseInt %))
       sort
       
       ;preprocess
       make-sum-multiply-three

       ;aggregation and print
       (filter #(= (:sum %) 2020))
       ))


;; (ns example.core
;;   (:require [clojure.math.combinatorics :as combo]))


;; ; COMBINATIONS
;; ; all the unique ways of taking t different elements from items
;; (combo/combinations [1 2 3] 2)
;; ;;=> ((1 2) (1 3) (2 3))
;; https://github.com/clojure/math.combinatorics
