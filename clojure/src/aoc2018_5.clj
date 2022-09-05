(ns aoc2018_5
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))
;; 파트 1
;; 입력: dabAcCaCBAcCcaDA

;; 같은 종류의 소문자와 대문자는 서로 ‘반응‘하여 사라짐. aABb -> ‘’
;; 사라진 자리는 진공이 되기 때문에 다른 문자들이 붙게 되고, 또 그 문자들끼리 반응할 수 있음.  abBA-> aA -> ‘’
;; 바로 옆에 붙어있어야만 서로 반응함. abAB -> abAB (반응 없음)
;; 대문자-대문자, 소문자-소문자는 서로 반응하지 않음. aabAAB-> aabAAB (반응 없음)
;; 예시 dabAcCaCBAcCcaDA => dabCBAcaDA

;; 주어진 input 에서 최종으로 남는 문자열을 리턴하시오.


(defn parse-file-to-str
  "parse file input to string
   input: string file
   output: string"
  [input]
  (->  input
       io/resource
       slurp))

(defn convert-char-case
  "convert character to opposite case of letter
   input: a A
   output: A a"
  [in-char]
  (if (Character/isUpperCase in-char)
    (first (string/lower-case in-char))
    #_(string/lower-case in-char)
    (first (string/upper-case in-char))))

;(convert-char-case \a)
;;.toUpperCase
;(.toUpperCase "a")

(defn is-opposite-case-letter
  "return true if two characters have opposite case to each other
   input: a A
   output: true"
  [char-a char-b] 
  (if (= (abs (- (int char-a) (int char-b))) (int 32))
    false
    true)
)

;;aA->AA-> not=
;;int 연산으로 바꿔보기
;(is-opposite-case-letter \a \a)


(defn react-polymer
  "execute to react polymer fully
   input: aAbdbcCBd
   output: bdd
   "
  [out in]
  (if (empty? in)
    out
    (recur
     (if (or (empty? out)
             (is-opposite-case-letter (peek out) (first in))) ;;last는 훨씬 느림
       (conj out (first in)) ;;push char to stack if there is no reaction
       (pop out)) ;;pop the last char from stack if there is reaction
     (rest in))))

(comment
  (->> "2018_5_sample.txt"
       ;parse
       parse-file-to-str

       ;preprocess
       (react-polymer [])

       ;aggregate
       ;print
       count))

;; 파트 2
;; 주어진 문자열에서 한 유닛 (대문자와 소문자)을 전부 없앤 후 반응시켰을 때, 가장 짧은 문자열의 길이를 리턴하시오.
;; 예를 들어 dabAcCaCBAcCcaDA 에서 a/A를 없애고 모두 반응시키면 dbCBcD가 되고 길이는 6인데 비해,
;; 같은 문자열에서 c/C를 없애고 모두 반응시키면 daDA가 남고 길이가 4이므로 4가 가장 짧은 길이가 됨.
;; O(n)

(defn make-map-removing-unit
  "make a map after removing unit from aA to zZ
   input: dabAcCa
   output: ((d b c C) (d a A c C a) (d a b A a)...)
   "
  [input]
  (for [unit
        (map char (range (int \a) (int \z)))]
    #_(filter #(and
                (not= unit %)
                (not= (char (- (int unit) 32)) %))
              input)

    (string/replace
     (string/replace input (str (char (- (int unit) 32))) "")
     (str unit) ""))
)

;;질문 ! 변수로 받아서 regex 처리가 안됨 .. (string/replace "aAabdfe" (str "a|A") "")
;"dabbA" ->replace (blank로 처리)->"dAbbA"->"dbb"
; (string/replace "aAabdfe" #"(?i)a" "")
; (string/replace "aAabdfe" (str #"(?i)a") "")
;(make-map-removing-unit "dabCdfedfcAdfd")

#_(let [a \A
      b \a
      key [\a \A]]
  
  (string/replace "aAabdfe" (str key) "")
  #_(string/replace "aAabdfe" b "")
  )

(comment
  (->> "2018_5_sample.txt"

       ;parse
       parse-file-to-str

       ;preprocess
       make-map-removing-unit
       (map #(apply str %))
       (map #(count (react-polymer [] %)))

       ;aggregate & print
       (apply min)))