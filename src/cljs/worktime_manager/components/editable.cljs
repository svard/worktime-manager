(ns worktime-manager.components.editable
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
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
        (dom/td nil
          (dom/span
            #js {:style (display (not editing))
                 :onDoubleClick #(om/set-state! owner :editing true)}
            text)
          (dom/input
            #js {:style (display editing)
                 :value text
                 :size 10
                 :onChange #(.. % -target -value)
                 :onKeyPress (fn [evt]
                               (when (== (.-keyCode evt) 13)
                                 (end-edit data (.. evt -target -value))
                                 (om/set-state! owner :editing false)))
                 :onBlur #(om/set-state! owner :editing false)}))))))
