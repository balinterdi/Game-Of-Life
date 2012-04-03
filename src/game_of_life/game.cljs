(ns ^{:doc "Conway Game of Life"
  :author "Balint Erdi <me@balinterdi.com>"}
  game_of_life.game)

; block is a still life
(def block [[0 0 0 0] [0 1 1 0] [0 1 1 0] [0 0 0 0]])
; blinker is an oscillator
(def blinker [[0 0 0 0 0] [0 0 1 0 0] [0 0 1 0 0] [0 0 1 0 0] [0 0 0 0 0]])

(defn display [life]
  (let [table-size (int (Math/sqrt (count life)))
        table (partition table-size life)]
    (doseq [row table]
      (binding [*out* *err*]
        (let [values (map #(last %) row)]
          (println (apply str (map #(if (zero? %) \- \x) values))))))
    (flush)))

; Every cell is represented as a vector:
; [row, column, value]
; E.g [2, 0, 1] is the 3rd row, 1st column and there is life there
(defn add-coordinates [cells table-size]
  ; table-size = sqrt(count cells)
  (map-indexed (fn [index item] (list (quot index table-size) (rem index table-size) item)) cells))

(defn between? [x a b]
  (and (>= x a) (<= x b)))

(defn next-life [table]
  (map
    (fn [[row col value]]
      (let [neighbors
        (filter
          (fn [[nrow ncol _]]
            (and
              (not (and (= nrow row) (= ncol col)))
              (between? nrow (dec row) (inc row))
              (between? ncol (dec col) (inc col))))
          table)
        living (reduce + (map #(last %) neighbors))
        next-value (cond (< living 2) 0 (> living 3) 0 (= value 1) 1 :else (if (= living 2) 0 1))]
      [row col next-value]))
    table))

;(defn run [seed ui]
;  (js/setTimeout (ui (add-coordinates seed)) 10000))

(defn get-next-life [seed table-size]
  (next-life (add-coordinates seed table-size)))

;(loop [life (add-coordinates seed) ui ui]
;  (ui life)
;  (.log js/console (str "called run"))
;  (js/setTimeout (recur (next-life life) ui) 1000)))

