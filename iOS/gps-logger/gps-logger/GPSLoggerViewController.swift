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
    
    var locationManager:CLLocationManager!
    
    var dataCount = 0
    var timer = Timer()
    var outputString = ""
    var startLogging = false
    var logInterval: Int?
    var logFormat: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        determineMyCurrentLocation()
    }
    
    override func viewDidAppear(_ animated: Bool) {

        let tabBar = tabBarController as! BaseTabBarController
        logInterval = tabBar.logInterval
        logFormat = tabBar.logFormat
        logIntervalLabel.text = String(describing: logInterval!)
    }

    @objc func determineMyCurrentLocation() {
        locationManager = CLLocationManager()
        locationManager.delegate = self
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
            startLogging(logFormat: logFormat!, logInterval: logInterval!)
        }
        else {
            startLogging = false
            stopLogging()
        }
    }
    
    func startLogging(logFormat: String, logInterval: Int) {
        outputString = startXMLString(logFormat: logFormat)
        runTimer(timeInterval: Double(logInterval))
    }
    
    func stopLogging() {
        // stop the timer
        timer.invalidate()
        // finish writing the string
        outputString = finishDataWriteToString(stringName: outputString)
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
        inputXMLString = inputXMLString + "\t<gpx version=\"1.1\" creator=\"Xcode\">\n"
        return inputXMLString
    }
    
    // Add location data to XML String
    func writeDataToString(lat: String, lon: String, stringName: String) -> String {
        var inputXMLString = stringName
        inputXMLString = inputXMLString + "\t\t<wpt lat=\"" + lat + "\" lon=\"" + lon + "\"></wpt>\n"
        return inputXMLString
    }
    
    // Finalize the XML String
    func finishDataWriteToString(stringName: String) -> String {
        var inputXMLString = stringName
        inputXMLString = inputXMLString + "\t</gpx>\n" + "</xml>"
        return inputXMLString
    }
    
    @IBAction func emailLogs(_ sender: UIButton) {
        //Check to see the device can send email.
        if MFMailComposeViewController.canSendMail() {
            print("Can send email.")
            
            let mailComposer = MFMailComposeViewController()
            mailComposer.mailComposeDelegate = self
            
            //Set the subject and message of the email
            mailComposer.setToRecipients(["mikev@digital2go.com"])
            mailComposer.setSubject("iOS Device GPS Logs")
            mailComposer.setMessageBody("Please see attached log file.", isHTML: false)
           
            let path = getDocumentsDirectory().appendingPathComponent("log.gpx")
            let fileData = NSData(contentsOf: path)
            mailComposer.addAttachmentData(fileData! as Data, mimeType: "application/gpx", fileName: "log.gpx")

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
        
        latLabel.text = userLocation.coordinate.latitude.description
        lonLabel.text = userLocation.coordinate.longitude.description

        // Only write data to string if user has started logging
        if startLogging == true {
            outputString = writeDataToString(lat: userLocation.coordinate.latitude.description, lon: userLocation.coordinate.longitude.description, stringName: outputString)
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("locationManager failed with error = \(error)")
    }
    
}
