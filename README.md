# Sa7tak3ena
## Overview
The Lebanese healthcare system faces challenges due to limited resources, particularly in meeting the increasing demand for intensive and neonatal care units. This lack of resources presents significant challenges for patients in locating vacant beds nearby and for ambulance workflow. As the population grows, providing specialized care, especially during cases demanding intensive medical intervention, becomes inadequate. The current random call approach for finding vacant hospitals further complicates the situation.

🚑 **Sa7tak3ena** seeks to revolutionize the healthcare system via hospital-patient connections, enhance transparency concerning hospital resources, and introduce automated procedures to minimize time spent on locating beds in emergency cases. This system aims to facilitate inter-hospital transfers, and the process of localization and reservation of the Intensive Care Unit (ICU), Neonatal Intensive Care Unit (NICU), and emergency beds by providing real-time hospital capacity information and an online reservation system.

Our proposed solution involves grouping all different Lebanese ambulance agencies and hospitals in a centralized system to manage and ease their work, providing real-time information on ambulance vehicles and hospital bed availability near the patient's location. This system allows hospitals to manage their bed vacancies, sections, and contact information, while ambulances can manage paramedics and cars. It reduces crowded emergencies and ensures optimal use of resources, replacing an ambulance random call approach.

The system would allow patients to request bed reservations according to their needs, make their hospital-to-hospital transfer process easier, and allow ambulance services to be converted to hospital transfers if required. This would optimize the healthcare system's responsiveness during emergencies, manage hospital and ambulance work more effectively, and enhance patient care and outcomes.

## 🎯Project Goal

Finding the nearest vacant ambulance, the ambulance's random transfer strategy, the struggle to access real-time hospital vacancy data, and the constant demand for emergency, ICU, and NICU beds complicate Lebanese healthcare services and threaten patients’ lives. Developing a site where ambulances, hospitals, and patients can communicate makes healthcare more efficient, reduces patient wait times, and cuts ambulance diversion time.

A centralized system manages and facilitates hospital and ambulance work, providing real-time information on ambulance vehicles and hospital bed availability near the patient's location. Hospitals manage their bed vacancies, sections, and contact info, while ambulances manage paramedics, cars, and reservations. This optimizes the healthcare system's responsiveness during emergencies, manages hospital and ambulance work effectively, and enhances patient care and outcomes.

## 💻Used Technologies

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-%236DB33F.svg?style=flat&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=flat&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=flat&logo=mysql&logoColor=white)
![Jsoup](https://img.shields.io/badge/Jsoup-%23363636.svg?style=flat&logo=jsoup&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=flat&logo=docker&logoColor=white)
![Twilio](https://img.shields.io/badge/Twilio-F22F46.svg?style=flat&logo=Twilio&logoColor=white)
![Stripe](https://img.shields.io/badge/Stripe-%23222F3E.svg?style=flat&logo=stripe&logoColor=white)
![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=flat&logo=html5&logoColor=white)
![Bootstrap](https://img.shields.io/badge/bootstrap-%238511FA.svg?style=flat&logo=bootstrap&logoColor=white)
![Chart.js](https://img.shields.io/badge/chart.js-F5788D.svg?style=flat&logo=chart.js&logoColor=white)
![Google Cloud](https://img.shields.io/badge/google%20cloud-%234285F4.svg?style=flat&logo=google-cloud&logoColor=white)


## 📝 Capstone Requirements

### User Authentication and Management
- **Login/Logout**: 
  - Users (Hospital, Patient, Ambulance, Admin) can log in and out using username/email and password.
- **Password Management**: 
  - Users can reset, modify, and recover their passwords.
- **Account Verification**: 
  - Patients must verify their phone number using WhatsApp.
  - Patients must provide personal details (first name, last name, gender, age, nationality) and upload ID/passport.

### Patient Features
- **Hospital Interaction**: 
  - View all hospital names.
  - Filter hospitals by district and sections.
  - View hospital contact information (phone number, location, manager name, manager phone, Supervising Physician Name, Supervising Physician Phone).
  - View hospital sections and bed availability (type and amount).
- **Requests**: 
  - Make transfer requests to another hospital, request emergency services, or reserve ICU/NICU beds. Upload a doctor report for NICU/ICU bed reservations.
  - Confirm requests within 30 minutes of hospital acceptance.
  - Request home ambulance service by entering medical description and location.
  - Use emergency call button for immediate ambulance service.
- **Notifications**: 
  - Receive updates via WhatsApp SMS for request status (accepted, rejected).
- **Donations**: 
  - Donate money to ambulances through Stripe.
- **History**: 
  - View reservation history and current bed reservations.

### Hospital Features
- **Request Management**: 
  - View, accept, and reject patient requests.
  - Provide ambulance type if needed while accepting patient requests.
- **Profile Management**: 
  - Edit profile details (user name, email, phone number, address, public name, manager name, manager phone, Supervising Physician Name, Supervising Physician Phone).
- **Bed Management**: 
  - View available beds.
  - Request new bed categories from admin.
  - Create beds with categories by entering category and number of beds.
  - Update vacancy of bed categories.
  - Delete bed categories.
- **Section Management**: 
  - View and edit sections (modify name, delete sections).
- **Transfer Service**: 
  - Request transfer ambulance service by specifying patient's details, ambulance type needed, and recommendations.
- **Analytics**: 
  - View rankings among other active hospitals.
  - View total reservations and sections of other hospitals.

### Ambulance Features
- **Request Management**: 
  - Accept or reject requests.
  - Archive non-append requests and indoor services by creating an ambulance request.
  - Reserve emergency beds for patient requests.
- **Profile Management**: 
  - Edit profile details (user name, phone, public name).
- **Car Management**: 
  - Manage cars (add, edit status, delete).
- **Paramedic Management**: 
  - Create, modify, and delete paramedics.
- **Request Details**: 
  - Modify request details (service type, paramedic, description, equipment, reserve a car).
- **Equipment Management**: 
  - View, edit, and delete monthly equipment audits.

### Admin Features
- **Account Management**: 
  - View and manage requested hospital and ambulance accounts.
  - Create, edit, and block accounts.
  - Manage account status (activate/deactivate).
- **Bed Management**: 
  - Manage bed categories (view, add, create).
- **Notifications**: 
  - Send customized emails to hospitals and ambulances.
  - Automatic emails for account activation/deactivation.
- **Verification**: 
  - Scrape MOPH site to verify hospital authenticity.
- **Analytics**: 
  - View analytics for hospitals and ambulances.
  - Create sub-branch accounts for ambulances.
  - View and manage branch details (total reservations, paramedics, cars, status).

## Constraints
- **Request to Reservation**: 
  - A request becomes a reservation upon patient confirmation.
  - Requests can be canceled before hospital acceptance.
  - Requests must be confirmed within 30 minutes to secure the reservation.
- **Automatic Notifications**: 
  - Automatic emails and WhatsApp messages are sent upon receiving new requests, activating/deactivating accounts, and request updates.
- **GPS Extraction**: 
  - Extract GPS location when submitting ambulance and hospital requests.
- **Archiving**: 
  - Requests and services are archived after 24 hours.
- **Regional Transfer Service**: 
  - Transfer service requests are dispatched based on car type and availability, and regional constraints.

## Notes
- **Constraints**: 
  - Ambulance Transfer service requests are dispatched according to car type availability and region-based requirements.
  - The patient can only send requests after verifying their account.
  - Requests and services not acted upon within given time frames will be automatically canceled or archived.

---
