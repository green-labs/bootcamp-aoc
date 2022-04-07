(ns aoc2018-2
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.math.combinatorics :as combo]))


(defn get-strings-from-input
  [filename]
  (-> filename
      io/resource
      slurp
      str/split-lines))

;; 이걸 왜 쓸까요? 가독성을 높이기 위해서 -> 함수를 중첩된 가장 깊숙한 곳 부터 읽기 vs 위에서 아래로 읽기
;; https://rescript-lang.org/docs/manual/latest/pipe

;; 인풋 -> freq list -> ... -> 정답

;; 각각의 스텝들을 함수로 분리하기

;;
;; 개발자가 하는일 = 데이터를 다루는 것 by Alex Miller
;;
;; "sssssss" => f: get-freq-set => ${1 2 3}
;; ${1 2 3} => g: xxxx =>  ???
;; f->g->h = 답이나오겠네?
;; 28346
;;

;;
;; map을 쓰면 왜좋냐? -> 함수형 프로그래밍에서 함수를 재사용할 수 있게 해줌.
;;
;; f: get-freq-set: String -> Set
;; [String] => [Set]이 되는 함수는 우리는 가지고 있는게 없는데, map이 이걸 가능하게 해줌 
;; (심화: 이거를 lift라고 함. Functor)



(defn get-freq-set
  "문자열의 특정 문자가 몇번 등장~~
  input: abcdef
  output: #{1}"
  [string] (->> string
                frequencies
                vals
                set))




;; loop recur로 풀어보기

(defn is-contain
  "set에 number가 있으면 input으로 받는 count를 1 증가 시켜주는 함수
   input: 2 {2 3} 1
   output: 2"
  [number freq-set cnt]
  (if (freq-set number)
    (+ cnt 1)
    cnt))

;;thread macro로 refactoring 하기
(defn part1-solution-loop-recur [strings]
  (loop [strings strings
         dup-twice-cnt 0
         dup-thirce-cnt 0]
    (let [cur-string (first strings)
          freq-set (get-freq-set cur-string)]
      (if (== (count strings) 0)
        (* dup-thirce-cnt dup-twice-cnt)
        (recur (next strings) (is-contain 2 freq-set dup-twice-cnt) (is-contain 3 freq-set dup-thirce-cnt))))))

;; loop-recur & 중간중간 데이터 변환하는 함수 구현하기
;; 
;; 1. 데이터 변경 + 재귀호출
;; 2. 탈출조건
;;
;; (if ??
;;     탈출해라!
;;     재귀호출해라!)




;;step1 모든 순서쌍 구하기
;;step2 같은 위치에 있지 않은 것만 filter
;;step3 filter된 리스트의 길이가 1인지 확인
;;step4 길이가 1인 나머지 부분만 return

(defn part2-solution1
  [strings]
  (->> strings
       comb/combinations
       count))

(comment
  (->> "day2.sample.txt"
       get-strings-from-input
       part1-solution-loop-recur)
  ;; (->> "day2.sample.txt"
  ;;      get-strings-from-input
  ;;      part2-solution1)
  )




;; #################################
;; ###        Refactoring        ###
;; #################################

;; frequencies 사용하기
;; PPAP (parse-process-aggregate-print) 원칙 따르기
;; declarative 한 함수 이름 사용하기