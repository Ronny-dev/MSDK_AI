# MSDK_AI
Base on DJI-MSDKV5, use Tensorflow-lite for AI visual recognition.

## 1. What is MSDK V5 for DJI
DJI Mobile SDK V5 has a series of APIs to control the software and hardware interfaces of an aircraft. We provide an open source production sample and a tutorial for developers to develop a more competitive drone solution on mobile device. This improves the experience and efficiency of MSDK App development.

**Highlights: Only Supported  [MATRICE 300 RTK] [MATRICE 30 SERIES]**

## 2. Register as a DJI Developer

Register for a DJI Developer account [here](https://developer.dji.com/).

During the registration process, email information and a credit card or phone number will need to be supplied to verify the registration. Any credit card information given will only be used for verification and will not be charged.

This guide assumes you are using Android Studio Bumblebee 2021.1.1 and above.

## 3. Generate an App Key

Every application needs a unique App Key to initialize the SDK.

To create an App Key for an application:

Go to the DJI developer Developer Center

Select the "Apps" tab on the left.
Select the "Create App" button on the right.
Enter the APP Name, Software Platform, Package Name, Category, and Description.
An application activation email will be sent to complete App Key generation.
The App Key will appear in the developer center, and can be copied and pasted into the application.

## 4. Run the MSDK-AI Sample App

The application uses the TensorFlow-Lite official sample model. Developer can import models by AndroidStudio plugins.

## 5.TODO

Program has completed basic modules of dji-aircraft SDK, but the development of AI has not been completed. Users can develop visual recognition functions in the Decoder method of MainActivity.

 
