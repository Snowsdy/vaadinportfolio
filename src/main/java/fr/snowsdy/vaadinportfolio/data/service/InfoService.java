package fr.snowsdy.vaadinportfolio.data.service;

import fr.snowsdy.vaadinportfolio.data.entity.Info;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InfoService {

    private final InfoRepository repository;

    @Autowired
    public InfoService(InfoRepository repository) {
        this.repository = repository;
    }

    public Optional<Info> get(UUID id) {
        return repository.findById(id);
    }

    public Info update(Info entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Info> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
