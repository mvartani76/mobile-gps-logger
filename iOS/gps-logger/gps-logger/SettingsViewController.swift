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
    
    var logFormatDataSource = ["gpx", "kml"]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        logFormatPicker.delegate = self
        logFormatPicker.dataSource = self
        let currentSliderValue = Int(logIntervalSlider.value)
        logIntervalLabel.text = "\(currentSliderValue)"
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
    }
    
    @IBAction func sliderValueChanged(_ sender: UISlider) {
        let currentValue = Int(sender.value)
        logIntervalLabel.text = "\(currentValue)"
    }
    
}

