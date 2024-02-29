# Capstone Requirements:
- Patient can sign up
- Admin creates Hospital ,Ambulance
- Admin,Patient, Hospital ,Ambulance login by username and password
- reset passward
- forget passward
- Hospital ,Ambulance edit profile
- Patient varify his account by copleting profile details ,upload id,upload a selfie
- Patient send request to list of hospitals he choose  (if verified)
- Hospital accept the request
- hospital can reject a request
- Patient confirm the one of the acceptence to become a reservation
- Patient can view history reservation
- Patient can view history request
- request must be deleted after 24 hr if not confirmed or accepted
- Pient give his feedback on the reservation (when it's done)
- Patient give rating for the reservation(which represent rate for hospital)


this work is dedicated to السيدة فاطمة الزهراء
Data Models

-> User (): ID (UUID) ; Username, Password, Email, Phone, Role (ENUM),deleted,enabled <br>
  --> Patient:id,fname,lname,identitycardImage,passport,age,gender,nationality,insurance, List<Reservations>, List<Requests> <br>
	--->gender,nationality,insurance: enums <br>
  --> Ambulance:id,Agency,<ambulanceType>, List<Reservations>(the one that shows that the user have confirmation), List<AmbularceRequests>(his own requests) <br>
	--->Agency: enum (هيئة، دفاع مدني ...) <br>
	--->ambulanceType: name(انواع الاسعافات المتوفرة لديهم) <br>
  --> Hospital:id,address,managername,managernum, supervisingnum,supervisingname,block, List<Reservations> , List<Requests>,List<ambulanceRequests> <br>
	--->Block:<Patients> <br>
->Request:id,ReservationType , patient, hospital, ambulanceNeeded (boolean),prefferedAmbulences, AmbulanceType (By Hospital) , Reservationstatus, medicalRecord, CreatedAt <br>		
	-->ReservationType:enum(nicu,icu,..) <br>
	-->prefferedAmbulences:List<AmbulncesAgency> <br>
	-->Reservation status:pending is at beginning <br>
				deleted the request had been for more than 24 hours without acceptance or not confirmed within 30min <br>
				Accepted by the hospital <br>
				confirmed by user within 30 min from acceptance <br>
				Reserved is status for the reservation (feedback is sent to be filled by patient at this state) <br>
				all reservations are archived after 24 hrs <br>
->reservation:id,ReservationType , patient, hospital, ambulanceNeeded (boolean),prefferedAmbulences, AmbulanceType,ambulanceRequests , Reservationstatus, medicalRecord,bednum,floornum, CreatedAt <br>
->AmbulanceRequest:id,from,to,SmbulanceStatus,ambulance,hospital <br>

Pateint -> has a surgery (SRH, SG, HH) -> each request has a  [ ReservationType , patient, hospital,  ambulanceNeeded (boolean), List<Ambulnces> prefferedAmbulences, AmbulanceType (By Hospital) , status(In procees/ Confirmed / Done), medicalRecord, CreatedAt,  (example : 30 min if confirmed cancel other requests) ]		<br>	

	 request SRH (R1) -> Save to database (approved) <br>
	 request SG  (R2) -> save to database  (refused) <br>
	 request HH  (R3) -> save to database  (approved) => patient chose this =>  make this request a reservation <br>

-> Reservations  each reservation has a [ ReservationType, patient, Hospital, AcceptanceInfo, ambulanceNeeded (boolean) ,  Ambulance] <br>
	-> AcceptanceInfo:  bedNumber, FloorNumber <br>

--------------------------------------------------------------------------------------------------------------------------------------------------------------
scanrios: <br>

Hospital SRH<br>
	R1 -> Accepted => change the status of request 1<br>
Hospital SG <br>
	R2 -> refused  <br>
Hospital HH <br>
	R3 -> approved -> approved By User -> will appear in reservations <br> 

patient make a request (checked need ambulance ) --> send to hospitals -> a hospital Approves -> needs confitmation in n min -> user confirms -> send requests to ambulnces (According to user prefferences and Ambulance type specified by the hospital)<br>
																					
