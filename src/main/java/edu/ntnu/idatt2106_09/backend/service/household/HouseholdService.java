package edu.ntnu.idatt2106_09.backend.service.household;

import edu.ntnu.idatt2106_09.backend.dto.HouseholdDto;
import edu.ntnu.idatt2106_09.backend.dto.UserDto;
import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.repository.HouseholdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public interface HouseholdService {

    public Optional<Household> getHouseholdById(Long householdId);
    public Optional<HouseholdDto> getHouseholdByIdAsDto(Long householdId) ;
    public ResponseEntity<HouseholdDto> getHouseholdByUserId(Integer userId);
    public ResponseEntity<UserDto> addUserToHousehold(Long householdId, UserDto userDto);
    public ResponseEntity<HouseholdDto> createHousehold(Integer userId, HouseholdDto householdDto);
    public ResponseEntity<HouseholdDto> updateHousehold(Long householdId, HouseholdDto householdDto);
    public Set<UserDto> getAllUsersInHousehold(Long householdId);
}
