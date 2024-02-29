package com.dgpad.thyme.model.enums;

public enum ReservationStatus {
    PENDING,
    Deleted,
    ACCEPTED,
    Rejected,
    CONFIRMED,
    RESERVED;
}
//pending is at beginning
//deleted the request had been for more than 24 hours without acceptance or not confirmed within 30min
//Accepted by the hospital
//confirmed by user within 30 min from acceptance
//Reserved is status for the reservation (feedback is sent to be filled by patient at this state)
//all reservations are archived after 24 hrs