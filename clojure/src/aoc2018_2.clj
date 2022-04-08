(ns aoc2018-2
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))


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
  [string]
  (->> string
       frequencies
       vals
       set))

;; loop recur로 풀어보기

(defn contain?-inc-length ;; 이름 고민해보기
  ;; cnt => [true false] (2가 있는지, 3이 있는지)
  "set에 number가 있으면 input으로 받는 list의 길이를 증가
   input: 2 {2 3} '(1)
   output: '(1 1)"
  [number freq-set lst]
  (let [result lst]
    (if (number freq-set)
      (conj result 1)
      result)))

;; 3 5 6
;; ([true false] [false true] [true true])
;; (apply map vector ([true false] [false true] [true true]))
;; => (map vector [true false] [false true] [true true])

;; = 과 ==의 차이를 저희에게 가르침해주세요...
;; partial을 이용해서
;; is-contain-2?
;; is-contain-3?
(def contain-2?-inc-length
  (partial contain?-inc-length 2))

(def contain-3?-inc-length
  (partial contain?-inc-length 3))



;;thread macro로 refactoring 하기
(defn part1-solution-loop-recur [strings]
  (loop [strings strings
         dup-twice-list '()
         dup-thirce-list '()]
    (let [cur-string (first strings)
          freq-set (get-freq-set cur-string)]
      (if (= (count strings) 0) ;; (zero? (count strings))
        (* (count dup-thirce-list) (count dup-twice-list))
        (recur (next strings)
               (contain-2?-inc-length freq-set dup-twice-list)
               (contain-2?-inc-length freq-set dup-thirce-list))))))


(defn contain-number
  "특정 숫자가 freq-set에 있으면 1 없으면 0 반환
   input: 2 #{2 3}
   output: 1"
  [number freq-set]
  (if (freq-set number)
    1
    0))

(def contain-3
  "3을 가지고 있는지 확인"
  (partial contain-number 3))

(def contain-2
  "2를 가지고 있는지 확인"
  (partial contain-number 2))

(defn get-contains-vector
  "freq-set에서 2, 3을 가지고 있는지 확인후 벡터로 반환
   input: #{2 1}
   output: [1 0]"
  [freq-set]
  (let [dup-twice (contain-2 freq-set)
        dup-thirce (contain-3 freq-set)]
    [dup-twice dup-thirce]))

;; (comment (get-contains-vector #{2 3}))

(defn get-sum-of-contain-vector
  "특정 문자가 2번, 3번 중복된 문자열들의 수를 vector로 반환
   input: [aabbcd aacccd asdfde]
   output: [3 1]"
  [strings]
  (reduce
   (fn [dup-counter freq-set]
     (let [contain-vector (get-contains-vector freq-set)]
       (mapv + dup-counter contain-vector)))
   [0 0]
   (map get-freq-set strings)))

;; (comment (mapv + [0 0] [1 1]))
;; loop-recur -> reduce -> map/filter/reduce (threading macro)
;; 1) reduce 버전
;; 2) map/filter/reduce 버전



;; loop-recur & 중간중간 데이터 변환하는 함수 구현하기
;; 
;; 1. 데이터 변경 + 재귀호출
;; 2. 탈출조건
;;
;; (if ??
;;     탈출해라!
;;     재귀호출해라!)


;; step1: 문자열 쌍 구하기
;; step2: 두 문자열 비교해서 다른 부분만 filtering
;; 
;; (defn get-string-pair
;;   [strings]
;;   (combo/combinations strings 2))


(defn get-string-pairs
  "모든 문자열 쌍을 구하는 함수
   input: [abcd bcdf asdf]
   output: ([abcd bcdf] [abcd asdf] [bcdf asdf])"
  [strings]
  (for [a strings
        b strings
        :when (not= a b)]
    [a b])) ;; good


(defn same?
  "같은 값인지 판단
   input: a a
   output: true"
  [[a b]]
  (= a b))

(defn get-common-letter
  "문자열 쌍에서 공통된 위치이며 같은 문자인 부분만 return하는 함수
   input: aabfdc abbfcc
   output: abfc"
  [str1 str2]
  (->>
   (apply map vector [str1 str2])
   (filter same?)
   (map first) ;; [a a b c c]
   str/join))


(defn diff-length-one?
  "주어진 두 string의 길이의 차이가 1인지 확인하는 함수
   input: abcde abcd
   ouput: true"
  [origin common-letter]
  (= (+ 1 (count common-letter))
     (count origin)))

;; predicate => Boolean 리턴 함수
;; 함수명 뒤에 ?를 붙이는게 컨벤션

(defn get-valid-common-letter
  "두 문자열 쌍이 하나 빼고 모두 같은 것이면 공통부분을 return 하는 함수
   input: abcde abcd
   output: abcd"
  [[str1 str2]]
  (let [common-letter (get-common-letter str1 str2)]
    (when (diff-length-one? str1 common-letter) common-letter)))

;; if / when
(defn part2-solution1
  [strings]
  (->> strings ;; ["" "" ""]
       get-string-pairs ;; [["ababa" "abbab"] ["ababa" "abbab"]]
       (map get-valid-common-letter) ;; [nil nil nil "adbabd" nil nil nil ....]"
       (filter #(not (nil? %)))
       first))

;; docstring에 input/output 명세
;; predicate 함수 만들어보기
;; 정답 구할 수 있는 로직 구현

(comment
  ;; (->> "day2.sample.txt"
  ;;      get-strings-from-input
  ;;      part1-solution-loop-recur
  ;;      )
  (->> "day2.sample.txt"
       get-strings-from-input
       get-sum-of-contain-vector
       (apply *))
  (->> "day2.sample.txt"
       get-strings-from-input
       part2-solution1))






;; #################################
;; ###        Refactoring        ###
;; #################################

;; frequencies 사용하기
;; PPAP (parse-process-aggregate-print) 원칙 따르기
;; declarative 한 함수 이름 사용하기