(ns aoc2018-1
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

;; 파트 1
;; 주어진 입력의 모든 숫자를 더하시오.
;; 예) +10 -2 -5 +1 이 입력일 경우 4를 출력

(defn get-numbers-from-input [filename]
  (let [input (io/resource filename)]
    (map read-string
         (str/split (slurp input) #"\n"))))

(defn part1-solution [numbers] (reduce + numbers))

;; (comment (part1-solution (get-numbers-from-input "day1.sample.txt")))


;; 파트 2
;; 주어진 입력의 숫자를 더할 때 마다 나오는 숫자 중, 처음으로 두번 나오는 숫자를 리턴하시오.
;; 예) +3, +3, +4, -2, -4 는 10이 처음으로 두번 나오는 숫자임.
;; 0 -> 3 (+3) -> 6 (+3) -> 10(+4) -> 8(-2) -> 4(-4) -> 7(+3) -> 10(+3) -> ...

(defn part2-solution1 [numbers]
  (loop [cycle-numbers (cycle numbers)
         sum 0
         results #{0}]
    (let [sum (+ sum (first cycle-numbers))]
      (if (results sum)
        sum
        (recur (next cycle-numbers) sum (conj results sum))))))



;; reduce/reduced -> 한번 도전!!
(comment
  (part1-solution (get-numbers-from-input "day1.sample.txt"))
  (part2-solution1 (get-numbers-from-input "day1.sample.txt")))



;; #################################
;; ###        Refactoring        ###
;; #################################

;; cycle 혹은 reductions 사용하기
;; loop-recur 시 let으로 바인딩하기