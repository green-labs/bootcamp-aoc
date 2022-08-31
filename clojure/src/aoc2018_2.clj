(ns aoc2018-2
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str]))

;; Get input
(defn read-input
  "Input: a file path
   Output: line-by-line separated array"
  [path] (-> path 
             (io/resource)
             (slurp)
             (clojure.string/split-lines)))

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
(comment
  (defn split-string
    "Input: a string
     Output: a list of letters composing a given string"
    [string re]
    (str/split string re))

  (defn get-frequencies
    "Input: single string
     Output: frequencies of letters in a string"
    [string]
    (-> string
        (split-string #"")
        (frequencies)))

  (defn aggregate-frequency-check
    "Input: The frequency of a letter
     Output: the aggregated array of duplicate check ()"
    [frequency checked-duplicates]
    ;; wip code: 
    ;; map bit-or checked-duplicates [(= frequence 2) (= frequency 3)] 
    (if (= frequency 2)
      (map bit-or checked-duplicates [1 0])
      (if (= frequency 3)
        (map bit-or checked-duplicates [0 1])
        (map bit-or checked-duplicates [0 0]))))
  
  (defn check-frequencies
    "Input: frequencies of letters
     Output: existence of letters with two and three occurence"
    [letter-frequencies]
    (loop [letters (keys letter-frequencies) index 0 checked-duplicates [0 0]]
      (if (< index (count letters))
         (recur letters (inc index) (aggregate-frequency-check (letter-frequencies (nth letters index)) checked-duplicates))
         checked-duplicates
      )
    )
  )
  (-> "abcdef"
      (get-frequencies)
      (check-frequencies))
  (defn aggregate-string-check-result
    "Input: intermediary aggregated string check results and the current check result"
    [] 
    (
     ;;check
    ))

  (defn count-ids [string-sequence]
    "Input: An array of strings
     Output: Two numbers, one is the number of ids which has the same letter twice, and the other number of whom has the same letter three times" 
    (loop [index 0 num-of-duplicates [0 0]] 
      (if (< index (count string-sequence))
        (recur 
         (inc index) 
         ((nth string-sequence index)))
        num-of-duplicates))
    )
    
  (count-ids ["abcdef" "bababc"]))
  

     
    
   
   
  
  


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
