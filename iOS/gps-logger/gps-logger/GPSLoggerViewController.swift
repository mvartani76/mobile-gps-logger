//
//  GPSLoggerViewController.swift
//  gps-logger
//
//  Created by Michael Vartanian on 8/19/18.
//  Copyright © 2018 Michael Vartanian. All rights reserved.
//

import UIKit
import CoreLocation

class GPSLoggerViewController: UIViewController, CLLocationManagerDelegate {

    @IBOutlet weak var toggleLoggingButton: UIButton!
    @IBOutlet weak var latLabel: UILabel!
    @IBOutlet weak var lonLabel: UILabel!
    
    var locationManager:CLLocationManager!
    var dataCount = 0
    var timer = Timer()
    var outputString = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        determineMyCurrentLocation()
    }

    @objc func determineMyCurrentLocation() {
        locationManager = CLLocationManager()
        locationManager.delegate = self
        //locationManager.distanceFilter = LocationManagerDistanceFilter
        locationManager.requestAlwaysAuthorization()
        
        if CLLocationManager.locationServicesEnabled() {
            locationManager.requestLocation()
        }
    }

    @IBAction func toggleLogging(_ sender: UIButton) {

        // Toggle the state of the button
        toggleLoggingButton.isSelected = toggleLoggingButton.isSelected ? false : true
        
        updateToggleButton(button: toggleLoggingButton,
                           stateOneColor: UIColor.gray,
                           stateOneText: "Stop Logging",
                           stateTwoColor: UIColor.blue,
                           stateTwoText: "Start Logging")
        
        if toggleLoggingButton.isSelected {
            startLogging(logFormat: "GPX", logInterval: 5)
        }
        else {
            stopLogging()
        }
    }
    
    func startLogging(logFormat: String, logInterval: Int) {
        outputString = startXMLString(logFormat: "GPX")
        runTimer(timeInterval: Double(logInterval))
    }
    func stopLogging() {
        // stop the timer
        timer.invalidate()
        // finish writing the string
        outputString = finishDataWriteToString(stringName: outputString)
    }
    
    func updateToggleButton(button: UIButton, stateOneColor: UIColor, stateOneText: String, stateTwoColor: UIColor, stateTwoText: String) {
        
        // Update button title/background based on state
        if button.isSelected {
            button.setTitle(stateOneText, for: .normal)
            button.backgroundColor = stateOneColor
        }
        else {
            button.setTitle(stateTwoText, for: .normal)
            button.backgroundColor = stateTwoColor
        }
    }
    
    func runTimer(timeInterval: Double) {
        timer = Timer.scheduledTimer(timeInterval: timeInterval, target: self, selector: #selector(GPSLoggerViewController.determineMyCurrentLocation), userInfo: nil, repeats: true)
    }
    
    func startXMLString(logFormat: String) -> String
    {
        var inputXMLString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
        inputXMLString = inputXMLString + "<gpx version=\"1.1\" creator=\"Xcode\">\n"
        print(inputXMLString)
        return inputXMLString
    }
    
    func writeDataToString(lat: String, lon: String, stringName: String) -> String {
        var inputXMLString = stringName
        inputXMLString = inputXMLString + "\t<wpt lat=\"" + lat + " long=\"" + lon + "></wpt>\n"
        return inputXMLString
    }
    
    func finishDataWriteToString(stringName: String) -> String {
        var inputXMLString = stringName
        inputXMLString = inputXMLString + "</gpx>"
        return inputXMLString
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let userLocation:CLLocation = locations[0] as CLLocation
        
        let now = Date()
        let formatter = DateFormatter()
        
        formatter.timeZone = TimeZone.current
        formatter.dateFormat = "yyyy-MM-dd HH:mm"
        
        let dateString = formatter.string(from: now)
        
        print("user latitude = \(userLocation.coordinate.latitude)")
        print("user longitude = \(userLocation.coordinate.longitude)")
        
        latLabel.text = userLocation.coordinate.latitude.description
        lonLabel.text = userLocation.coordinate.longitude.description
        //dataCountLabel.text = dataCount.description
        print(outputString)
        outputString = writeDataToString(lat: userLocation.coordinate.latitude.description, lon: userLocation.coordinate.longitude.description, stringName: outputString)
        print(outputString)
        
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("locationManager failed with error = \(error)")
    }
    
}
