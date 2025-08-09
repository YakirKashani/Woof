# Woof

**WOOF** is an **IoT-integrated mobile application** designed to monitor dogs' health, location, and daily activity in real time.  
The system combines **smart hardware sensors**, **cloud-based data storage**, and a **user-friendly Android app** to provide dog owners with actionable insights and peace of mind.

---

## Features

- **Live GPS Tracking**: Google Maps view with *Last Seen* location.
- **Dashboards**: Drinking and eating live ammount.
- **Multi-Dog**: Manage multiple dogs per user.
- **Social**: Post feed, comments, and likes (optional/modular).
- **Weight Comparison Scale**: Visual scale comparing the dog's current weight to the desired healthy weight range.
- **Vaccination & Medication Log**: Track the dog's vaccination schedule and medication history.
- **Friends & Following**: Add other owners as friends and follow their dogs' activity.
 
## Usage

1. **Login or Sign Up**
   - Open the app and log in with your existing account, or sign up.
 
2. **Add Your Dog(s)**
   - Create a profile for each dog, including name, breed, age, weight, and photo.

3. **Engage with the Community**
   - Add friends, follow other owners, and see their dogs' activities.
   - Post updates, share images, like, and comment on other users’ posts.

4. **Monitor Health Metrics**
   - Access the Medical Dashboard to view:
     1.  Food and water live amounts.
     2.   Your dog's *Last Seen* location
     3.   Record vaccination dates and medication schedules.
 
 5. **Manage Multiple Dogs**
    - Easily switch between profiles if you have more than one dog.

## Technologies used

### **Mobile App**
- **Language**: Java, XML
- **IDE**: Android Studio
- **Maps & Location**: Google Maps SDK for Android, Google Play Services Location API
- **Cloud Storage**: Cloudinary
- **Networking**: Retrofit2 + Gson
 
### **IoT Integration**
- **Maduino Zero 4G LTE (SIM7600E + GPS)** – GPS location tracking
- **ESP32 + HX711** – Food and water weight measurement
- **Communication Protocols**: Wi-Fi (ESP32), LTE (Maduino Zero), HTTP, JSON
- **Data Flow**: Firebase Realtime Database as the integration layer
- **IoT Firmware Repository**: https://github.com/NitzanLevi8/Woof-Sesors
 
### **Backend (Optional Integration)**
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Hosting**: Render
- **API**: RESTful Endpoints
