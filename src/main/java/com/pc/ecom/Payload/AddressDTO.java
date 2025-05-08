package com.pc.ecom.Payload;

import com.pc.ecom.Model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
   private Long addressId;
   private List<User> users;
   private String street;
   private String buildingName;
   private String city;
   private String country;
   private String zipcode;
}
