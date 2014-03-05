(ns worktime-manager.test.utils
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest testing)])
  (:require [cemerick.cljs.test :as t]
            [worktime-manager.utils :as utils])
  (:import [goog.date DateTime]))

(deftest str->date
  (testing "should create a date object from a string"
    (let [date (utils/str->date "2014-02-03T17:10:04.000+01:00")]
      (is (instance? DateTime date))
      (is (= (.getYear date) 2014))
      (is (= (.getMonth date) 1))
      (is (= (.getDate date) 3))
      (is (= (.getHours date) 17))
      (is (= (.getMinutes date) 10))
      (is (= (.getSeconds date) 4)))))

(deftest display-date
  (testing "should format a date string"
    (let [date (DateTime.)]
      (.setTime date 1387034721000)
      (is (= (utils/display-date date) "2013-12-14"))))

  (testing "should add leading zeros to month and day"
    (let [date (DateTime.)]
      (.setTime date 1391411032000)
      (is (= (utils/display-date date) "2014-02-03")))))

(deftest display-time
  (testing "should format a time string"
    (let [date (DateTime.)]
      (.setTime date 1391527521000)
      (is (= (utils/display-time date) "16:25:21"))))

  (testing "should add leading zeros to hour, minute and second"
    (let [date (DateTime.)]
      (.setTime date 1391411042000)
      (is (= (utils/display-time date) "08:04:02")))))

(deftest seconds->hours
  (testing "should convert seconds to hours"
    (is (= (utils/seconds->hours 27451) 7.63))
    (is (= (utils/seconds->hours 0) 0))
    (is (= (utils/seconds->hours -5) 0))))

(deftest get-week-number
  (testing "should return week number from a date"
    (let [date (DateTime.)]
      (.setTime date 1394050965504)
      (is (= (utils/get-week-number date) 10)))))

(deftest diff-dates
  (testing "should return the diff in seconds between two dates"
    (let [d1 (DateTime.)
          d2 (DateTime.)]
      (.setTime d1 1394054803000)
      (.setTime d2 1394051203000)
      (is (= (utils/diff-dates d1 d2) 3600))))

  (testing "should return 0 as diff between two equal dates"
    (let [d1 (DateTime.)
          d2 (DateTime.)]
      (.setTime d1 1394054803000)
      (.setTime d2 1394054803000)
      (is (= (utils/diff-dates d1 d2) 0)))))

(deftest disabled
  (testing "should add disabled to a class"
    (is (= (utils/disabled true "test") "test disabled")))

  (testing "should not add disabled to a class"
    (is (= (utils/disabled false "test") "test"))))
