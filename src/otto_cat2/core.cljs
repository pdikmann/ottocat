(ns otto-cat2.core
    (:require [reagent.core :as reagent :refer [atom]]))

;; (enable-console-print!)

;; (println "This text is printed from src/otto-cat2/core.cljs. Go ahead and edit it and see reloading in action.")

(defonce app-state (atom {:text "Hello world!"
                          :objs []
                          :thing {:a 1 :b 2}}))

(defn add-index [hm i]
  (assoc hm :i i))

(defn random-img []
  (let [base-url "https://i.otto.de/i/otto/"
        size-param "?w=200&h=200"
        lower-limit 10000000
        upper-limit 30000000
        random-int (+ (.floor js/Math
                               (* (.random js/Math)
                                  (- upper-limit lower-limit)))
                       lower-limit)
        url (str base-url random-int size-param)
        big-url (str base-url random-int)]
    {:n random-int
     :random true
     :url url
     :big big-url}))

(defn randomize-img [i]
  (swap! app-state
         assoc-in [:objs i]
         (add-index (random-img) i)))

(defn randomize-all-img []
  (swap! app-state
         assoc :objs
         (vec (map #(add-index (random-img) %)
                   (range 32)))))

(defn click-to-randomize []
  [:div.shuffler
   [:a {:href "#" :on-click #'randomize-all-img}
    "shuffle"]])

(defn specific-img [num]
  (let [base-url "https://i.otto.de/i/otto/"
        size-param "?w=200&h=200"
        url (str base-url num size-param)
        big-url (str base-url num)]
    {:n num
     :specific true
     :url url
     :big big-url}))

(defn replace-seq-img [img]
  "increase number of image (in case of load failure)."
  (swap! app-state
         assoc-in [:objs (:i img)]
         (add-index (specific-img (inc (:n img)))
                    (:i img))))

(defn add-seq-img [img]
  "add next sequence image (once previous loaded ok)."
  (let [i (inc (:i img))
        n (inc (:n img))]
    (when (< i 32)
      (swap! app-state
             assoc-in [:objs i]
             (add-index (specific-img n) i)))))

(defn sequence-all-img [n]
  (swap! app-state
         assoc :objs
         [(add-index (specific-img n) 0)]))

(defn sequence-start []
  (js/parseInt (.-value (.getElementById js/document "sequence-start"))))

(defn click-to-sequence []
  (let [num (atom "hey")]
    (fn []
      [:div.sequencer
       [:input {:id "sequence-start"
                :type "number"
                :min 10000000
                :max 20000000}]
       [:a {:href "#"
            :on-click #(sequence-all-img (sequence-start))}
        "start sequence"]])))

;; --------------------------------------------------------------------------------
;; react components
(defn sequence-img [img]
  [:div.frame
   [:a {:href (:big img) :target "_blank"}
    [:img {:src (:url img)
           :on-load #(if (< (.-width (.-target %)) 200)
                       (replace-seq-img img)
                       (add-seq-img img))
           :on-error #(replace-seq-img img)
           }]]
   [:br]
   [:p {:class "tiny-font center"}
    (:n img)]])

(defn auto-reloading-img [img]
  [:div.frame
   [:a {:href (:big img) :target "_blank"}
    [:img {:src (:url img)
           :on-load #(when (< (.-width (.-target %)) 200)
                       (randomize-img (:i img)))
           :on-error #(randomize-img (:i img))
           }]]
   [:br]
   [:p {:class "tiny-font center"}
    (:n img)]])

(defn catalogue []
  [:div
   [click-to-randomize]
   [click-to-sequence]
   [:hr]
   (for [img (:objs @app-state)]
     ^{:key (:n img)}
     (if (:random img)
       [auto-reloading-img img]
       [sequence-img img])
     )
   [:hr]
   [click-to-randomize]
   ])

(reagent/render-component [catalogue]
                          (. js/document (getElementById "app")))

(randomize-all-img)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
