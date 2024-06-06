# Overviiew:
The Lebanese healthcare system faces challenges due to limited resources, particularly in meeting the increasing demand for intensive and neonatal care units. This lack of resources presents significant challenges for patients in locating vacant beds nearby and for ambulance workflow. As The population grows, providing specialized care, especially during cases demanding intensive medical intervention becomes inaptitude. The current random call approach for finding vacant hospitals further complicates the situation.
Sa7tak3ena seeks to revolutionize the healthcare system via hospital-patient connections, enhance transparency concerning hospital resources, and introduce automated procedures to minimize time spent on locating beds in emergency cases. This system aims to facilitate inter-hospital transfers, and the process of localization and reservation of the Intensive Care Unit (ICU), Neonatal Intensive Care Unit (NICU), and emergency beds by providing real-time hospital capacity information and an online reservation system.
Our proposed solution involves grouping all different Lebanese ambulance agencies and hospitals in a centralized system to manage and ease their work, providing real-time information on ambulance vehicles and hospital bed availability near the patient's location. This system allows hospitals to manage their bed vacancies, sections, and contact information, while ambulances can manage paramedics and cars. It reduces crowded emergencies and ensures optimal use of resources, replacing an ambulance random call approach.
The system would allow patients to request bed reservations according to their needs, make their hospital-to-hospital transfer process easier, and allow ambulance services to be converted to hospital transfers if required. This would optimize the healthcare system's responsiveness during emergencies, manage hospital and ambulance work more effectively, and enhance patient care and outcomes.
# Project goal:
Finding the Nearest vacant ambulance, the Ambulance's random transfer strategy, the struggle to access real-time hospital vacancy data, and the constant demand for emergency, ICU, and NICU beds complicate Lebanese healthcare services and threaten patients’ lives. Despite these challenges, developing a site where ambulances, hospitals, and patients can communicate makes healthcare more efficient, reduces patient wait times, and cuts ambulance diversion time. 
A centralized system that manages and facilitates hospital and ambulance work and provides real-time information on ambulance vehicles and hospital bed availability near the patient's location. A system in which hospitals can manage their bed vacancies, sections, and contact info while ambulances can manage paramedics and cars, as well as manage their reservations and streamline the connection between them by hospital benefiting from ambulance transfer service to transfer their patients safely from and to their hospital. Ambulances make informed decisions when transferring a patient and directly reserve a bed in an emergency section at the most suitable hospital.
Our system would allow patients needing intensive care  to request bed reservations according to their needs (NICU, ICU, or emergency) and make their hospital-to-hospital transfer process easier. It also allows the patient to request an ambulance service which the process begins as a home service and can be then converted to hospital transfer if the case needs it. This can be easily done by ambulance paramedics after examining the patient. 
By achieving that, we can optimize the healthcare system's responsiveness during emergencies, manage hospital and ambulance work, allocate and benefit from resources (ICU, NICU) more effectively, reduce crowded emergency sections, and ultimately enhance patient care and outcomes.

