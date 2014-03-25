(ns worktime-manager.components.editable
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [worktime-manager.utils :refer [format-cell]]))

(defn display [show]
  (if show
    #js {}
    #js {:display "none"}))

(defn editable [data owner {:keys [column end-edit] :as opts}]
  (reify
    om/IInitState
    (init-state [_]
      {:editing false})
    om/IRenderState
    (render-state [_ {:keys [editing]}]
      (let [text (format-cell column data)]
        (html [:td
               [:span {:style (display (not editing))
                       :on-double-click #(om/set-state! owner :editing true)}
                text]
               [:input {:style (display editing)
                        :value text
                        :size 10
                        :on-change #(.. % -target -value)
                        :on-key-press (fn [evt]
                                        (when (== (.-keyCode evt) 13)
                                          (end-edit data (.. evt -target -value))
                                          (om/set-state! owner :editing false)))
                        :on-blur #(om/set-state! owner :editing false)}]])))))
