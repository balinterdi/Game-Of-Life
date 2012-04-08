; TODO: check out monet to replace canvas code
; http://www.chris-granger.com/projects/cljs/
(ns game-of-life.core
  (:require [clojure.browser.repl :as repl]
            [domina :as domina]
            [domina.css :as css]
            [game_of_life.game :as game]))
;
(defn ^:export greet [n]
  (js/alert "I am an evil side-effect"))

(defn ^:export set-canvas []
  (domina/append! (css/sel "#board") "<div>Hello World!</div>"))

;; TODO: this should probably go
(def table-size 5)

(defn draw-circle [canvas x y radius color]
  (do
    (set! (.-fillStyle canvas) color)
    (. canvas (beginPath))
    (. canvas arc x y radius 0 (* 2 (. js/Math -PI)) true)
    (. canvas (fill))))

(defn draw-cells [life board-id cell-size]
  ;; TODO: draw only the living cells here
  ;; and draw the board as lines
  (let [canvas (.getContext (.getElementById js/document board-id) "2d")
        cell-center (quot cell-size 2)
        life-radius (- (quot cell-size 2) (quot cell-size 10))]
    (doseq [[row, col, living] life]
      ;(.log js/console "row: " row " col: " col " living: " living)
      (if (= 1 living)
        (draw-circle canvas (+ cell-center (* col cell-size)) (+ cell-center (* row cell-size)) life-radius "rgb(0, 0, 0)")
        (draw-circle canvas (+ cell-center (* col cell-size)) (+ cell-center (* row cell-size)) life-radius "rgb(255, 255, 255)"))
      (set! (.-fillStyle canvas) "rgb(0, 0, 0)")
      (.strokeRect canvas (* row cell-size) (* col cell-size) cell-size cell-size))))

(defn store-life [life]
  (let [life-store (.getElementById js/document "current-life")]
    (set! (.-value life-store) (clojure.string/join "," life))))

(defn ^:export random-life []
  (do
    (store-life (repeatedly (* table-size table-size) #(rand-int 2)))
    (start)))

(defn ^:export next-life []
  (let [life-store (.getElementById js/document "current-life")
        current-life (.-value life-store)
        next-life (game/get-next-life (map #(js/parseInt %) (clojure.string/split current-life #",")) table-size)]
      (draw-cells next-life "board" 50)
      (set! (.-value life-store) (clojure.string/join "," (map (fn [[_, _, living]] living) next-life)))))

(defn ^:export draw-boards []
  (let [convert-to-board
         (fn [field-id]
            (game/add-coordinates
              (map #(js/parseInt %)
                   (clojure.string/split (.-value (.getElementById js/document field-id)) #","))
              table-size))]
    (draw-cells (convert-to-board "current-life") "board" 50)
    (draw-cells (convert-to-board "r-pentomino") "r-pentomino-board" 10)
    (draw-cells (convert-to-board "blinker") "blinker-board" 10)))
