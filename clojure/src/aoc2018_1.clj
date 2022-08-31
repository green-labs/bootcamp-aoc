(ns aoc2018-1
  (:require [clojure.string :as string]))

;; 파트 1
;; 주어진 입력의 모든 숫자를 더하시오.
;; 예) +10 -2 -5 +1 이 입력일 경우 4를 출력

;; parse input file to int vector and utilize "apply" to sum
(apply + (map #(Integer/parseInt %) 
             (string/split-lines 
             (slurp "resources/2018_1_sample.txt"))))


;; 파트 2
;; 주어진 입력의 숫자를 더할 때 마다 나오는 숫자 중, 처음으로 두번 나오는 숫자를 리턴하시오.
;; 예) +3, +3, +4, -2, -4 는 10이 처음으로 두번 나오는 숫자임.
;; 0 -> 3 (+3) -> 6 (+3) -> 10(+4) -> 8(-2) -> 4(-4) -> 7(+3) -> 10(+3) -> ...




(def infile 
  "parse input file to int vector
   input: file of int list lined by enter
   output: int vector
   "
  (map #(Integer/parseInt %)
               (string/split-lines
                (slurp "resources/2018_1_sample.txt"))))


(defn check-sum 
  "return the partial sum which showed twice
   using loop [vector partial-sum set-of-sum] & recur 
   input: int vector
   output: int value stands for the partial sum
   "
  [vals]
  (loop [v (rest vals)
         tempsum (first vals)
         setofsum #{}]
         ;(prn "initi "v)
    (if (contains? setofsum tempsum)
      tempsum
      (let [nv (if (empty? v) vals v)]
        ;(prn nv v)
        (recur (rest nv)
               (+ tempsum (first nv))
               (conj setofsum tempsum))))))


(check-sum infile)
;(prn infile sum)
