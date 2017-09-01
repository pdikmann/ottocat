(ns otto-cat2.core
    (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "This text is printed from src/otto-cat2/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"
                          :objs []
                          :thing {:a 1 :b 2}}))

(defn new-objs [n]
  (let [base-url "https://i.otto.de/i/otto/"
        size-param "?w=200&h=200"
        lower-limit 10000
        upper-limit 20000000
        random-ints (for [x (range n)]
                      (+ (.floor js/Math
                                 (* (.random js/Math)
                                    (- upper-limit lower-limit)))
                         lower-limit))
        urls (map #(str base-url % size-param)
                  random-ints)
        big-urls (map #(str base-url %)
                      random-ints)
        maps (map (fn [i url big] {:n i :url url :big big})
                  random-ints
                  urls
                  big-urls)]
    maps))

(defn hello-world []
  [:div
   [:a {:href "#"
        :onClick (fn []
                   (swap! app-state assoc-in [:objs] (new-objs 50))
                   (println "shiiiiit")
                   (println "what" @app-state)
                   )}
    "click to shuffle"]
   [:hr]
   (for [img (:objs @app-state)]
     ^{:key (:n img)}
     [:div {:class "inline"}
      [:a {:href (:big img) :target "_blank"}
       [:img {:src (:url img)}]]
      [:br]
      [:p {:class "tiny-font center"}
       (:n img)]]
     )
   [:hr]
   ])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
