package dev.mottolab.storeapi.dto.response.shipping;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.mottolab.storeapi.dto.response.tracking.TrackingDTO;
import dev.mottolab.storeapi.entity.ShippingEntity;
import dev.mottolab.storeapi.entity.ShippingTrackerEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShippingInfoDTO {
    @JsonProperty("id")
    private int id;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("address_line_1")
    private String addressLine1;
    @JsonProperty("address_line_2")
    private String addressLine2;
    @JsonProperty("sub_district")
    private String subDistrict;
    @JsonProperty("district")
    private String district;
    @JsonProperty("province")
    private String province;
    @JsonProperty("zip_code")
    private Integer zipCode;
    @JsonProperty("shipping_rate")
    private ShippingRateDTO shippingRateDTO = null;
    @JsonProperty("tracking")
    private TrackingDTO tracking;

    public ShippingInfoDTO(
            ShippingEntity entity,
            List<ShippingTrackerEntity> shippingTracker
    ) {
        this.id = entity.getId();
        this.fullName = entity.getFullName();
        this.addressLine1 = entity.getLine1();
        this.addressLine2 = entity.getLine2();
        this.subDistrict = entity.getAddress().getNameTH();
        this.district = entity.getAddress().getDistrict().getNameTH();
        this.province = entity.getAddress().getDistrict().getProvince().getNameTH();
        this.zipCode = entity.getAddress().getZipCode();
        if(entity.getShippingRate() != null){
            this.shippingRateDTO = new ShippingRateDTO(entity.getShippingRate());
        }
        if(entity.getTrackingNumber() != null){
            this.tracking = new TrackingDTO(
                    entity,
                    shippingTracker
            );
        }
    }
}