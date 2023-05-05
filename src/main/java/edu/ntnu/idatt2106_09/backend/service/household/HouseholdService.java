package edu.ntnu.idatt2106_09.backend.service.household;

import edu.ntnu.idatt2106_09.backend.dto.HouseholdDto;
import edu.ntnu.idatt2106_09.backend.dto.HouseholdDtoForHouseholdService;
import edu.ntnu.idatt2106_09.backend.dto.UserDto;
import edu.ntnu.idatt2106_09.backend.model.Household;
import edu.ntnu.idatt2106_09.backend.repository.HouseholdRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;


@Service
public interface HouseholdService {

    public Optional<Household> getHouseholdById(Long householdId);
    public Optional<HouseholdDtoForHouseholdService> getHouseholdByIdAsDto(Long householdId) ;
    public ResponseEntity<HouseholdDtoForHouseholdService> getHouseholdByUserId(Integer userId);
    public ResponseEntity<UserDto> addUserToHousehold(Long invitationNr, Integer userId);
    public ResponseEntity<HouseholdDtoForHouseholdService> createHousehold(Integer userId, HouseholdDtoForHouseholdService householdDto);
    public ResponseEntity<HouseholdDtoForHouseholdService> updateHousehold(Long householdId, HouseholdDtoForHouseholdService householdDto);
    public Set<UserDto> getAllUsersInHousehold(Long householdId);
}
