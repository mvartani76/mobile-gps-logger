//
//  BaseTabBarController.swift
//  gps-logger
//
//  Created by Michael Vartanian on 8/20/18.
//  Copyright © 2018 Michael Vartanian. All rights reserved.
//

import UIKit

class BaseTabBarController: UITabBarController {

    var logInterval: Int = 300
    var logFormat: String = "gpx"
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
