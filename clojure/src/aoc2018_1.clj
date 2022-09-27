(ns aoc2018-1)

;; 파트 1
;; 주어진 입력의 모든 숫자를 더하시오.
;; 예) +10 -2 -5 +1 이 입력일 경우 4를 출력


;; 파트 2
;; 주어진 입력의 숫자를 더할 때 마다 나오는 숫자 중, 처음으로 두번 나오는 숫자를 리턴하시오.
;; 예) +3, +3, +4, -2, -4 는 10이 처음으로 두번 나오는 숫자임.
;; 0 -> 3 (+3) -> 6 (+3) -> 10(+4) -> 8(-2) -> 4(-4) -> 7(+3) -> 10(+3) -> ...



(defn parse-input [input]
  (map #(read-string %) (clojure.string/split-lines (slurp input))))

(defn plus-with-reaches-value [input reaches now-value]
  (let [plus-value (+ (if (empty? input) 0 (first input)) now-value)]
    (if (contains? reaches plus-value)
      plus-value
      (recur (conj (vec (rest input)) (first input)) (set (conj reaches plus-value)) plus-value)
      ))
  )

(comment
  (parse-input "resources/day1.txt")
  (reduce + (parse-input "resources/day1.txt"))
  (plus-with-reaches-value (vec (parse-input "resources/day1.txt")) #{} 0)
  )


