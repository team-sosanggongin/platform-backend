package com.platform.sosangongin.domains.business.location;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessLocation {

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "attendance_radius")
    private Integer attendanceRadius = 100; // 출퇴근 인정 반경 (미터)

    @Builder
    public BusinessLocation(String address, String addressDetail, String zipCode,
                            Double latitude, Double longitude, Integer attendanceRadius) {
        this.address = address;
        this.addressDetail = addressDetail;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.attendanceRadius = attendanceRadius != null ? attendanceRadius : 100;
    }

    /**
     * 주어진 좌표가 출퇴근 인정 반경 내에 있는지 판정
     */
    public boolean isWithinAttendanceRadius(double lat, double lng) {
        if (this.latitude == null || this.longitude == null) {
            return false;
        }
        double distance = calculateDistance(lat, lng);
        return distance <= this.attendanceRadius;
    }

    /**
     * Haversine 공식으로 두 좌표 간 거리 계산 (미터)
     */
    private double calculateDistance(double lat, double lng) {
        final int EARTH_RADIUS = 6_371_000; // 미터

        double dLat = Math.toRadians(lat - this.latitude);
        double dLng = Math.toRadians(lng - this.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(lat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
