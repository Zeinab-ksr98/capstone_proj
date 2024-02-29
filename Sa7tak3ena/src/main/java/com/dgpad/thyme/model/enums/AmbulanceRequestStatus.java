package com.dgpad.thyme.model.enums;

public enum AmbulanceRequestStatus {
    PENDING,
    Deleted,
    ACCEPTED,
    REJECTED,
    Done;
}
//pending is at beginning
//deleted the request had been for more than 30 min without acceptance
//Accepted by Ambulance within 30 min from pending
//Rejected by Ambulance within 30 min from pending
//all requests are archived after 24 hrs
