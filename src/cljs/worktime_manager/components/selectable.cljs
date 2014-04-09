(ns worktime-manager.components.selectable
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.reader :refer [read-string]]
            [cljs.core.async :refer [<! chan alts!] :as async]))

(defn- check-selected [value owner]
  (when (= value (om/get-state owner :selected-val))
    [:span.glyphicon.glyphicon-ok.selected-year]))

(defn- select! [evt owner callback]
  (let [new-val (read-string (.. evt -target -textContent))
        old-val (om/get-state owner :selected-val)]
    (om/set-state! owner :selected-val new-val)
    (callback new-val old-val)))

(defn dropdown [data owner {:keys [value-key on-change-fn init-val] :as opts}]
  (reify
    om/IWillMount
    (will-mount [_]
      (let [broadcast-chan (om/get-shared owner :broadcast-chan)
            txs (chan)]
        (async/tap broadcast-chan txs)
        (go (loop []
              (let [[_ week] (<! txs)]
                (om/set-state! owner :selected-val week))
              (recur)))))
    om/IRenderState
    (render-state [_ {:keys [selected-val]}]
      (let [values (get data value-key)]
        (html [:ul.dropdown-menu
               {:on-click #(select! % owner on-change-fn)}
               (for [value values]
                 [:li
                  [:a value (check-selected value owner)]])])))))
