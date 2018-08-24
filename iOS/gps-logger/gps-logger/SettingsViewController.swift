//
//  SettingsViewController.swift
//  gps-logger
//
//  Created by Michael Vartanian on 8/19/18.
//  Copyright © 2018 Michael Vartanian. All rights reserved.
//

import UIKit

class SettingsViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {

    
    @IBOutlet weak var logFormatPicker: UIPickerView!
    @IBOutlet weak var logIntervalLabel: UILabel!
    @IBOutlet weak var logIntervalSlider: UISlider!
    @IBOutlet weak var segmentControlButton: UISegmentedControl!
    @IBOutlet weak var logLabelText: UILabel!
    
    var logFormatDataSource = ["gpx", "kml"]
    
    var logInterval = 300
    var logFormat = "gpx"
    var logMethod = "Time"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        logFormatPicker.delegate = self
        logFormatPicker.dataSource = self
        
        let currentSliderValue = Int(logIntervalSlider.value)
        logInterval = currentSliderValue
        logIntervalLabel.text = "\(currentSliderValue)"
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        let tabBar = tabBarController as! BaseTabBarController
        
        tabBar.logInterval = logInterval
        tabBar.logFormat = logFormat
        tabBar.logMethod = logMethod
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return logFormatDataSource.count
    }

    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return logFormatDataSource[row] as String
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        print(logFormatDataSource[row])
        logFormat = logFormatDataSource[row]
    }
    
    @IBAction func sliderValueChanged(_ sender: UISlider) {
        let currentValue = Int(sender.value)
        logIntervalLabel.text = "\(currentValue)"
        logInterval = currentValue
    }
    @IBAction func segmentControlChanged(_ sender: UISegmentedControl) {
        let buttonState = segmentControlButton.titleForSegment(at: segmentControlButton.selectedSegmentIndex)
        
        switch buttonState {
        case "Time":
            logLabelText.text = "Logging Interval (seconds)"
            logIntervalSlider.minimumValue = 1
            logIntervalSlider.maximumValue = 600
            logMethod = "Time"
        case "Distance":
            logLabelText.text = "Logging Distance (meters)"
            logIntervalSlider.minimumValue = 2
            logIntervalSlider.maximumValue = 1000
            logMethod = "Distance"
        default:
            print("No state detected")
        }
        
    }
}
