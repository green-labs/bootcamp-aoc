(ns aoc2018-2
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))


;; (defn get-strings-from-input 
;;   [filename] 
;;   (-> filename
;;       io/resource
;;       slurp
;;       str/split-lines))

;;       (abcdef bababac)
;;       -> get-freq ([1] [1 3])
;;       -> 
;; 파트 1;; 주어진 각각의 문자열에서, 같은 문자가 두번 혹은 세번씩 나타난다면 각각을 한번씩 센다.
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

'("abcdef" "bababc")
(->> '("abcdef" "bababc")
     (map get-freq-set))
;; loop recur로 풀어보기
(defn part1-solution [strings]
  (loop [strings strings]
    (let [cur-string (first strings)
          freq-])))
                                 ;; format on save

;; loop-recur & 중간중간 데이터 변환하는 함수 구현하기
;; 
;; 1. 데이터 변경 + 재귀호출
;; 2. 탈출조건
;;
;; (if ??
;;     탈출해라!
;;     재귀호출해라!)

(comment (part1-solution (get-strings-from-input "day2.sample.txt")))

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


;; #################################
;; ###        Refactoring        ###
;; #################################

;; frequencies 사용하기
;; PPAP (parse-process-aggregate-print) 원칙 따르기
;; declarative 한 함수 이름 사용하기