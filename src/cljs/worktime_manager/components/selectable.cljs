(ns worktime-manager.components.selectable
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.reader :refer [read-string]]))

(defn- check-selected [value owner]
  (when (= value (om/get-state owner :selected-val))
    [:span.glyphicon.glyphicon-ok.selected-year]))

(defn- select! [evt owner callback]
  (let [new-val (read-string (.. evt -target -textContent))
        old-val (om/get-state owner :selected-val)]
    (om/set-state! owner :selected-val new-val)
    (callback new-val old-val)))

(defn dropdown [data owner {:keys [value-key on-change-fn] :as opts}]
  (reify
    om/IInitState
    (init-state [_]
      {:selected-val (last (get data value-key))})
    om/IRenderState
    (render-state [_ {:keys [selected-val]}]
      (let [values (get data value-key)]
        (html [:ul.dropdown-menu
               {:on-click #(select! % owner on-change-fn)}
               (for [value values]
                 [:li
                  [:a value (check-selected value owner)]])])))))
