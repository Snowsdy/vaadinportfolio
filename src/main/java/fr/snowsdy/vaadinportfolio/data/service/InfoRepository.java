package fr.snowsdy.vaadinportfolio.data.service;

import fr.snowsdy.vaadinportfolio.data.entity.Info;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoRepository extends JpaRepository<Info, UUID> {

}