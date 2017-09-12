(ns otto-cat2.core
    (:require [reagent.core :as reagent :refer [atom]]))

;; (enable-console-print!)

;; (println "This text is printed from src/otto-cat2/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"
                          :objs []
                          :thing {:a 1 :b 2}}))

(defn random-img []
  (let [base-url "https://i.otto.de/i/otto/"
        size-param "?w=200&h=200"
        lower-limit 10000
        upper-limit 20000000
        random-int (+ (.floor js/Math
                               (* (.random js/Math)
                                  (- upper-limit lower-limit)))
                       lower-limit)
        url (str base-url random-int size-param)
        big-url (str base-url random-int)]
    {:n random-int
     :url url
     :big big-url}))

(defn add-index [hm i]
  (assoc hm :i i))

(defn swap-img [i]
  (swap! app-state
         assoc-in [:objs i]
         (add-index (random-img) i)))

(defn swap-all-img []
  (swap! app-state
         assoc :objs
         (vec (map #(add-index (random-img) %)
                   (range 32)))))

(defn click-to-shuffle []
  [:div.shuffler
   [:a {:href "#" :onClick #'swap-all-img}
    "shuffle"]])

(defn auto-reloading-img [img]
  [:div {:class "inline"}
   [:a {:href (:big img) :target "_blank"}
    [:img {:src (:url img)
           :on-load #(when (< (.-width (.-target %)) 200)
                       (swap-img (:i img)))
           :on-error #(swap-img (:i img))
           }]]
   [:br]
   [:p {:class "tiny-font center"}
    (:n img)]])

(defn catalogue []
  [:div
   [click-to-shuffle]
   [:hr]
   (for [img (:objs @app-state)]
     ^{:key (:n img)}
     [auto-reloading-img img]
     )
   [:hr]
   [click-to-shuffle]
   ])

(reagent/render-component [catalogue]
                          (. js/document (getElementById "app")))

(swap-all-img)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
