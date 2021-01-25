
#  ScriptManager
<p>
  <img src="https://raw.githubusercontent.com/ReleaseStandard/ScriptManager/master/app/src/main/res/mipmap-xxxhdpi/logo2.png">
</p>
Open source application to manage, launch and schedule your sh scripts.<br />
You can view it has cron schedulder of sh scripts.<br />
<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" height="75" />
<div style="display: flex;flex-direction: column;">
 <img src="https://raw.githubusercontent.com/ReleaseStandard/ScriptManager/master/fastlane/metadata/android/en-US/images/phoneScreenshots/1.jpg" width="30%" />
 <img src="https://raw.githubusercontent.com/ReleaseStandard/ScriptManager/master/fastlane/metadata/android/en-US/images/phoneScreenshots/2.jpg" width="30%" />
 <img src="https://raw.githubusercontent.com/ReleaseStandard/ScriptManager/master/fastlane/metadata/android/en-US/images/phoneScreenshots/3.jpg" width="30%" />
</div>

## Basic usage
Press "+" to create a new job.<br />
Longpress the new job and overflow menu > Edit (script your script).<br />
Then click play to launch the script.<br />
Check the result by long press then View log.<br />


## Project motivations
This project is inspired by [SL4A](https://en.wikipedia.org/wiki/Scripting_Layer_for_Android). <br />
The goal is to offert same capabilities on the device (ex: write and send SMS with sh line), <br />
throught an sh API.<br />


## Current state of the project
- [X] No root required.<br />
- [X] You can schedule and repeat jobs.<br />
- [X] Schedulded jobs persist accross reboot.<br />
- [X] Clear & Show jobs log.<br />
- [X] Import sh scripts.<br />
- [ ] SH API<br />


## Details
input date format is :<br />
```bash
.---------------- minute (0 - 59)
|  .------------- hour (0 - 23)
|  |  .---------- day of month (1 - 31)
|  |  |  .------- month (1 - 12) OR jan,feb,mar,apr ...
|  |  |  |  .---- year
|  |  |  |  |
*  *  *  *  * 
```
e.g.<br />
```bash
1 2 3 4 2021
```
for 02:01 on 03/04 of 2021.<br />
```bash
* * * * *
```
run job each minute.<br />

## Donation
```bash
https://paypal.me/ReleaseStandard
```
