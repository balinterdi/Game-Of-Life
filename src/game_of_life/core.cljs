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

;; FIXME: this should probably go
(def table-size 5)

(defn draw-cells [life]
  (let [canvas (.getContext (.getElementById js/document "board") "2d")]
    (set! (.-fillStyle canvas) "rgb(0, 0, 0)")
    (doseq [[x, y, living] life]
      ;(.log js/console "x: " x " y: " y " living: " living)
      (if (= 1 living)
        (do
          (. canvas (beginPath))
          (. canvas arc (+ 25 (* x 50)) (+ 25 (* y 50)) 20 0 (* 2 (. js/Math -PI)) true)
          (. canvas (fill)))
        (do
          (set! (.-fillStyle canvas) "rgb(255, 255, 255)")
          (.fillRect canvas (* x 50) (* 50 y) 50 50)))
      (set! (.-fillStyle canvas) "rgb(0, 0, 0)")
      (.strokeRect canvas (* x 50) (* 50 y) 50 50))))

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
      (draw-cells next-life)
      (set! (.-value life-store) (clojure.string/join "," (map (fn [[_, _, living]] living) next-life)))))

(defn ^:export start []
  (draw-cells
    (game/add-coordinates
      (map #(js/parseInt %)
           (clojure.string/split (.-value (.getElementById js/document "current-life")) #","))
      table-size)
    ))
