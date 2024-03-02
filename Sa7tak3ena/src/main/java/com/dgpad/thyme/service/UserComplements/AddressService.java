package com.dgpad.thyme.service.UserComplements;


import com.dgpad.thyme.model.enums.AmbulanceStatus;
import com.dgpad.thyme.model.usercomplements.Address;
import com.dgpad.thyme.model.usercomplements.AmbulanceCar;
import com.dgpad.thyme.model.users.Ambulance;
import com.dgpad.thyme.model.users.Hospital;
import com.dgpad.thyme.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public Address save(Address address){
        return addressRepository.save(address);
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }
    public void deleteAddress(Address address) {
        addressRepository.delete(address);
    }
    public Address updateAddress(long id, Address address){
        Address newaddress = getAddressById(id);
        newaddress.setLatitude(address.getLatitude());
        newaddress.setLongitude(address.getLongitude());
        return save(newaddress);

    }
//    calculate distances using Vincenty's formulae
    double SEMI_MAJOR_AXIS_MT = 6378137;
    double SEMI_MINOR_AXIS_MT = 6356752.314245;
    double FLATTENING = 1 / 298.257223563;
    double ERROR_TOLERANCE = 1e-12;

    public double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double U1 = Math.atan((1 - FLATTENING) * Math.tan(Math.toRadians(latitude1)));
        double U2 = Math.atan((1 - FLATTENING) * Math.tan(Math.toRadians(latitude2)));

        double sinU1 = Math.sin(U1);
        double cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2);
        double cosU2 = Math.cos(U2);

        double longitudeDifference = Math.toRadians(longitude2 - longitude1);
        double previousLongitudeDifference;

        double sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;

        do {
            sinSigma = Math.sqrt(Math.pow(cosU2 * Math.sin(longitudeDifference), 2) +
                    Math.pow(cosU1 * sinU2 - sinU1 * cosU2 * Math.cos(longitudeDifference), 2));
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * Math.cos(longitudeDifference);
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * Math.sin(longitudeDifference) / sinSigma;
            cosSqAlpha = 1 - Math.pow(sinAlpha, 2);
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            if (Double.isNaN(cos2SigmaM)) {
                cos2SigmaM = 0;
            }
            previousLongitudeDifference = longitudeDifference;
            double C = FLATTENING / 16 * cosSqAlpha * (4 + FLATTENING * (4 - 3 * cosSqAlpha));
            longitudeDifference = Math.toRadians(longitude2 - longitude1) + (1 - C) * FLATTENING * sinAlpha *
                    (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2))));
        } while (Math.abs(longitudeDifference - previousLongitudeDifference) > ERROR_TOLERANCE);

        double uSq = cosSqAlpha * (Math.pow(SEMI_MAJOR_AXIS_MT, 2) - Math.pow(SEMI_MINOR_AXIS_MT, 2)) / Math.pow(SEMI_MINOR_AXIS_MT, 2);

        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

        double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2))
                - B / 6 * cos2SigmaM * (-3 + 4 * Math.pow(sinSigma, 2)) * (-3 + 4 * Math.pow(cos2SigmaM, 2))));

        double distanceMt = SEMI_MINOR_AXIS_MT * A * (sigma - deltaSigma);
        return distanceMt / 1000;
    }
    public List<Hospital> sortHospitalsByDistance(Address pickupAddress, List<Hospital> hospitals) {
        List<Hospital> sortedHospitals = new ArrayList<>(hospitals);

        for (int i = 0; i < sortedHospitals.size() - 1; i++) {
            for (int j = i + 1; j < sortedHospitals.size(); j++) {
                Hospital hospital1 = sortedHospitals.get(i);
                Hospital hospital2 = sortedHospitals.get(j);

                double distance1 = calculateDistance(pickupAddress.getLatitude(), pickupAddress.getLongitude(),
                        hospital1.getAddress().getLatitude(), hospital1.getAddress().getLongitude());

                double distance2 = calculateDistance(pickupAddress.getLatitude(), pickupAddress.getLongitude(),
                        hospital2.getAddress().getLatitude(), hospital2.getAddress().getLongitude());

                if (distance1 > distance2) {
                    // Swap hospitals
                    Hospital temp = sortedHospitals.get(i);
                    sortedHospitals.set(i, sortedHospitals.get(j));
                    sortedHospitals.set(j, temp);
                }
            }
        }

        return sortedHospitals;
    }

    // Method to sort ambulances by distance from given address (after being filtered accourding to car status
    public List<Ambulance> sortAmbulancesByDistance(Address pickupAddress, List<Ambulance> Ambulances) {
        List<Ambulance> sortedAmbulances = new ArrayList<>(Ambulances);

        for (int i = 0; i < sortedAmbulances.size() - 1; i++) {
            for (int j = i + 1; j < sortedAmbulances.size(); j++) {
                Ambulance Ambulance1 = sortedAmbulances.get(i);
                Ambulance Ambulance2 = sortedAmbulances.get(j);

                double distance1 = calculateDistance(pickupAddress.getLatitude(), pickupAddress.getLongitude(),
                        Ambulance1.getAddress().getLatitude(), Ambulance1.getAddress().getLongitude());

                double distance2 = calculateDistance(pickupAddress.getLatitude(), pickupAddress.getLongitude(),
                        Ambulance2.getAddress().getLatitude(), Ambulance2.getAddress().getLongitude());

                if (distance1 > distance2) {
                    // Swap hospitals
                    Ambulance temp = sortedAmbulances.get(i);
                    sortedAmbulances.set(i, sortedAmbulances.get(j));
                    sortedAmbulances.set(j, temp);
                }
            }
        }

        return sortedAmbulances;
    }


}

