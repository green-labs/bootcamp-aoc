(ns aoc2018-2
  (:require [clojure.string :as s]))

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

(def sample "resources/day2.txt")

(defn refine-input [input]
  (s/split-lines input))


(defn twice
  "전달받은 문자열에 중복문자 2개가 있는지 확인\n
   input: [ s 'aaabbc' ]\n
   output: true\n
   "
  [s]
  (loop [ss (s/split s #"")
         st #{}]
    (when (seq ss)
      (if (get st (first ss))
        true
        (recur (rest ss) (conj st (first ss)))))))

(defn three-times
  "전달받은 문자열에 중복문자 3개가 있는지 확인하고 해당 문자 return\n
   input: [target-string 'aaabbcc']\n
   output: ['a']
   "
  [target-string]
  (loop [split-string (s/split target-string #"")
         mp           {}
         v            []]
    (if (empty? split-string)
      v
      (let [target (first split-string)
            others (rest split-string)]
        (recur others (merge mp {(target) (inc (get mp (target) 0))})
               (if (= 2 (Integer. (get mp (target) 0)))
                 v
                 (conj v (target))))))))

(defn remove-char
  "전달받은 문자열에 remove-char-list 문자들을 삭제\n
   input: [remove-char-list ['a' 'b'] target-string 'aaabbbccdd']\n
   output: 'ccdd'
   "
  [remove-char-list target-string]
  (loop [remove-char-list remove-char-list
         target-string    target-string]
    (if (empty? remove-char-list)
      target-string
      (recur (rest remove-char-list) (s/replace target-string (first remove-char-list) "")))))

(defn count-change
  "전달받은 문자열에 중복문자 여부에 따라 mp {:two :three} 의 값을 inc\n
   input: [mp {:two 0 :three 0} str 'aabbb']\n
   output: {:two 1 :three 1}
   "
  [mp str]
  (let [[two three] ((juxt :two :three) mp)
        three-words (three-times str)
        twice?      (twice (remove-char three-words str))]
    {:two   (if twice? (inc two) two)
     :three (if (> (count three-words) 0) (inc three) three)}))


(defn part1 []
  (->> (slurp sample)
       (refine-input)
       (reduce count-change {:two   0
                             :three 0})
       (#(* (:two %) (:three %)))))

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


(defn split-string-and-mapping [string]
  (s/split string #""))

(defn just-one-diff?
  "비교문자열 2개중 하나만 다를경우 해당 index를 return.\n
   없거나 2개이상일 경우 return nil\n
   input: [target-a 'aaabb' target-b 'aaacb']\n
   output: 3\n
   "
  [target-a target-b]
  (when (= (count target-a) (count target-b))
    (let [not-equal-list (filter (fn [i] (not (= (get target-a i) (get target-b i)))) (range (count target-a)))]
      (when (= 1 (count not-equal-list)) (first not-equal-list)))))

(defn vec-remove
  "전달받은 string을 vector변환후 pos 위치값 제거해서 return\n
   input [pos 1, string 'abcde']\n
   output ['a' 'c' 'd' 'e']\n
   "
  [pos string]
  (into (subvec (split-string-and-mapping string) 0 pos) (subvec (split-string-and-mapping string) (inc pos))))

(defn get-diff-vector
  "전달받은 target이 list에 하나만 다른값이 있는경우 다른 문자가 제거된값을 return\n
   input: [target 'aaabb' list ['aaabb' 'aaabc']]\n
   output: aaab
   "
  [target list]
  (loop [list list]
    (when (seq list)
      (let [diff? (just-one-diff? target (first list))]
        (if (nil? diff?)
          (recur (rest list))
          (vec-remove diff? target))))))

(defn find-diff [list]
  (loop [target (first list)
         others (rest list)]
    (when (seq others)
      (let [result (get-diff-vector target others)]
        (if (nil? result)
          (recur (first others) (rest others))
          result)))))



(defn part2 []
  (->> (slurp sample)
       (refine-input)
       (find-diff)
       (s/join "")))


(comment
  (twice "aabbc")
  (part1))

(comment
  (just-one-diff? ["a" "a" "b" "b" "c"] ["a" "a" "b" "b" "e"])
  (find-diff (map split-string-and-mapping '("abcde" "fghij" "fguij")))
  (part2))