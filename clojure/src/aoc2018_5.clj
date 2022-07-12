(ns aoc2018_5
  (:require [clojure.string :as s] [clojure.java.io :as io]))

(defn get-input [] (-> "day5.txt"
                       (io/resource)
                       (slurp)))

(defn char-range
  "시작 char 부터 끝 char 까지의 char 목록 return
   input a c
   output (a b c)
   "
  [start end]
  (map char (range (int start) (inc (int end)))))

(defn make-alphabet-combination-regex
  "전달받은 알파벳들로 (소문자대문자|대문자소문자) 정규식 표현을 return
   input (a b c d e)
   output #`(aA|Aa|bB|Bb|cC|Cc|dD|Dd|eE|Ee)`
   "
  [list]
  (re-pattern (str "("
                   (s/join "|" (reduce (fn [acc v] (conj acc (str v (s/upper-case v)) (str (s/upper-case v) v))) [] list))
                   ")")))

(defn get-regex-by-alphabet
  "전달받은 두 알파벳 범위의 대소문자 조합 정규식 return
   알파벳 하나만 전달시 해당 알파벳으로만 조합하여 reutrn
   input `a` `c`
   output #`(aA|Aa|bB|Bb|cC|Cc)`
   input `a`
   output #`(aA|Aa)`
   "
  ([alphabet]
   (get-regex-by-alphabet alphabet alphabet))
  ([start-alphabet end-alphabet]
   (let [list (char-range start-alphabet end-alphabet)]
     (make-alphabet-combination-regex list))))

(defn remove-by-regex
  "전달받은 문자열을 전달받은 정규식으로 더이상 치환할게 없을때까지 치환후 최종결과값 return"
  [string regex]
  (loop [replaced-string (s/replace string regex "")
         before-string   string]
    (if (= before-string replaced-string)
      replaced-string
      (recur (s/replace replaced-string regex "") replaced-string))))

(defn get-upper-case-char [c]
  (char (- (int c) 32)))

(defn remove-by-alphabet
  "문자열에있는 전달받은 alphabet 의 대소문자들을 지운다.
   input `ababcc` `a`
   output `bbcc`
   "
  [string alphabet]
  (s/join "" (remove #{alphabet (get-upper-case-char alphabet)} string)))

(defn part1 [input-string]
  (-> input-string
      (remove-by-regex (get-regex-by-alphabet \a \z))
      count))

(defn part2 [input-string]
  (->> (char-range \a \z)
       (map #(remove-by-alphabet input-string %))
       (map part1)
       (apply min)))

(defn reactive-condition?
  [char1 char2]
  (and (not= char1 char2)
       (= (s/upper-case char1)
          (s/upper-case char2))))

(defn reaction-polymer [polymer-string]
  (reduce (fn [polymer char]
            (if (seq polymer)
              (if (reactive-condition? (peek polymer) char)
                (pop polymer)
                (conj polymer char))
              (conj polymer char)))
          [] polymer-string))

(defn part1-refact [input-string]
  (-> input-string
      (reaction-polymer)
      count))

(defn part2-refact [input-string]
  (->> (for [polymer (char-range \a \z)]
         (part1-refact (remove-by-alphabet input-string polymer)))
       (apply min)))

(comment
  (time (part1 (get-input)))
  (time (part2 (get-input)))
  (time (part1-refact (get-input)))
  (time (part2-refact (get-input))))

