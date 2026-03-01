package ca.biglabs.nosqlcrud.repository;

import ca.biglabs.nosqlcrud.dto.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
