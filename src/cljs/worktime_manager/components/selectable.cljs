(ns worktime-manager.components.selectable
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.reader :refer [read-string]]
            [cljs.core.async :refer [<! chan alts!] :as async]))

(defn- check-selected [option selected]
  (when (= option selected)
    [:span.glyphicon.glyphicon-ok.selected-year]))

(defn- select! [evt old-val callback]
  (let [new-val (read-string (.. evt -target -textContent))]
    (callback new-val old-val)))

(defn dropdown [selected owner {:keys [on-change-fn] :as opts}]
  (reify
    om/IRenderState
    (render-state [_ {:keys [options]}]
      (html [:ul.dropdown-menu
             {:on-click #(select! % selected on-change-fn)}
             (for [option options]
               [:li
                [:a option (check-selected option selected)]])]))))