# Used Technologies:
A combination of technologies was used to create a functional and responsive website that maintains the user’s needs. Starting with the backend part, we used the Spring boot framework to integrate a functional core for the application. Java language was used in Spring Boot, and MySQL was used to implement the database. On top of these core tools, Jsoup scrapes data from the MOHP site, docker sends WhatsApp SMS to patients, Twilio Send Grid sends emails, and Stripe facilitates the donation feature. 
To achieve a user-friendly site we intended to use HTML, CSS, and Bootstrap to wrap it up in a proper attractive way. As well as, charts.js to represent some analytics for some users. Obtaining an accessible website is a must, so we deploy the website using Google Cloud
# Capstone Requirements:
- Hospital, patient, ambulance, and Admin shall be able to log in by username or email, and password
- Hospital, patient, ambulance, and Admin shall be able to log out 
- Hospital, patient, ambulance, and Admin shall be able to reset their passwords
- Hospital, patient, ambulance, and Admin shall be able to modify their passwords through forgot password
- The patient shall verify his account by filling in his first name, last name, gender, age, and nationality, and uploading a national ID or passport.
-  The patient and ambulances shall be able to view all hospital name
- The patients and ambulance shall be able to filter hospitals by district and sections 
- Patients and ambulance shall be able to view hospital contact information (phone number, location, manager name, manager phone, Supervising Physician Name, and Supervising Physician Phone)
- The patient and ambulance shall be able to view the hospital's sections available
- Patients and ambulance shall be able to view hospital beds and their vacancies (type and amount)
- Patients and hospitals can view their  history of bed reservations
- Patients shall be able to send a request to either transfer to another hospital, or emergency, reserve an ICU or NICU bed for all hospitals they select
- Patient shall upload doctor report instead of medical description when requesting NICU orICU bed reservation 
- The patient shall be notified with the request update (accepted, rejected) using WhatsApp sms
- Patient  shall be able to view their hospital bed reservations
- Patient can request a home ambulance service  by entering medical description and location 
- The patient can request an ambulance service using the emergency call button 
- The patient shall confirm the request within 30 minutes of hospital acceptance
- Patients and hospitals can view the contact information of the ambulance 
- Patient shall be able to donate money to ambulances through stripe
- The patient shall verify his phone number using the WhatsApp verification code while creating the account
- The hospital shall be able to view the requests sent for him
- The hospital shall be able to reject a patient’s request
- The hospital shall be able to accept a patient's request 
- The hospital shall be able to provide ambulance type if needed while accepting patients' request
- Hospitals and ambulances shall be able to edit their address by GPS location(longitude and latitude ) extraction
- The hospital shall be able to edit his profile by modifying/adding a user name, email, phone number, address, public name, manager name, manager phone, Supervising Physician Name, and Supervising Physician Phone
- The hospital shall be able to update reservation details by adding the bed and floor number
- Hospital shall be able to view his section
- Hospitals shall be able to edit his sections by modifying its name 
- Hospital shall be able to delete a section 
- Hospital shall be able to view his available beds
- Hospital shall be able to request new bed category to be created by admin
- hospital shall be able to create beds with categories by entering the category and number of beds 
- hospital shall be able to update vacancy of a bed category by modifying the number of beds available
- Hospitals shall be able to view their patient's list
- hospital shall be able to delete a bed category 
- Hospital can request transfer ambulance service to transfer by specifying patient's phone number, name, and address, from or to it,ambulance type needed ,and recommendations
- The administrative ambulance shall be able to create a sub-branch account with the same agency name 
- Administrative ambulances shall be able to view the branch account’s details, total reservations, paramedics, cars, and status.
- Administrative ambulance shall be able to manage branch accounts by deactivating and activating them
- The ambulance shall be able to accept a request
- The ambulance shall be able to reject a request
- Ambulance shall be able to modify his profile by modifying his user name, phone, and public name.
- Ambulances shall be able to manage their cars by adding a car, editing its status, deleting it 
- An ambulance request is sent after bed reservation if the patient needs one
- Ambulance shall be able to view his paramedics
- Ambulance shall be able to create a paramedic by adding his name,phone, image.
- Ambulance shall be able to modify paramedic's name and phone
- ambulance shall be able to delete a paramedic
- Ambulance shall be able to archive their non-append requests and indoor services by creating an ambulance request 
- Ambulance shall be able to modify request details by modifying service type, paramedic, description, equipment and reserving a car.
- Ambulance shall be able to reserve an emergency bed for each patient's requests
- Ambulance shall be able to view, edit, and delete monthly equipment audits
- Guests shall be able to request to have a hospital or ambulance account
- Admin shall be able to view the requested account and bed category 
- Admin shall be able to manage bed categories by viewing them and adding new ones (by providing their names)
- Admin shall be able to create hospital, admin and ambulance accounts
- Admin shall be able to edit user’s details by modifying username, email,phone
- Admin shall be able to send a customized email to a hospital or ambulance
- Admin can block, enable, and disable hospital, ambulance, and patient account
- The admin shall be able to view hospitals created upon scraping the MOPH site within users to check the authenticity of the hospital’s account request  
- Hospitals can view their rank among other active hospitals
- Hospitals can view other hospital total reservations and sections
- Admin, patient, and ambulance shall be able to view analytics
# Constrains
- A Request becomes a reservation when the patient confirms the accepted request
- The patient can cancel a bed request of reservation before ambulance acceptance by the hospital
- The patient shall confirm the accepted request within 30 minutes to secure the reserved
- GPS is extracted when submitting ambulance and hospital requests by the patient
- Ambulance Transfer service request made by a hospital  is dispatched according to the car type needed and availability as well as according to the “from region”(hospital region in case of from and patient region in case of to hospital) 
- Transfer requests become region_based only when the provided ambulance type doesn't exist
- The patient can send a request only after verifying their account.
- Ambulance requests and hospital reservations after 24 hours are archived in history 
- Ambulances and hospitals shall confirm requests within 10 minutes; otherwise, they will be automatically canceled
- An automatic email is sent to the ambulance and hospital upon receiving a new request
- An automatic email is sent upon activating and deactivating their account by admin 
- An automatic WhatsApp SMS is sent to the patient number upon accepting a request
- The system uses MySQL as a database management system




