package com.example.repository;

import com.example.model.UserElectrode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserElectrodeRepository extends JpaRepository<UserElectrode, Long> {

    UserElectrode findByUsernameAndElectrodeNumber(String username, int electrodeNumber);
}
