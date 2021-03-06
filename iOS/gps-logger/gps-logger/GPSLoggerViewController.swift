//
//  GPSLoggerViewController.swift
//  gps-logger
//
//  Created by Michael Vartanian on 8/19/18.
//  Copyright © 2018 Michael Vartanian. All rights reserved.
//

import UIKit
import CoreLocation
import MessageUI

class GPSLoggerViewController: UIViewController, CLLocationManagerDelegate, MFMailComposeViewControllerDelegate {

    @IBOutlet weak var toggleLoggingButton: UIButton!
    @IBOutlet weak var latLabel: UILabel!
    @IBOutlet weak var lonLabel: UILabel!
    @IBOutlet weak var logIntervalLabel: UILabel!
    @IBOutlet weak var logLabelText: UILabel!
    
    var locationManager:CLLocationManager!
    
    var dataCount = 0
    var timer = Timer()
    var outputString = ""
    var startLogging = false
    var logInterval: Int?
    var logFormat: String?
    var logMethod: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        locationManager = CLLocationManager()
        locationManager.delegate = self
        locationManager.allowsBackgroundLocationUpdates = true
        locationManager.pausesLocationUpdatesAutomatically = false
        locationManager.showsBackgroundLocationIndicator = true
        locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        
        determineMyCurrentLocation()
    }
    
    override func viewDidAppear(_ animated: Bool) {

        let tabBar = tabBarController as! BaseTabBarController
        logInterval = tabBar.logInterval
        logFormat = tabBar.logFormat
        logMethod = tabBar.logMethod
        logIntervalLabel.text = String(describing: logInterval!)
        
        switch logMethod {
            case "Time":
                logLabelText.text = "Log Interval (seconds)"
            case "Distance":
                logLabelText.text = "Log Distance (meters)"
            default:
                print("No state detected")
        }
        
        
    }

    @objc func determineMyCurrentLocation() {

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
            startLogging = true
            startLogging(logMethod: logMethod!, logFormat: logFormat!, logInterval: logInterval!)
        }
        else {
            startLogging = false
            stopLogging(logMethod: logMethod!)
        }
    }
    
    func startLogging(logMethod: String, logFormat: String, logInterval: Int) {
        outputString = startXMLString(logFormat: logFormat)
        
        if (logMethod == "Time") {
            runTimer(timeInterval: Double(logInterval))
        }
        else if (logMethod == "Distance") {
            locationManager.requestAlwaysAuthorization()
            
            locationManager.distanceFilter = CLLocationDistance(logInterval)
            
            if CLLocationManager.locationServicesEnabled() {
                locationManager.startUpdatingLocation()
            }
        }
    }
    
    func stopLogging(logMethod: String) {
        
        if (logMethod == "Time") {
            // stop the timer
            timer.invalidate()
        }
        else if (logMethod == "Distance") {
            locationManager.stopUpdatingLocation()
        }
        // finish writing the string
        outputString = finishDataWriteToString(logFormat: logFormat!, stringName: outputString)
        writeStringToFile(inputString: outputString, logFormat: logFormat!)
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
    
    // Start the XML String
    func startXMLString(logFormat: String) -> String
    {
        var inputXMLString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
        
        switch logFormat {
            case "gpx":
                inputXMLString = inputXMLString + "\t<gpx version=\"1.1\" creator=\"Xcode\">\n"
            case "kml":
                inputXMLString = inputXMLString + "\t<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
                inputXMLString = inputXMLString + "\t\t<Placemark>\n"
                inputXMLString = inputXMLString + "\t\t\t<name>iOS Test Path</name>\n"
                inputXMLString = inputXMLString + "\t\t\t<LineString>\n"
                inputXMLString = inputXMLString + "\t\t\t\t<tessellate>1</tessellate>\n"
                inputXMLString = inputXMLString + "\t\t\t\t<coordinates>\n"
            default:
                print("error selecting logFormat")
        }
        
        return inputXMLString
    }
    
    // Add location data to XML String
    func writeDataToString(logFormat: String, lat: String, lon: String, stringName: String) -> String {
        var inputXMLString = stringName
        var stateStr = ""
        
        let date = Date()
        let formatter = ISO8601DateFormatter()
        formatter.timeZone = TimeZone(abbreviation: "UTC")

        let utcTimeZoneStr = formatter.string(from: date)
        
        let state = UIApplication.shared.applicationState
        if state == .background {
            stateStr = "background"
        }
        else if state == .active {
            stateStr = "active"
        }
        
        switch logFormat {
            case "gpx":
                inputXMLString = inputXMLString + "\t\t<wpt lat=\"" + lat + "\" lon=\"" + lon + "\"></wpt>\n"
                inputXMLString = inputXMLString + "\t\t<time>" + utcTimeZoneStr + "</time>\n"
                inputXMLString = inputXMLString + "\t\t<metadata><keywords>" + stateStr + "</keywords></metadata>\n"
            case "kml":
                inputXMLString = inputXMLString + "\t\t\t\t\t" + lon + "," + lat + "," + "0\n"
            default:
                print("error selecting logFormat")
        }
        
        return inputXMLString
    }
    
    // Finalize the XML String
    func finishDataWriteToString(logFormat: String, stringName: String) -> String {
        var inputXMLString = stringName
        
        switch logFormat {
            case "gpx":
                inputXMLString = inputXMLString + "\t</gpx>\n" + "</xml>"
            case "kml":
                inputXMLString = inputXMLString + "\t\t\t\t</coordinates>\n"
                inputXMLString = inputXMLString + "\t\t\t</LineString>\n"
                inputXMLString = inputXMLString + "\t\t</Placemark>\n"
                inputXMLString = inputXMLString + "\t</kml>\n" + "</xml>"
            default:
                print("error selecting logFormat")
        }
        
        
        return inputXMLString
    }
    
    @IBAction func emailLogs(_ sender: UIButton) {
        //Check to see the device can send email.
        if MFMailComposeViewController.canSendMail() {
            print("Can send email.")
  
            let now = Date()
            let formatter = DateFormatter()
            
            formatter.timeZone = TimeZone.current
            formatter.dateFormat = "yyyyMMddHHmm"
            let dateString = formatter.string(from: now)
            var filename = "log"
            
            if logFormat == "gpx" {
                 filename = filename + dateString + ".gpx"
            }
            else if logFormat == "kml" {
                filename = filename + dateString + ".kml"
            }
            
            let mailComposer = MFMailComposeViewController()
            mailComposer.mailComposeDelegate = self
            
            //Set the subject and message of the email
            mailComposer.setToRecipients(["mikev@digital2go.com"])
            mailComposer.setSubject("iOS Device GPS Logs")
            mailComposer.setMessageBody("Please see attached log file.", isHTML: false)
           
            let path = getDocumentsDirectory().appendingPathComponent("log.gpx")
            let fileData = NSData(contentsOf: path)
            
            if logFormat == "gpx" {
                mailComposer.addAttachmentData(fileData! as Data, mimeType: "application/gpx", fileName: filename)
            }
            else if logFormat == "kml" {
                mailComposer.addAttachmentData(fileData! as Data, mimeType: "application/kml", fileName: filename)
            }
            self.present(mailComposer, animated: true, completion: nil)
        }
    }
    
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        self.dismiss(animated: true, completion: nil)
    }
    
    func writeStringToFile(inputString: String, logFormat: String) {
        // Set the file path
        let path = getDocumentsDirectory().appendingPathComponent("log.gpx")
        
        // Set the contents
        let content = inputString
        
        do {
            // Write content to file
            try content.write(to: path, atomically: false, encoding: String.Encoding.utf8)
        }
        catch let error as NSError {
            print("Error writing contents to file: \(error)")
        }
    }
    
    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0]
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let userLocation:CLLocation = locations[0] as CLLocation
        print("location updated")
        latLabel.text = userLocation.coordinate.latitude.description
        lonLabel.text = userLocation.coordinate.longitude.description

        // Only write data to string if user has started logging
        if startLogging == true {
            outputString = writeDataToString(logFormat: logFormat!, lat: userLocation.coordinate.latitude.description, lon: userLocation.coordinate.longitude.description, stringName: outputString)
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("locationManager failed with error = \(error)")
    }
    
}
