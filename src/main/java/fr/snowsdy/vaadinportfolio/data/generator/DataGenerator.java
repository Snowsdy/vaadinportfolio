package fr.snowsdy.vaadinportfolio.data.generator;

import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import fr.snowsdy.vaadinportfolio.data.Role;
import fr.snowsdy.vaadinportfolio.data.entity.Info;
import fr.snowsdy.vaadinportfolio.data.entity.User;
import fr.snowsdy.vaadinportfolio.data.service.InfoRepository;
import fr.snowsdy.vaadinportfolio.data.service.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository,
            InfoRepository infoRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setName("John Normal");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setName("Emma Powerful");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);
            logger.info("... generating 100 Info entities...");
            ExampleDataGenerator<Info> infoRepositoryGenerator = new ExampleDataGenerator<>(Info.class,
                    LocalDateTime.of(2022, 5, 31, 0, 0, 0));
            infoRepositoryGenerator.setData(Info::setTitle, DataType.BOOK_TITLE);
            infoRepositoryGenerator.setData(Info::setImagePath, DataType.BOOK_IMAGE_URL);
            infoRepositoryGenerator.setData(Info::setAddedAt, DataType.DATE_OF_BIRTH);
            infoRepositoryGenerator.setData(Info::setDescription, DataType.SENTENCE);
            infoRepositoryGenerator.setData(Info::setGithubLink, DataType.STATE);
            infoRepositoryGenerator.setData(Info::setLanguage, DataType.WORD);
            infoRepository.saveAll(infoRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}