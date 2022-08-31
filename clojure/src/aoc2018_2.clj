(ns aoc2018-2
  (:require [clojure.string :as string])
  (:require [clojure.set :as set]))


;; 파트 1
;; 주어진 각각의 문자열에서, 같은 문자가 두번 혹은 세번씩 나타난다면 각각을 한번씩 센다.
;; 두번 나타난 문자가 있는 문자열의 수 * 세번 나타난 문자가 있는 문자열의 수를 반환하시오.
;; 예)
;; abcdef 어떤 문자도 두번 혹은 세번 나타나지 않음 -> (두번 나오는 문자열 수: 0, 세번 나오는 문자열 수: 0)
;; bababc 2개의 a, 3개의 b -> (두번 나오는 문자열 수: 1, 세번 나오는 문자열 수: 1)
;; abbcde 2개의 b -> (두번 나오는 문자열 수: 2, 세번 나오는 문자열 수: 1)
;; abcccd 3개의 c -> (두번 나오는 문자열 수: 2, 세번 나오는 문자열 수: 2)
;; aabcdd 2개의 a, 2개의 d 이지만, 한 문자열에서 같은 갯수는 한번만 카운트함 -> (두번 나오는 문자열 수: 3, 세번 나오는 문자열 수: 2)
;; abcdee 2개의 e -> (두번 나오는 문자열 수: 4, 세번 나오는 문자열 수: 2)
;; ababab 3개의 a, 3개의 b 지만 한 문자열에서 같은 갯수는 한번만 카운트함 -> (두번 나오는 문자열 수: 4, 세번 나오는 문자열 수: 3)
;; 답 : 4 * 3 = 12

;;질문: reduce로 두 return값을 가지게 하는 법.. 
;;예를 들어, (reduce (fn [2 3](count-n)) input-file) 해서 loop-recur 처럼 line별로 계산..

(defn parse-input
  "parse file input to string list
   input: string file with line alignment
   output: vetor of strings"
  [input] (->  input
               (slurp)
               (string/split-lines)))

(defn count-character
  "count the number of characters appeared n times
   n: the number the character appeared
   m: the number the characters whihc appeared n times
   {n1 m2, n2 m2, n3 m3..}
   ababacc -> {3 1, 2 2}
   input: line of string
   output: map"
  [input] (-> input
              (frequencies)
              (vals)
              (frequencies)))

(defn count-n-times
  "count the string which has repeated character n times
   [{aaab, ccddd}, 3] -> count strings which has characters repeated 3 times
   -> return 2 
   input: vector of strings, n
   output: count of lines which has repeated character n times"
  [input number]
  (reduce (fn [r x]
            (+ r
               (if (get (count-character x) number)
                 1
                 0)))
          0
          input))


(let [input (parse-input "resources/2018_2_sample.txt")]
    (*
     (count-n-times input 3)
     (count-n-times input 2)))

;{3 0, 2 0}
;; 파트 2
;; 여러개의 문자열 중, 같은 위치에 정확히 하나의 문자가 다른 문자열 쌍에서 같은 부분만을 리턴하시오.
;; 예)
;; abcde
;; fghij
;; klmno
;; pqrst
;; fguij
;; axcye
;; wvxyz

;; 주어진 예시에서 fguij와 fghij는 같은 위치 (2번째 인덱스)에 정확히 한 문자 (u와 h)가 다름. 따라서 같은 부분인 fgij를 리턴하면 됨.
;;ppap

(defn make-substr
  "make substring after deleting nth index
   [abcd, 1] -> acd 
   input: string, index
   output: string without the character which was at the nth index
   "
  [index input]
  (str
   (subs input 0 index)
   (subs input (+ index 1))))

(defn make-substr-map
  "make substring after deleting nth index using map
   input: vector of strings
   output: vector of strings after deleting nth index"
  [index input]
  (map (partial make-substr index) input))

(defn find-similar-str
  "find similar string after deleting nth index character
   input: vector of strs, nth index
   output: if true, similar str, or nil"
  [input index]
  (->> input
       (make-substr-map index)
       (frequencies)
       (set/map-invert)
       (#(get % 2))))


(defn find-similar-str-file
  "find simliar str after deleting nth index with loop-recur
   input: file with strings
   output: similar str"
  [input-file]
  (let [input (parse-input input-file)]
    (loop [index 0
          ]
      (let [ans (find-similar-str input index)]
        (if (not(nil? ans))
          ans
          (recur (inc index)
          ))))))

(find-similar-str-file "resources/2018_2_sample.txt")

;; #################################
;; ###        Refactoring        ###
;; #################################
